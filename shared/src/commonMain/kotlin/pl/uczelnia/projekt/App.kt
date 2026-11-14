package pl.uczelnia.projekt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// 1. Definicja ekranów
sealed class Screen(val route: String, val title: String, val icon: String) {
    object Home : Screen("home", "Start", "🏠")
    object Search : Screen("search", "Szukaj", "🔍")
    object Notifications : Screen("notifications", "Powiadomienia", "🔔")
    object Favorites : Screen("favorites", "Ulubione", "⭐")
    object Profile : Screen("profile", "Profil", "👤")

    companion object {
        fun fromRoute(route: String): Screen = when (route) {
            "search" -> Search
            "notifications" -> Notifications
            "favorites" -> Favorites
            "profile" -> Profile
            else -> Home
        }
    }
}

@Composable
fun App() {
    MaterialTheme {
        val items = listOf(
            Screen.Home,
            Screen.Search,
            Screen.Notifications,
            Screen.Favorites,
            Screen.Profile
        )

        // Zachowanie wybranej zakładki przy obrocie / wstrzymaniu aplikacji za pomocą rememberSaveable
        var selectedRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
        val selectedScreen = Screen.fromRoute(selectedRoute)

        // Stany ekranów zachowywane przy obrocie i wstrzymaniu (rememberSaveable)
        var searchQuery by rememberSaveable { mutableStateOf("") }
        var profileName by rememberSaveable { mutableStateOf("") }
        var profileEmail by rememberSaveable { mutableStateOf("") }
        var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isWideScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail {
                        Spacer(modifier = Modifier.weight(1f))
                        items.forEach { screen ->
                            NavigationRailItem(
                                icon = { Text(screen.icon, style = MaterialTheme.typography.titleMedium) },
                                label = { Text(screen.title) },
                                selected = selectedScreen == screen,
                                onClick = { selectedRoute = screen.route }
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        ScreenContent(
                            screen = selectedScreen,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { searchQuery = it },
                            profileName = profileName,
                            onProfileNameChanged = { profileName = it },
                            profileEmail = profileEmail,
                            onProfileEmailChanged = { profileEmail = it },
                            notificationsEnabled = notificationsEnabled,
                            onNotificationsToggled = { notificationsEnabled = it }
                        )
                    }
                }
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Text(screen.icon, style = MaterialTheme.typography.titleMedium) },
                                    label = { Text(screen.title) },
                                    selected = selectedScreen == screen,
                                    onClick = { selectedRoute = screen.route }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        ScreenContent(
                            screen = selectedScreen,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { searchQuery = it },
                            profileName = profileName,
                            onProfileNameChanged = { profileName = it },
                            profileEmail = profileEmail,
                            onProfileEmailChanged = { profileEmail = it },
                            notificationsEnabled = notificationsEnabled,
                            onNotificationsToggled = { notificationsEnabled = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    screen: Screen,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    profileName: String,
    onProfileNameChanged: (String) -> Unit,
    profileEmail: String,
    onProfileEmailChanged: (String) -> Unit,
    notificationsEnabled: Boolean,
    onNotificationsToggled: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Aktualny ekran: ${screen.title}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        when (screen) {
            is Screen.Home -> {
                Text("Witaj w aplikacji! Stan jest w pełni zachowywany przy obrocie i wstrzymaniu.")
            }
            is Screen.Search -> {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text("Wpisz szukaną frazę...") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Wyniki dla: \"$searchQuery\"")
            }
            is Screen.Notifications -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("Otrzymuj powiadomienia")
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = onNotificationsToggled
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(if (notificationsEnabled) "Powiadomienia włączone" else "Powiadomienia wyłączone")
            }
            is Screen.Favorites -> {
                Text("Twoje ulubione elementy są bezpieczne.")
            }
            is Screen.Profile -> {
                OutlinedTextField(
                    value = profileName,
                    onValueChange = onProfileNameChanged,
                    label = { Text("Imię i nazwisko (Formularz)") },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = profileEmail,
                    onValueChange = onProfileEmailChanged,
                    label = { Text("Adres E-mail") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Wprowadzono: $profileName ($profileEmail)")
            }
        }
    }
}
