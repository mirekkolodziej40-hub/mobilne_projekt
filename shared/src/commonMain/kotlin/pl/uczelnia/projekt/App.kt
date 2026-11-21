package pl.uczelnia.projekt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect

// --- Model danych (Warstwa danych / model) ---
data class EwakuacjaItem(
    val id: Int,
    val nazwa: String,
    val kategoria: String,
    val status: String
)

// 1. Definicja ekranów
sealed class Screen(val route: String, val title: String, val icon: String) {
    object Home : Screen("home", "Start", "🏠")
    object Search : Screen("search", "Szukaj", "🔍")
    object Notifications : Screen("notifications", "Ustawienia", "⚙️")
    object Favorites : Screen("favorites", "Statystyki", "📊")
    object Profile : Screen("profile", "Formularz", "📝")

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

// Obserwator cyklu życia na różnych ekranach (Zadanie 3.3)
@Composable
fun ObserwatorCyklu(nazwa: String) {
    DisposableEffect(Unit) {
        println("CYKL [$nazwa]: kompozycja dodana")
        onDispose { println("CYKL [$nazwa]: kompozycja usunięta") }
    }
    LifecycleEventEffect(Lifecycle.Event.ON_START) {
        println("CYKL [$nazwa]: ON_START")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        println("CYKL [$nazwa]: ON_RESUME")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        println("CYKL [$nazwa]: ON_PAUSE")
    }
    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
        println("CYKL [$nazwa]: ON_STOP")
    }
}

@Composable
fun App() {
    ObserwatorCyklu("AppRoot")

    MaterialTheme {
        val items = listOf(
            Screen.Home,
            Screen.Search,
            Screen.Notifications,
            Screen.Favorites,
            Screen.Profile
        )

        // Zachowanie wybranej zakładki (rememberSaveable)
        var selectedRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
        val selectedScreen = Screen.fromRoute(selectedRoute)

        // Stany ekranów zachowywane przy obrocie i wstrzymaniu (rememberSaveable)
        var searchQuery by rememberSaveable { mutableStateOf("") }
        var filterCategory by rememberSaveable { mutableStateOf("Wszystkie") }
        
        // Stan formularza z walidacją (Zadanie 2.8)
        var formName by rememberSaveable { mutableStateOf("") }
        var formEmail by rememberSaveable { mutableStateOf("") }
        var formCount by rememberSaveable { mutableStateOf("1") }
        var formIsUrgent by rememberSaveable { mutableStateOf(false) }
        var formSubmitted by rememberSaveable { mutableStateOf(false) }
        var formError by rememberSaveable { mutableStateOf(false) }

        // Stan ustawień (Zadanie 2.9)
        var settingNotifications by rememberSaveable { mutableStateOf(true) }
        var settingDarkMode by rememberSaveable { mutableStateOf(false) }
        var settingOfflineMode by rememberSaveable { mutableStateOf(true) }

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
                        ScreenContentRouter(
                            screen = selectedScreen,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { searchQuery = it },
                            filterCategory = filterCategory,
                            onFilterChanged = { filterCategory = it },
                            formName = formName,
                            onFormNameChanged = { formName = it; formError = false },
                            formEmail = formEmail,
                            onFormEmailChanged = { formEmail = it; formError = false },
                            formCount = formCount,
                            onFormCountChanged = { formCount = it },
                            formIsUrgent = formIsUrgent,
                            onFormUrgentChanged = { formIsUrgent = it },
                            formSubmitted = formSubmitted,
                            formError = formError,
                            onSubmitForm = {
                                if (formName.isBlank() || formEmail.isBlank()) {
                                    formError = true
                                    formSubmitted = false
                                } else {
                                    formError = false
                                    formSubmitted = true
                                }
                            },
                            settingNotifications = settingNotifications,
                            onSettingNotificationsChanged = { settingNotifications = it },
                            settingDarkMode = settingDarkMode,
                            onSettingDarkModeChanged = { settingDarkMode = it },
                            settingOfflineMode = settingOfflineMode,
                            onSettingOfflineModeChanged = { settingOfflineMode = it }
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
                        ScreenContentRouter(
                            screen = selectedScreen,
                            searchQuery = searchQuery,
                            onSearchQueryChanged = { searchQuery = it },
                            filterCategory = filterCategory,
                            onFilterChanged = { filterCategory = it },
                            formName = formName,
                            onFormNameChanged = { formName = it; formError = false },
                            formEmail = formEmail,
                            onFormEmailChanged = { formEmail = it; formError = false },
                            formCount = formCount,
                            onFormCountChanged = { formCount = it },
                            formIsUrgent = formIsUrgent,
                            onFormUrgentChanged = { formIsUrgent = it },
                            formSubmitted = formSubmitted,
                            formError = formError,
                            onSubmitForm = {
                                if (formName.isBlank() || formEmail.isBlank()) {
                                    formError = true
                                    formSubmitted = false
                                } else {
                                    formError = false
                                    formSubmitted = true
                                }
                            },
                            settingNotifications = settingNotifications,
                            onSettingNotificationsChanged = { settingNotifications = it },
                            settingDarkMode = settingDarkMode,
                            onSettingDarkModeChanged = { settingDarkMode = it },
                            settingOfflineMode = settingOfflineMode,
                            onSettingOfflineModeChanged = { settingOfflineMode = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenContentRouter(
    screen: Screen,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    filterCategory: String,
    onFilterChanged: (String) -> Unit,
    formName: String,
    onFormNameChanged: (String) -> Unit,
    formEmail: String,
    onFormEmailChanged: (String) -> Unit,
    formCount: String,
    onFormCountChanged: (String) -> Unit,
    formIsUrgent: Boolean,
    onFormUrgentChanged: (Boolean) -> Unit,
    formSubmitted: Boolean,
    formError: Boolean,
    onSubmitForm: () -> Unit,
    settingNotifications: Boolean,
    onSettingNotificationsChanged: (Boolean) -> Unit,
    settingDarkMode: Boolean,
    onSettingDarkModeChanged: (Boolean) -> Unit,
    settingOfflineMode: Boolean,
    onSettingOfflineModeChanged: (Boolean) -> Unit
) {
    // Podłączamy obserwator cyklu życia dla aktywnego ekranu (Zadanie 3.3)
    ObserwatorCyklu("Ekran_${screen.title}")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Ekran: ${screen.title}", style = MaterialTheme.typography.headlineMedium)

        when (screen) {
            is Screen.Home -> {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("System Zarządzania Ewakuacją", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Wszystkie systemy monitorowania w gotowości. Stan aplikacji jest w pełni zabezpieczony przy obrocie.")
                    }
                }
            }
            is Screen.Search -> {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text("Szukaj budynku / punktu zbiórki...") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = filterCategory == "Wszystkie", onClick = { onFilterChanged("Wszystkie") }, label = { Text("Wszystkie") })
                    FilterChip(selected = filterCategory == "Budynki", onClick = { onFilterChanged("Budynki") }, label = { Text("Budynki") })
                    FilterChip(selected = filterCategory == "Schronienia", onClick = { onFilterChanged("Schronienia") }, label = { Text("Schronienia") })
                }
                Text("Wyniki dla: \"$searchQuery\" [Kategoria: $filterCategory]")
            }
            is Screen.Notifications -> {
                // Zadanie 2.9: Ekran ustawień z co najmniej trzema przełącznikami
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Ustawienia Aplikacji", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Powiadomienia alarmowe")
                            Switch(checked = settingNotifications, onCheckedChange = onSettingNotificationsChanged)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Tryb ciemny")
                            Switch(checked = settingDarkMode, onCheckedChange = onSettingDarkModeChanged)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Praca offline")
                            Switch(checked = settingOfflineMode, onCheckedChange = onSettingOfflineModeChanged)
                        }
                    }
                }
            }
            is Screen.Favorites -> {
                // Zadanie 2.9: Ekran statystyk pokazujący wartości i pasek postępu
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Statystyki Ewakuacji", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("• Aktywne budynki w systemie: 12")
                        Text("• Punkty zbiórki: 5")
                        Text("• Bezpieczne schrony: 3")
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Stopień przygotowania kampusu:")
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(progress = { 0.85f }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("85% gotowości operacyjnej", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            is Screen.Profile -> {
                // Zadanie 2.8: Formularz z walidacją, polami obowiązkowymi, komunikatem o błędzie i przewijaniem
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Formularz Zgłoszenia / Statusu", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = formName,
                            onValueChange = onFormNameChanged,
                            label = { Text("Imię i nazwisko *") },
                            isError = formError && formName.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (formError && formName.isBlank()) {
                            Text("Pole wymagane!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = formEmail,
                            onValueChange = onFormEmailChanged,
                            label = { Text("E-mail kontaktowy *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            isError = formError && formEmail.isBlank(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (formError && formEmail.isBlank()) {
                            Text("Pole wymagane!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = formCount,
                            onValueChange = onFormCountChanged,
                            label = { Text("Liczba osób w grupie") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Pilne wezwanie pomocy")
                            Switch(checked = formIsUrgent, onCheckedChange = onFormUrgentChanged)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = onSubmitForm, modifier = Modifier.fillMaxWidth()) {
                            Text("Zapisz zgłoszenie")
                        }

                        if (formSubmitted) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Zgłoszenie wysłane pomyślnie!", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
