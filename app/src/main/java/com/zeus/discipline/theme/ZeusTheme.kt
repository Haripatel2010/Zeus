package com.zeus.discipline.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable

private val ZeusDarkColorScheme = darkColorScheme(
    primary = ZeusColors.PureWhite,
    onPrimary = ZeusColors.PureBlack,

    background = ZeusColors.Background,
    onBackground = ZeusColors.TextPrimary,

    surface = ZeusColors.BackgroundSecondary,
    onSurface = ZeusColors.TextPrimary,

    surfaceVariant = ZeusColors.GlassTint,
    onSurfaceVariant = ZeusColors.TextSecondary,

    outline = ZeusColors.GlassBorder
)

private val ZeusMaterialTypography = Typography(
    displayLarge = ZeusTypography.Display,
    headlineLarge = ZeusTypography.Headline,
    titleLarge = ZeusTypography.Title,
    bodyLarge = ZeusTypography.Body,
    labelMedium = ZeusTypography.Label
)

@Composable
fun ZeusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ZeusDarkColorScheme,
        typography = ZeusMaterialTypography,
        content = content
    )
}

