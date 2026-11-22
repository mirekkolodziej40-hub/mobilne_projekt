package pl.uczelnia.projekt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.uczelnia.projekt.ui.theme.ProjektTheme

@Composable
fun App() {
    ObserwatorCyklu("AppRoot")

    val systemDark = isSystemInDarkTheme()
    // Tryb ciemny z ekranu ustawień (domyślnie zgodny z systemem); zachowywany przy obrocie
    var settingDarkMode by rememberSaveable { mutableStateOf(systemDark) }

    ProjektTheme(darkTheme = settingDarkMode) {
        PaskiSystemowe(settingDarkMode)
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
        var settingOfflineMode by rememberSaveable { mutableStateOf(true) }

        BoxWithConstraints(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            val isWideScreen = maxWidth >= 600.dp

            if (isWideScreen) {
                Row(modifier = Modifier.fillMaxSize()) {
                    NavigationRail {
                        Spacer(modifier = Modifier.weight(1f))
                        items.forEach { screen ->
                            NavigationRailItem(
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
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
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
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
            is Screen.Home -> EkranStartowy()
            is Screen.Search -> EkranSzukaj(searchQuery, onSearchQueryChanged, filterCategory, onFilterChanged)
            is Screen.Notifications -> EkranPowiadomienia(settingNotifications, onSettingNotificationsChanged, settingDarkMode, onSettingDarkModeChanged, settingOfflineMode, onSettingOfflineModeChanged)
            is Screen.Favorites -> EkranUlubione()
            is Screen.Profile -> EkranProfil(formName, onFormNameChanged, formEmail, onFormEmailChanged, formCount, onFormCountChanged, formIsUrgent, onFormUrgentChanged, formSubmitted, formError, onSubmitForm)
        }
    }
}
