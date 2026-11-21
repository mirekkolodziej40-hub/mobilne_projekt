package pl.uczelnia.projekt

import androidx.compose.runtime.Immutable

@Immutable
data class EwakuacjaItem(
    val id: Int,
    val nazwa: String,
    val kategoria: String,
    val status: String
)

sealed class Screen(val route: String, val title: String, val icon: String) {
    object Home : Screen("home", "Start", "🏠")
    object Search : Screen("search", "Szukaj", "🔍")
    object Notifications : Screen("notifications", "Ustawienia", "⚙️")
    object Favorites : Screen("favorites", "Statystyki", "📊")
    object Profile : Screen("profile", "Formularz", "📝")

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
