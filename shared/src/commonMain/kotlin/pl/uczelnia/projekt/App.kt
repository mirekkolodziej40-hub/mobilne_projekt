package pl.uczelnia.projekt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import pl.uczelnia.projekt.ui.theme.ProjektTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Zgłoszenia", Icons.Filled.Home)
    object Search : Screen("search", "Szukaj", Icons.Filled.Search)
    object Notifications : Screen("notifications", "Alerty", Icons.Filled.Notifications)
    object Favorites : Screen("favorites", "Ulubione", Icons.Filled.Star)
    object Profile : Screen("profile", "Profil", Icons.Filled.Person)

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
    val systemDark = androidx.compose.foundation.isSystemInDarkTheme()

    // Ręczny wybór motywu (domyślnie zgodny z ustawieniem systemu)
    var ciemny by rememberSaveable { mutableStateOf(systemDark) }

    ProjektTheme(darkTheme = ciemny) {
        PaskiSystemowe(ciemny)
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

        val focusManager = LocalFocusManager.current
        fun openForm(id: Long?) { editedId = id; formOpen = true }
        fun selectTab(route: String) { focusManager.clearFocus(); selectedRoute = route; formOpen = false }

        val onEdit: (pl.uczelnia.projekt.data.Incydent) -> Unit = { openForm(it.id) }

        val tabContent: @Composable () -> Unit = {
            Column(Modifier.fillMaxSize()) {
                NaglowekEkranu(selectedScreen.title, ciemny) { ciemny = it }
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    when (selectedScreen) {
                        is Screen.Home -> {
                            val fab: @Composable (Modifier) -> Unit = { m ->
                                FloatingActionButton(
                                    onClick = { openForm(null) },
                                    containerColor = MaterialTheme.colorScheme.tertiary,
                                    contentColor = MaterialTheme.colorScheme.onTertiary,
                                    modifier = m
                                ) { Icon(Icons.Filled.Add, contentDescription = "Dodaj zgłoszenie") }
                            }
                            BoxWithConstraints(Modifier.fillMaxSize()) {
                                if (maxWidth > maxHeight) {
                                    // Poziomo: FAB w osobnej kolumnie obok listy, nie nachodzi na akcje kart.
                                    Row(Modifier.fillMaxSize()) {
                                        IncydentList(
                                            incydenty, "Brak zgłoszeń. Dodaj pierwsze przyciskiem +",
                                            onEdit, { vm.usun(it.id) }, { vm.przelaczUlubiony(it.id) },
                                            modifier = Modifier.weight(1f)
                                        )
                                        Box(Modifier.fillMaxHeight().padding(16.dp), contentAlignment = Alignment.BottomEnd) {
                                            fab(Modifier)
                                        }
                                    }
                                } else {
                                    IncydentList(
                                        incydenty, "Brak zgłoszeń. Dodaj pierwsze przyciskiem +",
                                        onEdit, { vm.usun(it.id) }, { vm.przelaczUlubiony(it.id) }
                                    )
                                    fab(Modifier.align(Alignment.BottomEnd).padding(16.dp))
                                }
                            }
                        }
                        is Screen.Search -> Column(
                            Modifier.fillMaxSize()
                                .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
                        ) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                                label = { Text("Wpisz szukaną frazę...") },
                                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                            val wyniki = incydenty.filter {
                                searchQuery.isNotBlank() &&
                                    (it.tytul.contains(searchQuery, true) || it.lokalizacja.contains(searchQuery, true) || it.opis.contains(searchQuery, true))
                            }
                            IncydentList(
                                wyniki, if (searchQuery.isBlank()) "Wpisz frazę, aby wyszukać" else "Brak wyników dla: \"$searchQuery\"",
                                onEdit, { vm.usun(it.id) }, { vm.przelaczUlubiony(it.id) },
                                pustaIkona = Icons.Filled.Search
                            )
                        }
                        is Screen.Favorites -> IncydentList(
                            incydenty.filter { it.ulubiony }, "Brak ulubionych elementów",
                            onEdit, { vm.usun(it.id) }, { vm.przelaczUlubiony(it.id) },
                            pustaIkona = Icons.Filled.Star
                        )
                        else -> ScreenContent(
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

        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            val isWideScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail {
                        Spacer(modifier = Modifier.weight(1f))
                        items.forEach { screen ->
                            NavigationRailItem(
                                icon = { Icon(screen.icon, contentDescription = null) },
                                label = { EtykietaNawigacji(screen.title) },
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
                                    icon = { Icon(screen.icon, contentDescription = null) },
                                    label = { EtykietaNawigacji(screen.title) },
                                    selected = selectedScreen == screen,
                                    onClick = { selectTab(screen.route) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding).consumeWindowInsets(innerPadding)) {
                        if (formOpen) formContent() else tabContent()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NaglowekEkranu(tytul: String, ciemny: Boolean, onZmianaMotywu: (Boolean) -> Unit) {
    TopAppBar(
        title = { Text(tytul, fontWeight = FontWeight.Bold) },
        actions = { PrzelacznikMotywu(ciemny, onZmianaMotywu) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
    )
}

@Composable
private fun EtykietaNawigacji(tekst: String) {
    Text(
        tekst,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall
    )
}

@Composable
private fun SekcjaKarta(tytul: String, content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(tytul, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            content()
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
    val focusManager = LocalFocusManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) { detectTapGestures(onTap = { focusManager.clearFocus() }) }
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when (screen) {
            is Screen.Home -> {
                SekcjaKarta("Witaj") {
                    Text("Witaj w aplikacji! Cykl życia i stan są w pełni monitorowane.")
                }
            }
            is Screen.Search -> {
                SekcjaKarta("Szukaj") {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChanged,
                        label = { Text("Wpisz szukaną frazu...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Wyniki dla: \"$searchQuery\"", style = MaterialTheme.typography.bodyMedium)
                }
            }
            is Screen.Notifications -> {
                SekcjaKarta("Powiadomienia") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Otrzymuj powiadomienia", Modifier.weight(1f))
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = onNotificationsToggled
                        )
                    }
                    Text(
                        if (notificationsEnabled) "Powiadomienia włączone" else "Powiadomienia wyłączone",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            is Screen.Favorites -> {
                SekcjaKarta("Ulubione") {
                    Text("Twoje ulubione elementy są zachowane.")
                }
            }
            is Screen.Profile -> {
                SekcjaKarta("Dane osobowe") {
                    OutlinedTextField(
                        value = profileName,
                        onValueChange = onProfileNameChanged,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        label = { Text("Imię i nazwisko (Formularz)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = profileEmail,
                        onValueChange = onProfileEmailChanged,
                        singleLine = true,
                        label = { Text("Adres E-mail") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                SekcjaKarta("Podsumowanie") {
                    val imie = profileName.trim()
                    val mail = profileEmail.trim()
                    Text(
                        when {
                            imie.isEmpty() && mail.isEmpty() -> "Uzupełnij dane powyżej"
                            imie.isNotEmpty() && mail.isNotEmpty() -> "Wprowadzono: $imie ($mail)"
                            else -> "Wprowadzono: ${imie.ifEmpty { mail }}"
                        }
                    )
                }
            }
        }
    }
}

/** Przełącznik motywu: słońce – suwak – księżyc. */
@Composable
private fun PrzelacznikMotywu(ciemny: Boolean, onZmiana: (Boolean) -> Unit) {
    val aktywny = MaterialTheme.colorScheme.tertiary
    val nieaktywny = MaterialTheme.colorScheme.onSurfaceVariant
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(end = 12.dp)
    ) {
        IkonaSlonca(if (ciemny) nieaktywny else aktywny)
        Switch(
            checked = ciemny,
            onCheckedChange = onZmiana,
            modifier = Modifier.semantics { contentDescription = "Motyw ciemny" }
        )
        IkonaKsiezyca(if (ciemny) aktywny else nieaktywny)
    }
}

@Composable
private fun IkonaSlonca(kolor: androidx.compose.ui.graphics.Color) {
    androidx.compose.foundation.Canvas(Modifier.size(20.dp)) {
        val c = center
        val r = size.minDimension * 0.22f
        drawCircle(kolor, r, c)
        for (i in 0 until 8) {
            val a = i * kotlin.math.PI / 4
            val dx = kotlin.math.cos(a).toFloat()
            val dy = kotlin.math.sin(a).toFloat()
            drawLine(
                kolor,
                androidx.compose.ui.geometry.Offset(c.x + dx * r * 1.6f, c.y + dy * r * 1.6f),
                androidx.compose.ui.geometry.Offset(c.x + dx * r * 2.2f, c.y + dy * r * 2.2f),
                strokeWidth = 2.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }
    }
}

@Composable
private fun IkonaKsiezyca(kolor: androidx.compose.ui.graphics.Color) {
    androidx.compose.foundation.Canvas(Modifier.size(20.dp)) {
        val r = size.minDimension * 0.42f
        val sierp = androidx.compose.ui.graphics.Path().apply {
            op(
                androidx.compose.ui.graphics.Path().apply {
                    addOval(androidx.compose.ui.geometry.Rect(center, r))
                },
                androidx.compose.ui.graphics.Path().apply {
                    addOval(androidx.compose.ui.geometry.Rect(center + androidx.compose.ui.geometry.Offset(r * 0.55f, -r * 0.35f), r * 0.85f))
                },
                androidx.compose.ui.graphics.PathOperation.Difference
            )
        }
        drawPath(sierp, kolor)
    }
}
