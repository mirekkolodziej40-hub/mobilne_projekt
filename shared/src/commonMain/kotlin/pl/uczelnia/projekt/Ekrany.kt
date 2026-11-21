package pl.uczelnia.projekt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EkranStartowy() {
    ObserwatorCyklu("Ekran_Start")
    Card(modifier = Modifier.fillMaxWidth()) {
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
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
}

@Composable
fun EkranUstawienia(
    settingNotifications: Boolean,
    onSettingNotificationsChanged: (Boolean) -> Unit,
    settingDarkMode: Boolean,
    onSettingDarkModeChanged: (Boolean) -> Unit,
    settingOfflineMode: Boolean,
    onSettingOfflineModeChanged: (Boolean) -> Unit
) {
    ObserwatorCyklu("Ekran_Ustawienia")
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

@Composable
fun EkranStatystyki() {
    ObserwatorCyklu("Ekran_Statystyki")
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

@Composable
fun EkranFormularz(
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
    ObserwatorCyklu("Ekran_Formularz")
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
