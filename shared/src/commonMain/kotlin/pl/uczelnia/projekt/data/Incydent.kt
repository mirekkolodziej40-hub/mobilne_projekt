package pl.uczelnia.projekt.data

enum class Priorytet(val etykieta: String) {
    NISKI("Niski"), SREDNI("Średni"), WYSOKI("Wysoki")
}

/** Model domenowy: zgłoszenie dotyczące bezpieczeństwa / ewakuacji. */
data class Incydent(
    val id: Long,
    val tytul: String,
    val lokalizacja: String,
    val opis: String,
    val priorytet: Priorytet,
    val ulubiony: Boolean = false
)
