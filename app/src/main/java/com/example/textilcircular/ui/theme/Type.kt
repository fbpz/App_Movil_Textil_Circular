package com.example.textilcircular.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.textilcircular.R

// Fuentes de la web: títulos en Big Shoulders Display, cuerpo en Nunito
val BigShoulders = FontFamily(
    Font(R.font.big_shoulders_bold, FontWeight.Bold),
    Font(R.font.big_shoulders_black, FontWeight.Black)
)

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold)
)

private val base = Typography()

val Typography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = BigShoulders, fontWeight = FontWeight.Black),
    displayMedium = base.displayMedium.copy(fontFamily = BigShoulders, fontWeight = FontWeight.Black),
    displaySmall = base.displaySmall.copy(fontFamily = BigShoulders, fontWeight = FontWeight.Black),
    headlineLarge = base.headlineLarge.copy(fontFamily = BigShoulders, fontWeight = FontWeight.Black),
    headlineMedium = base.headlineMedium.copy(fontFamily = BigShoulders, fontWeight = FontWeight.Black),
    headlineSmall = base.headlineSmall.copy(fontFamily = BigShoulders, fontWeight = FontWeight.Bold),
    titleLarge = base.titleLarge.copy(fontFamily = BigShoulders, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
    titleMedium = base.titleMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
    titleSmall = base.titleSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
    bodyLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = base.bodyMedium.copy(fontFamily = Nunito),
    bodySmall = base.bodySmall.copy(fontFamily = Nunito),
    labelLarge = base.labelLarge.copy(fontFamily = Nunito, fontWeight = FontWeight.Bold),
    labelMedium = base.labelMedium.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold),
    labelSmall = base.labelSmall.copy(fontFamily = Nunito, fontWeight = FontWeight.SemiBold)
)
