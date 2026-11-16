package pl.uczelnia.projekt.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow
import pl.uczelnia.projekt.data.Incydent
import pl.uczelnia.projekt.data.IncydentRepository
import pl.uczelnia.projekt.data.Priorytet

/** Warstwa ViewModel: logika prezentacji, pośrednik między UI a repozytorium. */
class IncydentViewModel(private val repozytorium: IncydentRepository) : ViewModel() {
    val incydenty: StateFlow<List<Incydent>> = repozytorium.incydenty

    fun znajdz(id: Long): Incydent? = repozytorium.znajdz(id)

    /** Zapisuje nowy (id == null) lub edytowany element. Zwraca false, gdy dane są niepoprawne. */
    fun zapisz(id: Long?, tytul: String, lokalizacja: String, opis: String, priorytet: Priorytet): Boolean {
        if (tytul.isBlank()) return false
        val istniejacy = id?.let { repozytorium.znajdz(it) }
        if (istniejacy != null) {
            repozytorium.zapisz(
                istniejacy.copy(
                    tytul = tytul.trim(), lokalizacja = lokalizacja.trim(),
                    opis = opis.trim(), priorytet = priorytet
                )
            )
        } else {
            repozytorium.dodaj(tytul.trim(), lokalizacja.trim(), opis.trim(), priorytet)
        }
        return true
    }

    fun usun(id: Long) = repozytorium.usun(id)
    fun przelaczUlubiony(id: Long) = repozytorium.przelaczUlubiony(id)
}
