package pl.uczelnia.projekt

import androidx.compose.runtime.Composable

/** Dopasowuje kolor ikon pasków systemowych (status/nawigacja) do motywu aplikacji. */
@Composable
expect fun PaskiSystemowe(ciemny: Boolean)
