package pl.uczelnia.projekt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.uczelnia.projekt.data.Incydent
import pl.uczelnia.projekt.data.Priorytet

/** Lista elementów z akcjami: edycja, usuwanie (z potwierdzeniem), ulubione. */
@Composable
fun IncydentList(
    incydenty: List<Incydent>,
    pustyTekst: String,
    onEdit: (Incydent) -> Unit,
    onDelete: (Incydent) -> Unit,
    onToggleFavorite: (Incydent) -> Unit,
    modifier: Modifier = Modifier
) {
    var doUsuniecia by remember { mutableStateOf<Incydent?>(null) }

    if (incydenty.isEmpty()) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(pustyTekst) }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(incydenty, key = { it.id }) { incydent ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(incydent.tytul, style = MaterialTheme.typography.titleMedium)
                        Text("📍 ${incydent.lokalizacja}", style = MaterialTheme.typography.bodyMedium)
                        if (incydent.opis.isNotBlank()) Text(incydent.opis, style = MaterialTheme.typography.bodySmall)
                        Text("Priorytet: ${incydent.priorytet.etykieta}", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = { onToggleFavorite(incydent) }) {
                                Text(if (incydent.ulubiony) "⭐ Ulubiony" else "☆ Ulubione")
                            }
                            TextButton(onClick = { onEdit(incydent) }) { Text("✏️ Edytuj") }
                            TextButton(onClick = { doUsuniecia = incydent }) { Text("🗑 Usuń") }
                        }
                    }
                }
            }
        }
    }

    doUsuniecia?.let { cel ->
        AlertDialog(
            onDismissRequest = { doUsuniecia = null },
            title = { Text("Usunąć element?") },
            text = { Text("„${cel.tytul}” zostanie trwale usunięty.") },
            confirmButton = {
                TextButton(onClick = { onDelete(cel); doUsuniecia = null }) { Text("Usuń") }
            },
            dismissButton = { TextButton(onClick = { doUsuniecia = null }) { Text("Anuluj") } }
        )
    }
}

/** Formularz dodawania (incydent == null) i edycji elementu. */
@Composable
fun IncydentForm(
    incydent: Incydent?,
    onSave: (tytul: String, lokalizacja: String, opis: String, priorytet: Priorytet) -> Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var tytul by rememberSaveable { mutableStateOf(incydent?.tytul ?: "") }
    var lokalizacja by rememberSaveable { mutableStateOf(incydent?.lokalizacja ?: "") }
    var opis by rememberSaveable { mutableStateOf(incydent?.opis ?: "") }
    var priorytet by rememberSaveable { mutableStateOf((incydent?.priorytet ?: Priorytet.SREDNI).name) }
    var blad by rememberSaveable { mutableStateOf(false) }

    Column(modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = onBack) { Text("← Wróć") }
            Text(
                if (incydent == null) "Nowy element" else "Edycja elementu",
                style = MaterialTheme.typography.headlineSmall
            )
        }
        OutlinedTextField(
            value = tytul, onValueChange = { tytul = it; blad = false },
            label = { Text("Tytuł *") }, isError = blad,
            supportingText = { if (blad) Text("Tytuł jest wymagany") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = lokalizacja, onValueChange = { lokalizacja = it },
            label = { Text("Lokalizacja") }, modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = opis, onValueChange = { opis = it },
            label = { Text("Opis") }, minLines = 3, modifier = Modifier.fillMaxWidth()
        )
        Text("Priorytet", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Priorytet.entries.forEach { p ->
                FilterChip(
                    selected = priorytet == p.name,
                    onClick = { priorytet = p.name },
                    label = { Text(p.etykieta) }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (onSave(tytul, lokalizacja, opis, Priorytet.valueOf(priorytet))) onBack() else blad = true
            }) { Text("Zapisz") }
            OutlinedButton(onClick = onBack) { Text("Anuluj") }
        }
    }
}
