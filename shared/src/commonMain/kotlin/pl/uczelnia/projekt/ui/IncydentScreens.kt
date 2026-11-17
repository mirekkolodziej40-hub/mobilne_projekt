package pl.uczelnia.projekt.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pl.uczelnia.projekt.data.Incydent
import pl.uczelnia.projekt.data.Priorytet
import pl.uczelnia.projekt.ui.theme.priorytetKolory

/** Stan pusty: ikona + krótki tekst, wyśrodkowane. */
@Composable
fun EmptyState(icon: ImageVector, tekst: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon, contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(Modifier.height(16.dp))
        Text(
            tekst, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center
        )
    }
}

/** Kolorowa plakietka priorytetu. */
@Composable
fun PriorytetBadge(p: Priorytet, modifier: Modifier = Modifier) {
    val k = priorytetKolory(p)
    Surface(modifier = modifier, shape = MaterialTheme.shapes.small, color = k.tlo, contentColor = k.tekst) {
        Text(
            p.etykieta,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/** Lista elementów z akcjami: edycja, usuwanie (z potwierdzeniem), ulubione. */
@Composable
fun IncydentList(
    incydenty: List<Incydent>,
    pustyTekst: String,
    onEdit: (Incydent) -> Unit,
    onDelete: (Incydent) -> Unit,
    onToggleFavorite: (Incydent) -> Unit,
    modifier: Modifier = Modifier,
    pustaIkona: ImageVector = Icons.Filled.Place
) {
    var doUsuniecia by remember { mutableStateOf<Incydent?>(null) }

    if (incydenty.isEmpty()) {
        EmptyState(pustaIkona, pustyTekst, modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(incydenty, key = { it.id }) { incydent ->
                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(start = 16.dp, end = 8.dp, top = 16.dp, bottom = 8.dp)) {
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                incydent.tytul,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                            )
                            PriorytetBadge(incydent.priorytet, Modifier.padding(end = 8.dp))
                        }
                        if (incydent.lokalizacja.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Place, contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    incydent.lokalizacja,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (incydent.opis.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                incydent.opis,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2, overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            IconButton(onClick = { onToggleFavorite(incydent) }) {
                                Icon(
                                    if (incydent.ulubiony) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = if (incydent.ulubiony) "Usuń z ulubionych" else "Dodaj do ulubionych",
                                    tint = if (incydent.ulubiony) MaterialTheme.colorScheme.tertiary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { onEdit(incydent) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edytuj", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { doUsuniecia = incydent }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Usuń", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
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
@OptIn(ExperimentalMaterial3Api::class)
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

    Column(modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(if (incydent == null) "Nowy element" else "Edycja elementu") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                }
            }
        )
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
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
                    val k = priorytetKolory(p)
                    FilterChip(
                        selected = priorytet == p.name,
                        onClick = { priorytet = p.name },
                        label = { Text(p.etykieta) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = k.tlo,
                            selectedLabelColor = k.tekst
                        )
                    )
                }
            }
        }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (onSave(tytul, lokalizacja, opis, Priorytet.valueOf(priorytet))) onBack() else blad = true
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Zapisz") }
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Anuluj") }
        }
    }
}
