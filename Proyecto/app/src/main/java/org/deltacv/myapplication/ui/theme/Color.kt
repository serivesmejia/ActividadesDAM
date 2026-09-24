package org.deltacv.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

// Main Palette - Light Mode
val LightPrimary = Color(0xFF335C67)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightBackground = Color(0xFFF4ECE1)
val LightOnBackground = Color(0xFF1C2A38)
val LightSurface = Color(0xFFFFFFFF)
val LightOnSurface = Color(0xFF1C2A38)
val LightSecondary = Color(0xFF524868)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFF9C8EB9)
val LightOnSecondaryContainer = Color(0xFFFFFFFF)

// Main Palette - Dark Mode
val DarkPrimary = Color(0xFF16242B)
val DarkOnPrimary = Color(0xFFFFFFFF)
val DarkBackground = Color(0xFF0F151B)
val DarkOnBackground = Color(0xFFF0F4F8)
val DarkSurface = Color(0xFF1C242C)
val DarkOnSurface = Color(0xFFF0F4F8)
val DarkSecondary = Color(0xFF231F33)
val DarkOnSecondary = Color(0xFFFFFFFF)
val DarkSecondaryContainer = Color(0xFF3F3859)
val DarkOnSecondaryContainer = Color(0xFFFFFFFF)

// Custom Card Color Scheme
data class CardColorScheme(
    val cardBg: Color,
    val avatarBg: Color,
    val avatarIcon: Color,
    val badgeBg: Color,
    val badgeText: Color,
    val textColor: Color,
    val iconTint: Color,
    val decorationColor: Color
)

// Card 0: Teal Theme (Ana Torres, Carlos Mendoza)
val LightCardTeal = CardColorScheme(
    cardBg = Color(0xFFD6E8ED),
    avatarBg = Color(0xFF74B4C7),
    avatarIcon = Color(0xFF0F3B47),
    badgeBg = Color(0xFFA2CFC7),
    badgeText = Color(0xFF0F3B47),
    textColor = Color(0xFF0F3B47),
    iconTint = Color(0xFF2B6D7A),
    decorationColor = Color(0xFF528A81)
)

val DarkCardTeal = CardColorScheme(
    cardBg = Color(0xFF1B2D33),
    avatarBg = Color(0xFF1F424C),
    avatarIcon = Color(0xFF47A5A5),
    badgeBg = Color(0xFF24514C),
    badgeText = Color(0xFF75C2B4),
    textColor = Color(0xFFD0E6E6),
    iconTint = Color(0xFF47A5A5),
    decorationColor = Color(0xFF3D8874)
)

// Card 1: Purple Theme (Luis Ramírez)
val LightCardPurple = CardColorScheme(
    cardBg = Color(0xFFE5E0F2),
    avatarBg = Color(0xFFA191D2),
    avatarIcon = Color(0xFF2A1B4E),
    badgeBg = Color(0xFFC2B2E3),
    badgeText = Color(0xFF2A1B4E),
    textColor = Color(0xFF2A1B4E),
    iconTint = Color(0xFF654E9B),
    decorationColor = Color(0xFF9F8DCE)
)

val DarkCardPurple = CardColorScheme(
    cardBg = Color(0xFF282238),
    avatarBg = Color(0xFF3A3152),
    avatarIcon = Color(0xFF8D6FD1),
    badgeBg = Color(0xFF493A6B),
    badgeText = Color(0xFFBBA2EC),
    textColor = Color(0xFFE1D8F5),
    iconTint = Color(0xFF8D6FD1),
    decorationColor = Color(0xFF7B5BAF)
)

// Card 2: Peach / Orange Theme (Sofía Herrera)
val LightCardPeach = CardColorScheme(
    cardBg = Color(0xFFFBE1CF),
    avatarBg = Color(0xFFEB8E68),
    avatarIcon = Color(0xFF4A1E10),
    badgeBg = Color(0xFFF2B495),
    badgeText = Color(0xFF4A1E10),
    textColor = Color(0xFF4A1E10),
    iconTint = Color(0xFFC45B38),
    decorationColor = Color(0xFFE07851)
)

val DarkCardPeach = CardColorScheme(
    cardBg = Color(0xFF352123),
    avatarBg = Color(0xFF523032),
    avatarIcon = Color(0xFFE66A4E),
    badgeBg = Color(0xFF6B3830),
    badgeText = Color(0xFFF2A18D),
    textColor = Color(0xFFFAD5C8),
    iconTint = Color(0xFFE66A4E),
    decorationColor = Color(0xFFD45D43)
)

fun getCardColorScheme(index: Int, isDark: Boolean): CardColorScheme {
    return if (isDark) {
        when (index % 3) {
            0 -> DarkCardTeal
            1 -> DarkCardPurple
            else -> DarkCardPeach
        }
    } else {
        when (index % 3) {
            0 -> LightCardTeal
            1 -> LightCardPurple
            else -> LightCardPeach
        }
    }
}
