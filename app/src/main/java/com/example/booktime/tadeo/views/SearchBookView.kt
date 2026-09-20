package com.example.booktime.tadeo.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.booktime.tadeo.components.*
import com.example.booktime.tadeo.data.model.GoogleBookItem
import com.example.booktime.tadeo.navigation.Screen
import com.example.booktime.tadeo.ui.theme.*
import com.example.booktime.tadeo.viewmodels.SearchBookViewModel
import com.google.firebase.auth.FirebaseAuth
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SearchBookView(
    navController: NavController,
    onBottomNavClick: (Int) -> Unit = {},
    viewModel: SearchBookViewModel = viewModel()
) {
    Scaffold(
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = { BooktimeBottomNav(selectedItem = 2, onItemSelected = onBottomNavClick) },
        containerColor = PrincipalMenu
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Buscar en línea",
                color = AppWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            BooktimeTextField(
                value = viewModel.query,
                onValueChange = { viewModel.query = it },
                placeholder = "Buscar",
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.onSearch() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ButtonGreen)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(viewModel.results) { book ->
                        GoogleBookItemRow(
                            book = book,
                            isSelected = viewModel.selectedBookId == book.id,
                            onSelect = { viewModel.selectBook(book.id) }
                        )
                    }
                }

                if (viewModel.selectedBookId != null) {
                    Button(
                        onClick = {
                            val userId = FirebaseAuth.getInstance().currentUser?.uid
                            userId?.let { uid ->
                                viewModel.saveBookToFirebase(uid) { savedBook ->
                                    val encodedUrl = URLEncoder.encode(savedBook.imageUrl, "UTF-8")
                                    val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(savedBook.addedAt?.toDate() ?: Date())
                                    navController.navigate("${Screen.BookAddedSuccess.route}/${savedBook.title}/${savedBook.author}/$encodedUrl?date=$date")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(bottom = padding.calculateBottomPadding() + 16.dp).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonGreen),
                        shape = RoundedCornerShape(28.dp),
                        enabled = !viewModel.isSaving
                    ) {
                        if (viewModel.isSaving) CircularProgressIndicator(color = AppWhite, modifier = Modifier.size(24.dp))
                        else Text("Guardar", color = AppWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleBookItemRow(book: GoogleBookItem, isSelected: Boolean, onSelect: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(120.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) FillRectangle.copy(alpha = 0.7f) else FillRectangle.copy(alpha = 0.3f),
        tonalElevation = if (isSelected) 8.dp else 0.dp,
        onClick = onSelect
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = book.volumeInfo.imageLinks?.httpsThumbnail,
                contentDescription = null,
                modifier = Modifier.width(70.dp).fillMaxHeight().clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                InfoLabel("TITULO", book.volumeInfo.title)
                InfoLabel("AUTOR", book.volumeInfo.authors?.joinToString() ?: "Desconocido")
                InfoLabel("GÉNERO", book.volumeInfo.categories?.firstOrNull() ?: "Fantasía")
            }
        }
    }
}

@Composable
fun InfoLabel(label: String, value: String) {
    Row {
        Text("$label: ", color = AppWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(value, color = AppWhite, fontSize = 11.sp, maxLines = 1)
    }
}

