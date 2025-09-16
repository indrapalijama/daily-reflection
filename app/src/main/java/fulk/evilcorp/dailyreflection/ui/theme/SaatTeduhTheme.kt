package fulk.evilcorp.dailyreflection.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),         // Light Blue
    onPrimary = Color(0xFF000000),       // Black text on light blue
    secondary = Color(0xFFFFD54F),       // Amber
    onSecondary = Color(0xFF000000),     // Black text
    tertiary = Color(0xFFA5D6A7),        // Light Green
    onTertiary = Color(0xFF000000),      // Black text
    background = Color(0xFF0F1419),      // Deep dark blue-gray
    onBackground = Color(0xFFFFFFFF),    // White text
    surface = Color(0xFF1A1F26),         // Dark blue-gray surface
    onSurface = Color(0xFFFFFFFF),       // White text
    outline = Color(0xFF5A6B7A)          // Muted outline
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),         // Blue 700
    onPrimary = Color(0xFFFFFFFF),       // White text
    secondary = Color(0xFFFFC107),       // Amber 500
    onSecondary = Color(0xFF000000),     // Black text
    tertiary = Color(0xFF4CAF50),        // Green 500
    onTertiary = Color(0xFFFFFFFF),      // White text
    background = Color(0xFFF8FAFE),      // Very light blue-white
    onBackground = Color(0xFF1B1B1B),    // Dark text
    surface = Color(0xFFFFFFFF),         // Pure white cards
    onSurface = Color(0xFF1B1B1B),       // Dark text
    outline = Color(0xFFE0E0E0)          // Light outline
)

@Composable
fun SaatTeduhTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}