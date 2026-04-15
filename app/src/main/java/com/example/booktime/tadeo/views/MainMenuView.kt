package com.example.booktime.tadeo.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.booktime.tadeo.R
import com.example.booktime.tadeo.views.settings.SettingsScreen
import com.example.booktime.tadeo.views.ComingSoonScreen

@Composable
fun MainMenu(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {

    var currentScreen by remember { mutableStateOf("menu") }

    when (currentScreen) {

        "menu" -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF4A5A6E)),
                contentAlignment = Alignment.Center
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(32.dp)
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(200.dp)
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .width(280.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF54C35D),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Iniciar sesión")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier
                            .width(280.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF54C35D),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Registrarse")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    IconButton(onClick = { currentScreen = "settings" }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Configuración",
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }


                    Button(
                        onClick = { currentScreen = "comingSoon" },
                        modifier = Modifier
                            .width(280.dp)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ir a ComingSoon")
                    }
                }
            }
        }

        "settings" -> {
            SettingsScreen(
                onBackClick = { currentScreen = "menu" },
                onAccountClick = {}
            )
        }

        "comingSoon" -> {
            ComingSoonScreen(
                onBackClick = { currentScreen = "menu" },
                onSettingsClick = { currentScreen = "settings" }
            )
        }
    }
}