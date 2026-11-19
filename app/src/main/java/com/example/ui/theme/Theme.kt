package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

@Immutable
data class TarmeemExtendedColors(
  val gold: Color,
  val gold2: Color,
  val green: Color,
  val surface2: Color,
  val line: Color,
  val muted: Color,
  val seerahAccent: Color,
  val aqeedahAccent: Color,
  val quranAccent: Color,
  val fiqhAccent: Color,
  val asmaAccent: Color,
  val isDark: Boolean,
)

private val DarkExtendedColors =
  TarmeemExtendedColors(
    gold = DarkGold,
    gold2 = DarkGold2,
    green = DarkGreen,
    surface2 = DarkSurface2,
    line = DarkLine,
    muted = DarkMuted,
    seerahAccent = SeerahAccentDark,
    aqeedahAccent = AqeedahAccentDark,
    quranAccent = QuranAccentDark,
    fiqhAccent = FiqhAccentDark,
    asmaAccent = AsmaAccentDark,
    isDark = true,
  )

private val LightExtendedColors =
  TarmeemExtendedColors(
    gold = LightGold,
    gold2 = LightGold2,
    green = LightGreen,
    surface2 = LightSurface2,
    line = LightLine,
    muted = LightMuted,
    seerahAccent = SeerahAccentLight,
    aqeedahAccent = AqeedahAccentLight,
    quranAccent = QuranAccentLight,
    fiqhAccent = FiqhAccentLight,
    asmaAccent = AsmaAccentLight,
    isDark = false,
  )

val LocalTarmeemColors = staticCompositionLocalOf { DarkExtendedColors }

private val TarmeemDarkColorScheme =
  darkColorScheme(
    primary = DarkGold,
    onPrimary = DarkOnGold,
    primaryContainer = DarkSurface2,
    onPrimaryContainer = DarkGold2,
    secondary = DarkGreen,
    onSecondary = DarkOnGold,
    secondaryContainer = DarkSurface2,
    onSecondaryContainer = DarkText,
    tertiary = DarkGold2,
    onTertiary = DarkOnGold,
    background = DarkBg,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = DarkSurface2,
    onSurfaceVariant = DarkMuted,
    outline = DarkLine,
    outlineVariant = DarkLine,
  )

private val TarmeemLightColorScheme =
  lightColorScheme(
    primary = LightGold,
    onPrimary = LightOnGold,
    primaryContainer = LightSurface2,
    onPrimaryContainer = LightGold2,
    secondary = LightGreen,
    onSecondary = LightOnGold,
    secondaryContainer = LightSurface2,
    onSecondaryContainer = LightText,
    tertiary = LightGold2,
    onTertiary = LightOnGold,
    background = LightBg,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightSurface2,
    onSurfaceVariant = LightMuted,
    outline = LightLine,
    outlineVariant = LightLine,
  )

private val TarmeemShapes =
  Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
  )

@Composable
fun TarmeemTheme(
  darkTheme: Boolean = true,
  fontScale: Float = 1.0f,
  comfortLineSpacing: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) TarmeemDarkColorScheme else TarmeemLightColorScheme
  val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
  val typography = buildTarmeemTypography(fontScale = fontScale, comfortLineSpacing = comfortLineSpacing)

  CompositionLocalProvider(
    LocalLayoutDirection provides LayoutDirection.Rtl,
    LocalTarmeemColors provides extendedColors,
  ) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = typography,
      shapes = TarmeemShapes,
      content = content,
    )
  }
}

object TarmeemThemeTokens {
  val colors: TarmeemExtendedColors
    @Composable get() = LocalTarmeemColors.current
}
