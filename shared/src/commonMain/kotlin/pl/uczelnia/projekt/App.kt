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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.uczelnia.projekt.data.InMemoryIncydentRepository
import pl.uczelnia.projekt.ui.IncydentForm
import pl.uczelnia.projekt.ui.IncydentList
import pl.uczelnia.projekt.viewmodel.IncydentViewModel

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

// Obserwator cyklu życia (Laboratorium 3 - LifecycleEventEffect & DisposableEffect)
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
    ObserwatorCyklu("App")

    MaterialTheme {
        val items = listOf(Screen.Home, Screen.Search, Screen.Notifications, Screen.Favorites, Screen.Profile)

        // ViewModel (warstwa prezentacji) -> repozytorium (warstwa danych)
        val vm: IncydentViewModel = viewModel { IncydentViewModel(InMemoryIncydentRepository()) }

        // Nawigacja: wybrana zakładka + opcjonalny ekran formularza (dodawanie / edycja)
        var selectedRoute by rememberSaveable { mutableStateOf(Screen.Home.route) }
        var formOpen by rememberSaveable { mutableStateOf(false) }
        var editedId by rememberSaveable { mutableStateOf<Long?>(null) }
        val selectedScreen = Screen.fromRoute(selectedRoute)

        var searchQuery by rememberSaveable { mutableStateOf("") }
        var profileName by rememberSaveable { mutableStateOf("") }
        var profileEmail by rememberSaveable { mutableStateOf("") }
        var notificationsEnabled by rememberSaveable { mutableStateOf(true) }

        val incydenty by vm.incydenty.collectAsState()

        fun openForm(id: Long?) { editedId = id; formOpen = true }
        fun selectTab(route: String) { selectedRoute = route; formOpen = false }

        val onEdit: (pl.uczelnia.projekt.data.Incydent) -> Unit = { openForm(it.id) }

        val tabContent: @Composable () -> Unit = {
            when (selectedScreen) {
                is Screen.Home -> Box(Modifier.fillMaxSize()) {
                    IncydentList(
                        incydenty, "Brak elementów. Dodaj pierwszy przyciskiem +",
                        onEdit, { vm.usun(it.id) }, { vm.przelaczUlubiony(it.id) }
                    )
                    FloatingActionButton(
                        onClick = { openForm(null) },
                        modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
                    ) { Text("＋", style = MaterialTheme.typography.titleLarge) }
                }
                is Screen.Search -> Column(Modifier.fillMaxSize()) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Wpisz szukaną frazę...") },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                    val wyniki = incydenty.filter {
                        searchQuery.isNotBlank() &&
                            (it.tytul.contains(searchQuery, true) || it.lokalizacja.contains(searchQuery, true) || it.opis.contains(searchQuery, true))
                    }
                    IncydentList(
                        wyniki, if (searchQuery.isBlank()) "Wpisz frazę, aby wyszukać" else "Brak wyników dla: \"$searchQuery\"",
                        onEdit, { vm.usun(it.id) }, { vm.przelaczUlubiony(it.id) }
                    )
                }
                is Screen.Favorites -> IncydentList(
                    incydenty.filter { it.ulubiony }, "Brak ulubionych elementów",
                    onEdit, { vm.usun(it.id) }, { vm.przelaczUlubiony(it.id) }
                )
                else -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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

        val formContent: @Composable () -> Unit = {
            // key: formularz inicjalizuje się od nowa dla każdego edytowanego elementu
            key(editedId) {
                IncydentForm(
                    incydent = editedId?.let { vm.znajdz(it) },
                    onSave = { t, l, o, p -> vm.zapisz(editedId, t, l, o, p) },
                    onBack = { formOpen = false }
                )
            }
        }

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
                                onClick = { selectTab(screen.route) }
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Box(modifier = Modifier.fillMaxSize().weight(1f)) {
                        if (formOpen) formContent() else tabContent()
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
                                    onClick = { selectTab(screen.route) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        if (formOpen) formContent() else tabContent()
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
                Text("Witaj w aplikacji! Cykl życia i stan są w pełni monitorowane.")
            }
            is Screen.Search -> {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChanged,
                    label = { Text("Wpisz szukaną frazu...") },
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
                Text("Twoje ulubione elementy są zachowane.")
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
