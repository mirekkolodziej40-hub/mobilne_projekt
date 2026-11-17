package pl.uczelnia.projekt.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.uczelnia.projekt.data.Priorytet

private val LightColors = lightColorScheme(
    primary = Color(0xFF2F4A7A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD9E3F8),
    onPrimaryContainer = Color(0xFF0F2447),
    secondary = Color(0xFFB85A00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDCC2),
    onSecondaryContainer = Color(0xFF3A1B00),
    tertiary = Color(0xFFE8710A),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBC2),
    onTertiaryContainer = Color(0xFF331500),
    background = Color(0xFFF8F9FC),
    onBackground = Color(0xFF1A1C20),
    surface = Color(0xFFF8F9FC),
    onSurface = Color(0xFF1A1C20),
    surfaceVariant = Color(0xFFE1E4EC),
    onSurfaceVariant = Color(0xFF44474F),
    outline = Color(0xFF757780)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFAFC6F5),
    onPrimary = Color(0xFF14305E),
    primaryContainer = Color(0xFF2B4676),
    onPrimaryContainer = Color(0xFFD9E3F8),
    secondary = Color(0xFFFFB77C),
    onSecondary = Color(0xFF4A2600),
    secondaryContainer = Color(0xFF6B3900),
    onSecondaryContainer = Color(0xFFFFDCC2),
    tertiary = Color(0xFFFF9A45),
    onTertiary = Color(0xFF4A2200),
    tertiaryContainer = Color(0xFF6B3500),
    onTertiaryContainer = Color(0xFFFFDBC2),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE2E2E9),
    surface = Color(0xFF111318),
    onSurface = Color(0xFFE2E2E9),
    surfaceVariant = Color(0xFF44474F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF8E909A)
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

private val AppTypography = Typography(
    headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 36.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontSize = 24.sp, lineHeight = 32.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 24.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.1.sp),
    titleSmall = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium)
)

/** Kolory plakietki priorytetu: tło i tekst, czytelne w obu motywach. */
data class PriorytetKolory(val tlo: Color, val tekst: Color)

@Composable
fun priorytetKolory(p: Priorytet): PriorytetKolory {
    val dark = isSystemInDarkTheme()
    return when (p) {
        Priorytet.WYSOKI -> if (dark) PriorytetKolory(Color(0xFF7A1F1F), Color(0xFFFFDAD6)) else PriorytetKolory(Color(0xFFFFDAD6), Color(0xFF8C1D18))
        Priorytet.SREDNI -> if (dark) PriorytetKolory(Color(0xFF6B4E00), Color(0xFFFFE08A)) else PriorytetKolory(Color(0xFFFFE9A8), Color(0xFF5C4300))
        Priorytet.NISKI -> if (dark) PriorytetKolory(Color(0xFF1F5130), Color(0xFFB7F0C4)) else PriorytetKolory(Color(0xFFC8F0D2), Color(0xFF14532D))
    }
}

@Composable
fun ProjektTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors: ColorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, shapes = AppShapes, typography = AppTypography, content = content)
}
