package pl.uczelnia.projekt.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/** Warstwa danych – ViewModel zna tylko ten interfejs. */
interface IncydentRepository {
    val incydenty: StateFlow<List<Incydent>>
    fun dodaj(tytul: String, lokalizacja: String, opis: String, priorytet: Priorytet)
    fun zapisz(incydent: Incydent)
    fun usun(id: Long)
    fun przelaczUlubiony(id: Long)
    fun znajdz(id: Long): Incydent?
}

class InMemoryIncydentRepository : IncydentRepository {
    private var nastepneId = 4L
    private val dane = MutableStateFlow(
        listOf(
            Incydent(1, "Zablokowane wyjście ewakuacyjne", "Budynek A, parter", "Wyjście zastawione sprzętem.", Priorytet.WYSOKI),
            Incydent(2, "Przegląd gaśnic", "Budynek B, I piętro", "Kontrola dat ważności gaśnic.", Priorytet.SREDNI),
            Incydent(3, "Uszkodzone oznaczenie drogi ewakuacji", "Parking podziemny", "Brak podświetlenia znaku.", Priorytet.NISKI, ulubiony = true)
        )
    )
    override val incydenty: StateFlow<List<Incydent>> = dane

    override fun dodaj(tytul: String, lokalizacja: String, opis: String, priorytet: Priorytet) {
        val nowy = Incydent(nastepneId++, tytul, lokalizacja, opis, priorytet)
        dane.value = dane.value + nowy
    }

    override fun zapisz(incydent: Incydent) {
        dane.value = dane.value.map { if (it.id == incydent.id) incydent else it }
    }

    override fun usun(id: Long) {
        dane.value = dane.value.filterNot { it.id == id }
    }

    override fun przelaczUlubiony(id: Long) {
        dane.value = dane.value.map { if (it.id == id) it.copy(ulubiony = !it.ulubiony) else it }
    }

    override fun znajdz(id: Long): Incydent? = dane.value.firstOrNull { it.id == id }
}
