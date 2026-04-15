package com.example.booktime.tadeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.FirebaseApp
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.views.ComingSoonScreen
import com.example.booktime.tadeo.views.CreateNewPasswordScreen
import com.example.booktime.tadeo.views.ForgotPasswordScreen
import com.example.booktime.tadeo.views.LoadingScreen
import com.example.booktime.tadeo.views.LoginScreen
import com.example.booktime.tadeo.views.MainMenu
import com.example.booktime.tadeo.views.OnboardingTimeScreen
import com.example.booktime.tadeo.views.OnboardingGenreScreen
import com.example.booktime.tadeo.views.RegisterScreen
import kotlinx.coroutines.delay
import com.example.booktime.tadeo.views.settings.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            BooktimeTheme {
                var currentScreen by remember { mutableStateOf("loading") }

                LaunchedEffect(Unit) {
                    delay(3000)
                    currentScreen = "main"
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF4A5A6E))
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (targetState == "register" || targetState == "login" || targetState == "forgot_password" || targetState == "create_new_password" || targetState == "coming_soon" || targetState == "onboarding_time" || targetState == "onboarding_genre" || (initialState == "loading" && targetState == "main")) {

                                slideInHorizontally(animationSpec = tween(400)) { it }
                                    .togetherWith(slideOutHorizontally(animationSpec = tween(400)) { -it / 2 })
                            } else {

                                slideInHorizontally(animationSpec = tween(400)) { -it }
                                    .togetherWith(slideOutHorizontally(animationSpec = tween(400)) { it / 2 })
                            }
                        },
                        label = "ScreenTransition"
                    ) { screen ->
                        when (screen) {
                            "loading" -> LoadingScreen()
                            "main" -> MainMenu(
                                onLoginClick = { currentScreen = "login" },
                                onRegisterClick = { currentScreen = "register" }
                            )

                            "register" -> RegisterScreen(
                                onBackClick = { currentScreen = "main" },
                                onRegisterSuccess = { currentScreen = "onboarding_time" }
                            )

                            "onboarding_time" -> OnboardingTimeScreen(
                                onNext = { currentScreen = "onboarding_genre" }
                            )

                            "onboarding_genre" -> OnboardingGenreScreen(
                                onFinish = { currentScreen = "coming_soon" }
                            )

                            "login" -> LoginScreen(
                                onBackClick = { currentScreen = "main" },
                                onForgotPasswordClick = { currentScreen = "forgot_password" },
                                onLoginSuccess = { currentScreen = "coming_soon" }
                            )

                            "forgot_password" -> ForgotPasswordScreen(
                                onBackClick = { currentScreen = "login" },
                                onConfirmCodeClick = { currentScreen = "create_new_password" }
                            )

                            "create_new_password" -> CreateNewPasswordScreen(
                                onBackClick = { currentScreen = "forgot_password" },
                                onPasswordCreated = { currentScreen = "login" }
                            )

                            "coming_soon" -> ComingSoonScreen(
                                    onBackClick = { currentScreen = "main" },
                            onSettingsClick = {
                                currentScreen = "settings"
                            }
                                )

                            "settings" -> SettingsScreen(
                                onBackClick = { currentScreen = "coming_soon" },
                                onAccountClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}