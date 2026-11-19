package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val TajawalFontFamily =
  FontFamily(
    Font(R.font.tajawal, FontWeight.Normal),
    Font(R.font.tajawal, FontWeight.Medium),
    Font(R.font.tajawal, FontWeight.Bold),
  )

val AmiriFontFamily =
  FontFamily(
    Font(R.font.amiri, FontWeight.Normal),
    Font(R.font.amiri, FontWeight.Bold),
  )

val ScheherazadeFontFamily =
  FontFamily(
    Font(R.font.scheherazade_new, FontWeight.Normal),
    Font(R.font.scheherazade_new, FontWeight.Bold),
  )

fun buildTarmeemTypography(fontScale: Float = 1.0f, comfortLineSpacing: Boolean = true): Typography {
  val lineMult = if (comfortLineSpacing) 1.08f else 0.95f
  return Typography(
    displaySmall =
      TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (28f * fontScale).sp,
        lineHeight = (38f * fontScale * lineMult).sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (24f * fontScale).sp,
        lineHeight = (34f * fontScale * lineMult).sp,
      ),
    headlineSmall =
      TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (20f * fontScale).sp,
        lineHeight = (28f * fontScale * lineMult).sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (19f * fontScale).sp,
        lineHeight = (27f * fontScale * lineMult).sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = TajawalFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (16f * fontScale).sp,
        lineHeight = (23f * fontScale * lineMult).sp,
      ),
    titleSmall =
      TextStyle(
        fontFamily = TajawalFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (14f * fontScale).sp,
        lineHeight = (20f * fontScale * lineMult).sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (17f * fontScale).sp,
        lineHeight = (28f * fontScale * lineMult).sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = TajawalFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (14.5f * fontScale).sp,
        lineHeight = (22f * fontScale * lineMult).sp,
      ),
    bodySmall =
      TextStyle(
        fontFamily = TajawalFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = (12.5f * fontScale).sp,
        lineHeight = (19f * fontScale * lineMult).sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = TajawalFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = (13.5f * fontScale).sp,
        lineHeight = (18f * fontScale).sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = TajawalFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (12f * fontScale).sp,
        lineHeight = (16f * fontScale).sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = TajawalFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = (11f * fontScale).sp,
        lineHeight = (15f * fontScale).sp,
      ),
  )
}
