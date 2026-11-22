package pl.uczelnia.projekt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.unit.dp

@Composable
fun EkranStartowy() {
    ObserwatorCyklu("Ekran_Start")
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("System Zarządzania Ewakuacją", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Wszystkie systemy monitorowania w gotowości. Stan aplikacji jest w pełni zabezpieczony przy obrocie.")
        }
    }
}

@Composable
fun EkranSzukaj(
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    filterCategory: String,
    onFilterChanged: (String) -> Unit
) {
    ObserwatorCyklu("Ekran_Szukaj")
    val focusManager = LocalFocusManager.current
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text("Szukaj budynku / punktu zbiórki...") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            modifier = Modifier.fillMaxWidth()
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filterCategory == "Wszystkie", onClick = { onFilterChanged("Wszystkie") }, label = { Text("Wszystkie") })
            FilterChip(selected = filterCategory == "Budynki", onClick = { onFilterChanged("Budynki") }, label = { Text("Budynki") })
            FilterChip(selected = filterCategory == "Schronienia", onClick = { onFilterChanged("Schronienia") }, label = { Text("Schronienia") })
        }
        Text("Wyniki dla: \"$searchQuery\" [Kategoria: $filterCategory]")
    }
}

@Composable
fun EkranPowiadomienia(
    settingNotifications: Boolean,
    onSettingNotificationsChanged: (Boolean) -> Unit,
    settingDarkMode: Boolean,
    onSettingDarkModeChanged: (Boolean) -> Unit,
    settingOfflineMode: Boolean,
    onSettingOfflineModeChanged: (Boolean) -> Unit
) {
    ObserwatorCyklu("Ekran_Powiadomienia")
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Powiadomienia i Ustawienia", style = MaterialTheme.typography.titleLarge)
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

@Composable
fun EkranUlubione() {
    ObserwatorCyklu("Ekran_Ulubione")
    // Wartości wyliczane z danych
    val razem = przykladoweElementy.size
    val poKategoriach = przykladoweElementy.groupingBy { it.kategoria }.eachCount()
    val gotowe = przykladoweElementy.count { it.status == "Gotowy" }
    val procent = if (razem == 0) 0f else gotowe.toFloat() / razem

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Statystyki", style = MaterialTheme.typography.titleLarge)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KartaStatystyki("Wszystkie", razem, Modifier.weight(1f))
            poKategoriach.forEach { (kategoria, liczba) ->
                KartaStatystyki(kategoria, liczba, Modifier.weight(1f))
            }
        }
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Gotowość operacyjna: $gotowe z $razem")
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = { procent }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(4.dp))
                Text("${(procent * 100).toInt()}% gotowych", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun KartaStatystyki(nazwa: String, liczba: Int, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$liczba", style = MaterialTheme.typography.headlineMedium)
            Text(nazwa, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun EkranProfil(
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
    onSubmitForm: () -> Unit
) {
    ObserwatorCyklu("Ekran_Profil")
    val focusManager = LocalFocusManager.current
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Profil użytkownika i Formularz", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = formName,
                onValueChange = onFormNameChanged,
                label = { Text("Imię i nazwisko *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
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
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Lista rozwijana: typ zgłoszenia
            var typ by rememberSaveable { mutableStateOf("Zagrożenie pożarowe") }
            var rozwiniete by remember { mutableStateOf(false) }
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { rozwiniete = true }, modifier = Modifier.fillMaxWidth()) {
                    Text("Typ zgłoszenia: $typ", modifier = Modifier.weight(1f))
                    Text("▾")
                }
                DropdownMenu(expanded = rozwiniete, onDismissRequest = { rozwiniete = false }) {
                    listOf("Zagrożenie pożarowe", "Zagrożenie chemiczne", "Awaria zasilania", "Inne").forEach { opcja ->
                        DropdownMenuItem(text = { Text(opcja) }, onClick = { typ = opcja; rozwiniete = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Pilne wezwanie pomocy")
                Switch(checked = formIsUrgent, onCheckedChange = onFormUrgentChanged)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { focusManager.clearFocus(); onSubmitForm() }, modifier = Modifier.fillMaxWidth()) {
                Text("Zapisz profil / zgłoszenie")
            }

            if (formSubmitted) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Zgłoszenie wysłane pomyślnie!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
