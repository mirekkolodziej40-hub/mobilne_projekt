package pl.uczelnia.projekt

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// 1. Definicja ekranów wraz z etykietą i ikoną (emoji)
sealed class Screen(val route: String, val title: String, val icon: String) {
    object Home : Screen("home", "Start", "🏠")
    object Search : Screen("search", "Szukaj", "🔍")
    object Notifications : Screen("notifications", "Powiadomienia", "🔔")
    object Favorites : Screen("favorites", "Ulubione", "⭐")
    object Profile : Screen("profile", "Profil", "👤")
}

@Composable
fun App() {
    MaterialTheme {
        // Lista 5 ekranów obsługiwanych przez dolną nawigację
        val items = listOf(
            Screen.Home,
            Screen.Search,
            Screen.Notifications,
            Screen.Favorites,
            Screen.Profile
        )

        // Stan przechowujący aktualnie wybraną zakładkę
        var selectedScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Text(screen.icon, style = MaterialTheme.typography.titleMedium) },
                            label = { Text(screen.title) },
                            selected = selectedScreen == screen,
                            onClick = { selectedScreen = screen }
                        )
                    }
                }
            }
        ) { innerPadding ->
            // Główna zawartość ekranu w zależności od wybranej zakładki
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // KLUCZOWE: chroni treść przed zasłonięciem przez dolny pasek
                contentAlignment = Alignment.Center
            ) {
                when (selectedScreen) {
                    is Screen.Home -> Text("Ekran Główny", style = MaterialTheme.typography.headlineMedium)
                    is Screen.Search -> Text("Ekran Wyszukiwania", style = MaterialTheme.typography.headlineMedium)
                    is Screen.Notifications -> Text("Ekran Powiadomień", style = MaterialTheme.typography.headlineMedium)
                    is Screen.Favorites -> Text("Ekran Ulubionych", style = MaterialTheme.typography.headlineMedium)
                    is Screen.Profile -> Text("Ekran Profilu", style = MaterialTheme.typography.headlineMedium)
                }
            }
        }
    }
}
