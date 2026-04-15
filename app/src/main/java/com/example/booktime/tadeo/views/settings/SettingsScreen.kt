package com.example.booktime.tadeo.views.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.viewmodel.AccountViewModel
import coil.compose.rememberAsyncImagePainter
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip

val TextPrimary = Color(0xFFD9D9D9)

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onAccountClick: () -> Unit,
    viewModel: AccountViewModel = viewModel()
) {

    val user by viewModel.user.collectAsState()
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->

        uri?.let {
            println(" IMAGEN SELECCIONADA")

            imageUri = it
            viewModel.uploadImage(it)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5A6E))
            .padding(16.dp)
    ) {


        Column {



            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Configuración",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF657285)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.White, shape = CircleShape)
                        .clickable {
                            launcher.launch("image/*")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        imageUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                            )
                        }

                        !user?.photoUrl.isNullOrEmpty() -> {
                            Image(
                                painter = rememberAsyncImagePainter(user?.photoUrl),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                            )
                        }

                        else -> {
                            Text("👤")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = user?.name ?: "Cargando...",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user?.email ?: "",
                    color = TextPrimary.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        SettingItem("Cuenta", onClick = onAccountClick)
        SettingItem("Notificaciones") {}
        SettingItem("Privacidad") {}
        SettingItem("Ayuda") {}
        SettingItem("Acerca de") {}

        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión", color = TextPrimary)
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF657285)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = TextPrimary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5A6E))
            .padding(16.dp)
    ) {

        Text("9:30", color = TextPrimary)

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Configuración", color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF657285)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("👤")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("User name", color = TextPrimary, fontWeight = FontWeight.Bold)
                Text("correo@exple.com", color = TextPrimary.copy(alpha = 0.7f))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SettingItem("Cuenta") {}
        SettingItem("Notificaciones") {}
        SettingItem("Privacidad") {}
        SettingItem("Ayuda") {}
        SettingItem("Acerca de") {}

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
            shape = RoundedCornerShape(50),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión", color = TextPrimary)
        }
    }
}