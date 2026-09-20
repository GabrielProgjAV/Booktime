package com.example.booktime.tadeo.views

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.components.ChatBottomSheet
import com.example.booktime.tadeo.data.model.Book
import com.example.booktime.tadeo.data.repository.GoogleBooksRepository
import com.example.booktime.tadeo.ui.theme.PrincipalMenu
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.booktime.tadeo.data.utils.PdfTextExtractor

import java.io.File
import java.io.FileOutputStream
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfReaderView(
    bookId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { GoogleBooksRepository() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    var book by remember { mutableStateOf<Book?>(null) }
    var pdfPages by remember { mutableStateOf<List<Bitmap>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showChat by remember { mutableStateOf(false) }
    var pdfContext by remember { mutableStateOf("") }

    // Libera la memoria de los bitmaps de las páginas al salir de la pantalla.
    val currentPdfPages = rememberUpdatedState(pdfPages)
    DisposableEffect(Unit) {
        onDispose {
            currentPdfPages.value.forEach { bitmap ->
                if (!bitmap.isRecycled) {
                    bitmap.recycle()
                }
            }
        }
    }

    LaunchedEffect(bookId, userId) {
        if (userId != null) {
            val library = repository.getUserLibrary(context, userId)
            book = library.find { it.id == bookId }

            book?.let { b ->

                val isGooglePreview =
                    isGoogleBooksUrl(b.fileUri)

                val isRemotePdf =
                    b.fileUri.contains(".pdf") ||
                            b.fileUri.contains("firebasestorage")

                when {

                    isGooglePreview -> {
                        isLoading = false
                    }

                    isRemotePdf -> {

                        downloadAndLoadPdf(
                            context,
                            b.fileUri
                        ) { pages, text, error ->

                            pdfPages = pages
                            pdfContext = text
                            errorMessage = error
                            isLoading = false
                        }
                    }

                    b.fileUri.isNotEmpty() -> {

                        loadPdfPages(
                            context,
                            b.fileUri.toUri()
                        ) { pages, error ->

                            pdfPages = pages

                            pdfContext =
                                PdfTextExtractor.extractText(
                                    context,
                                    b.fileUri.toUri()
                                )

                            errorMessage = error
                            isLoading = false
                        }
                    }

                    else -> {

                        errorMessage =
                            "Este libro no tiene vista previa disponible"

                        isLoading = false
                    }
                }

            } ?: run {

                errorMessage = "Libro no encontrado en tu biblioteca"
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        book?.title ?: "Lector",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showChat = !showChat }) {
                        Image(
                            painter = painterResource(id = R.drawable.timo),
                            contentDescription = "Chat con IA",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrincipalMenu
                )
            )
        },
        containerColor = Color.Black
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                val isGooglePreview =
                    book?.fileUri?.let { isGoogleBooksUrl(it) } == true

                if (isGooglePreview) {
                    GoogleBooksWebView(url = book!!.fileUri)
                } else {
                    val listState = rememberLazyListState()
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(pdfPages) { bitmap ->
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(),
                                contentScale = ContentScale.FillWidth
                            )
                        }
                    }
                }
            }

            // Capa del Chat de IA
            if (showChat) {
                ChatBottomSheet(
                    bookId = book?.id ?: bookId,
                    bookTitle = book?.title ?: "Libro",
                    bookAuthor = book?.author ?: "Autor desconocido",
                    bookDescription = book?.description ?: "Sin descripción",
                    pdfContext = pdfContext,
                    onClose = { showChat = false }

                    
                )
            }
        }
    }
}

/**
 * Valida de forma estricta si una URL pertenece a Google (host exacto o subdominio),
 * en lugar de un simple "contains" que podría coincidir con cualquier URL que
 * contenga la cadena "google.com" en cualquier parte (por ejemplo, en un parámetro).
 */
private fun isGoogleBooksUrl(fileUri: String): Boolean {
    return try {
        val host = Uri.parse(fileUri).host ?: return false
        host.equals("google.com", ignoreCase = true) ||
            host.endsWith(".google.com", ignoreCase = true)
    } catch (e: Exception) {
        false
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun GoogleBooksWebView(url: String) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true // Ayuda a cargar visores pesados
                webViewClient = WebViewClient()
                loadUrl(url)
            }
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

private suspend fun downloadAndLoadPdf(
    context: android.content.Context,
    url: String,
    onResult: (List<Bitmap>, String, String?) -> Unit
) = withContext(Dispatchers.IO) {
    try {
        val file = File(context.cacheDir, "temp_reading_book.pdf")
        URL(url).openStream().use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        
        val uri = Uri.fromFile(file)
        var text = ""
        var pagesList = emptyList<Bitmap>()
        
        loadPdfPages(context, uri) { pages, error ->
            if (error == null) {
                pagesList = pages
                text = PdfTextExtractor.extractText(context, uri)
            }
        }
        
        withContext(Dispatchers.Main) {
            onResult(pagesList, text, null)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onResult(emptyList(), "", "Error al descargar el libro: ${e.localizedMessage}")
        }
    }
}

private suspend fun loadPdfPages(
    context: android.content.Context,
    uri: Uri,
    onResult: (List<Bitmap>, String?) -> Unit
) = withContext(Dispatchers.IO) {
    val bitmaps = mutableListOf<Bitmap>()
    try {

        try {
            context.contentResolver.takePersistableUriPermission(
                uri,
                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        } catch (e: Exception) {
            Log.w("PdfReaderView", "No se pudo tomar el permiso persistente del URI: $uri", e)
        }

        val pfd: ParcelFileDescriptor? = context.contentResolver.openFileDescriptor(uri, "r")

        pfd?.use { fd ->
            val renderer = PdfRenderer(fd)
            val maxPages = minOf(renderer.pageCount, 15)

            for (i in 0 until maxPages) {
                val page = renderer.openPage(i)

                // RGB_565 no necesita canal alfa para páginas de PDF y usa la mitad
                // de memoria que ARGB_8888, lo que ayuda a evitar OutOfMemoryError
                // al renderizar hasta 15 páginas a la vez.
                val bitmap = Bitmap.createBitmap(
                    page.width,
                    page.height,
                    Bitmap.Config.RGB_565
                )

                val canvas = android.graphics.Canvas(bitmap)
                canvas.drawColor(android.graphics.Color.WHITE)

                page.render(
                    bitmap,
                    null,
                    null,
                    PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                )
                bitmaps.add(bitmap)
                page.close()
            }
            renderer.close()
        }

        withContext(Dispatchers.Main) {
            onResult(bitmaps, null)
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            onResult(emptyList(), "Error al abrir el PDF: ${e.localizedMessage}")
        }
    }
}
