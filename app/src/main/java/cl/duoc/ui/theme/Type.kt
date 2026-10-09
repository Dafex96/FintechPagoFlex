package cl.duoc.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

val PagoFlexFontFamily: FontFamily = FontFamily.SansSerif

val PagoFlexTypography = Typography(
    // Titulos grandes
    displayLarge = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 40.sp, lineHeight = 48.sp, letterSpacing = (-0.01).em,
    ),
    displayMedium = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 34.sp, lineHeight = 42.sp, letterSpacing = (-0.01).em,
    ),
    displaySmall = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp, lineHeight = 38.sp,
    ),

    // Titulos medios
    headlineLarge = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 32.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp,
    ),

    // Titulos pequeños
    titleLarge = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp, lineHeight = 28.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Medium,
        fontSize = 18.sp, lineHeight = 26.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Medium,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),

    // Texto corrido
    bodyLarge = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 18.sp, lineHeight = 28.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),

    // Botones
    labelLarge = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 24.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = PagoFlexFontFamily, fontWeight = FontWeight.Medium,
        fontSize = 13.sp, lineHeight = 18.sp,
    ),
)

// Estilo montos
val MontoGrande = TextStyle(
    fontFamily = PagoFlexFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    fontFeatureSettings = "tnum",
)

val MontoMediano = TextStyle(
    fontFamily = PagoFlexFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp,
    lineHeight = 30.sp,
    fontFeatureSettings = "tnum",
)