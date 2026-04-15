package com.example.booktime.tadeo.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.viewmodel.AccountViewModel

@Composable
fun ComingSoonScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val viewModel: AccountViewModel = viewModel()

    LaunchedEffect(Unit) {
        viewModel.loadUser()
    }

    val user by viewModel.user.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF4A5A6E))
    ) {

        // 🔙 BOTÓN BACK
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .padding(top = 48.dp, start = 16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }


        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Configuración",
                tint = Color.White
            )
        }


        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Próximamente",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Esta función estará disponible pronto.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nombre: ${user?.name ?: "Cargando..."}",
                color = Color.White
            )

            Text(
                text = "Email: ${user?.email ?: "Cargando..."}",
                color = Color.White
            )
        }
    }
}