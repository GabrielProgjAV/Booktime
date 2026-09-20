package com.example.booktime.tadeo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType

import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import com.example.booktime.tadeo.ui.theme.BooktimeTheme
import com.example.booktime.tadeo.navigation.Screen
import com.example.booktime.tadeo.views.*

import kotlinx.coroutines.delay

import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.booktime.tadeo.viewmodels.SettingsViewModel

import com.example.booktime.tadeo.analytics.AnalyticsScreen

@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        setContent {
            BooktimeTheme {
                val navController = rememberNavController()
                val auth = remember { FirebaseAuth.getInstance() }
                val settingsViewModel: SettingsViewModel = viewModel()
                
                // Observar el estado de la sesión de forma reactiva
                var currentUser by remember { mutableStateOf(auth.currentUser) }

                // Evita que la navegación inicial de la pantalla de carga y el listener de
                // abajo naveguen ambos al arrancar la app (navegación duplicada/competida).
                // Solo se marca como "true" una vez que la pantalla de carga ya navegó.
                var hasNavigatedFromLoading by remember { mutableStateOf(false) }

                // Listener para cambios de estado de autenticación (Cierre de sesión global)
                DisposableEffect(auth) {
                    val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                        val user = firebaseAuth.currentUser
                        currentUser = user

                        if (user == null) {
                            // Si el usuario cierra sesión, limpiamos TODO el stack y mandamos al Main.
                            // Durante el arranque en frío, la navegación inicial la controla la
                            // pantalla de carga; este listener solo navega tras ese primer arranque.
                            if (hasNavigatedFromLoading) {
                                navController.navigate(Screen.Main.route) {
                                    popUpTo(0) { inclusive = true }
                                    launchSingleTop = true
                                }
                            }
                        } else {
                            // Cuando un usuario inicia sesión o la app arranca con uno,
                            // aseguramos que se carguen sus ajustes y se programen sus alarmas.
                            settingsViewModel.loadSettings()
                        }
                    }
                    auth.addAuthStateListener(listener)
                    onDispose {
                        auth.removeAuthStateListener(listener)
                    }
                }

             val onBottomNavItemSelected: (Int) -> Unit = { index ->

    when (index) {

        0 -> navController.navigate(Screen.Books.route)

        1 -> navController.navigate(Screen.Analytics.route)

        2 -> navController.navigate(Screen.NewBook.route)

        3 -> navController.navigate(Screen.FavoriteBooks.route)

        4 -> navController.navigate(Screen.Settings.route)
    }
}

                NavHost(
                    navController = navController,
                    startDestination = Screen.Loading.route,
                    modifier = Modifier,
                    enterTransition = { slideInRight() },
                    exitTransition = { slideOutLeft() },
                    popEnterTransition = { slideInLeft() },
                    popExitTransition = { slideOutRight() }
                ) {
                    composable(
                        route = Screen.Loading.route,
                        enterTransition = { fadeIn(animationSpec = tween(500)) },
                        exitTransition = { fadeOut(animationSpec = tween(500)) }
                    ) {
                        LaunchedEffect(Unit) {
                            delay(2000)
                            if (!hasNavigatedFromLoading) {
                                hasNavigatedFromLoading = true
                                if (currentUser != null) {
                                    navController.navigate(Screen.Books.route) {
                                        popUpTo(Screen.Loading.route) { inclusive = true }
                                    }
                                } else {
                                    navController.navigate(Screen.Main.route) {
                                        popUpTo(Screen.Loading.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                        LoadingScreen()
                    }

                    composable(route = Screen.Main.route) {
                        MainMenu(
                            onLoginClick = { navController.navigate(Screen.Login.route) },
                            onRegisterClick = { navController.navigate(Screen.Register.route) }
                        )
                    }

                    composable(route = Screen.Login.route) {
                        LoginScreen(
                            onBackClick = { navController.popBackStack() },
                            onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                            onLoginSuccess = {
                                navController.navigate(Screen.Books.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(route = Screen.Register.route) {
                        RegisterScreen(
                            onBackClick = { navController.popBackStack() },
                            onRegisterSuccess = {
                                navController.navigate(Screen.OnboardingTime.route)
                            }
                        )
                    }

                    composable(
                        route = Screen.Books.route
                    ) {
                        BooksView(
                            onAddClick = { navController.navigate(Screen.NewBook.route) },
                            onBookClick = { bookId -> 
                                navController.navigate(Screen.BookDetail.createRoute(bookId))
                            },
                            onBottomNavClick = onBottomNavItemSelected
                        )
                    }

                    composable(
                        route = Screen.BookDetail.route,
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                        BookDetailView(
                            bookId = bookId,
                            onBackClick = { navController.popBackStack() },
                            onReadClick = { id -> 
                                navController.navigate(Screen.PdfReader.createRoute(id))
                            }
                        )
                    }

                    composable(
                        route = Screen.PdfReader.route,
                        arguments = listOf(navArgument("bookId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                        PdfReaderView(
                            bookId = bookId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = Screen.Settings.route
                    ) {
                        SettingsView(
                            onBackClick = { navController.popBackStack() },
                            onEditProfileClick = { navController.navigate(Screen.EditProfile.route) },
                            onChangePasswordClick = { navController.navigate(Screen.ChangePassword.route) },
                            onReadingSettingsClick = { navController.navigate(Screen.ReadingSettings.route) },
                            onLogoutClick = {
                                // El signOut() lo hace el ViewModel, el listener de arriba redirige
                            },
                            onBottomNavClick = onBottomNavItemSelected
                        )
                    }

                    composable(Screen.EditProfile.route) {
                        EditProfileView(
                            onBackClick = { navController.popBackStack() },
                            onBottomNavClick = onBottomNavItemSelected
                        )
                    }

                    composable(Screen.ChangePassword.route) {
                        ChangePasswordView(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ReadingSettings.route) {
                        ReadingSettingsView(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.NewBook.route) {
                        NewBookScreen(
                            onBackClick = { navController.popBackStack() },
                            onSearchClick = { navController.navigate(Screen.SearchBook.route) },
                            onImportEbookClick = { navController.navigate(Screen.NewEbook.route) },
                            onImportAudiobookClick = { },
                            onBottomNavClick = onBottomNavItemSelected
                        )
                    }

                    composable(Screen.NewEbook.route) {
                        AddBookScreen(
                            userId = currentUser?.uid ?: "",
                            onBackClick = { navController.popBackStack() },
                            onBottomNavClick = onBottomNavItemSelected
                        )
                    }

                    composable(Screen.SearchBook.route) {
                        SearchBookView(
                            navController = navController,
                            onBottomNavClick = onBottomNavItemSelected
                        )
                    }

                    composable(Screen.FavoriteBooks.route) {
                        FavoriteBooksView(
                            onBackClick = { navController.popBackStack() },
                            onBookClick = { bookId -> 
                                navController.navigate(Screen.BookDetail.createRoute(bookId))
                            }
                        )
                    }

                    composable(Screen.Analytics.route) {
                        AnalyticsScreen(
                            onBottomNavItemSelected = { index ->

                                when (index) {

                                    0 -> navController.navigate(Screen.Books.route)

                                    1 -> navController.navigate(Screen.Analytics.route)

                                    2 -> navController.navigate(Screen.NewBook.route)

                                    3 -> navController.navigate(Screen.FavoriteBooks.route)

                                    4 -> navController.navigate(Screen.Settings.route)
                                }
                            }
                        )
                    }

                    composable(Screen.ComingSoon.route) {
                        ComingSoonScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.OnboardingTime.route) {
                        OnboardingTimeScreen(
                            onNext = { navController.navigate(Screen.OnboardingGenre.route) }
                        )
                    }

                    composable(Screen.OnboardingGenre.route) {
                        OnboardingGenreScreen(
                            onFinish = {
                                navController.navigate(Screen.Books.route) {
                                    popUpTo(Screen.Main.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.ForgotPassword.route) {
                        ForgotPasswordScreen(
                            onBackClick = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "${Screen.BookAddedSuccess.route}/{title}/{author}/{imageUrl}?date={date}",
                        arguments = listOf(
                            navArgument("title") { type = NavType.StringType },
                            navArgument("author") { type = NavType.StringType },
                            navArgument("imageUrl") { type = NavType.StringType },
                            navArgument("date") {
                                type = NavType.StringType
                                defaultValue = "Desconocida"
                            }
                        )
                    ) { backStackEntry ->
                        val title = backStackEntry.arguments?.getString("title") ?: ""
                        val author = backStackEntry.arguments?.getString("author") ?: ""
                        val date = backStackEntry.arguments?.getString("date") ?: ""
                        val encodedUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
                        val imageUrl = java.net.URLDecoder.decode(encodedUrl, "UTF-8")

                        BookAddedSuccessView(
                            title = title,
                            author = author,
                            imageUrl = imageUrl,
                            dateAdded = date,
                            onStartClick = {
                                navController.navigate(Screen.Books.route) {
                                    popUpTo(Screen.Books.route) { inclusive = true }
                                }
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

fun slideInRight(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        initialOffsetX = { it }
    ) + fadeIn(animationSpec = tween(500))

fun slideOutLeft(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        targetOffsetX = { -it / 3 }
    ) + fadeOut(animationSpec = tween(500))

fun slideInLeft(): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        initialOffsetX = { -it / 3 }
    ) + fadeIn(animationSpec = tween(500))

fun slideOutRight(): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        targetOffsetX = { it }
    ) + fadeOut(animationSpec = tween(500))
