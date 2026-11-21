package pl.uczelnia.projekt

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class EwakuacjaItem(
    val id: Int,
    val nazwa: String,
    val kategoria: String,
    val status: String
)

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Start", Icons.Default.Home)
    object Search : Screen("search", "Szukaj", Icons.Default.Search)
    object Notifications : Screen("notifications", "Powiadomienia", Icons.Default.Notifications)
    object Favorites : Screen("favorites", "Ulubione", Icons.Default.Favorite)
    object Profile : Screen("profile", "Profil", Icons.Default.Person)

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
