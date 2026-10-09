package cl.duoc.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

// Modo claro
val Primary = Color(0xFF0B6E5E)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryContainer = Color(0xFFD6F2EA)
val OnPrimaryContainer = Color(0xFF043B32)

val Secondary = Color(0xFF2F4858)
val OnSecondary = Color(0xFFFFFFFF)
val SecondaryContainer = Color(0xFFE3ECF1)
val OnSecondaryContainer = Color(0xFF14232C)

// Acento
val Tertiary = Color(0xFF8A5A00)
val OnTertiary = Color(0xFFFFFFFF)
val TertiaryContainer = Color(0xFFFFE9BF)
val OnTertiaryContainer = Color(0xFF3B2600)

val Error = Color(0xFFB3261E)
val OnError = Color(0xFFFFFFFF)
val ErrorContainer = Color(0xFFF9DEDC)
val OnErrorContainer = Color(0xFF410E0B)

val Background = Color(0xFFFAFBFA)
val OnBackground = Color(0xFF14201D)
val Surface = Color(0xFFFFFFFF)
val OnSurface = Color(0xFF14201D)
val SurfaceVariant = Color(0xFFE8EEEC)
val OnSurfaceVariant = Color(0xFF4A5A56)
val Outline = Color(0xFF7A8985)
val OutlineVariant = Color(0xFFCFD8D5)

// Modo oscuro
val PrimaryDark = Color(0xFF7ED8C3)
val OnPrimaryDark = Color(0xFF00382F)
val PrimaryContainerDark = Color(0xFF005144)
val OnPrimaryContainerDark = Color(0xFFD6F2EA)

val SecondaryDark = Color(0xFFB8CAD6)
val OnSecondaryDark = Color(0xFF223340)
val SecondaryContainerDark = Color(0xFF384A57)
val OnSecondaryContainerDark = Color(0xFFE3ECF1)

val TertiaryDark = Color(0xFFF2BE61)
val OnTertiaryDark = Color(0xFF452B00)
val TertiaryContainerDark = Color(0xFF653F00)
val OnTertiaryContainerDark = Color(0xFFFFE9BF)

val ErrorDark = Color(0xFFF2B8B5)
val OnErrorDark = Color(0xFF601410)
val ErrorContainerDark = Color(0xFF8C1D18)
val OnErrorContainerDark = Color(0xFFF9DEDC)

val BackgroundDark = Color(0xFF0F1513)
val OnBackgroundDark = Color(0xFFE0E8E5)
val SurfaceDark = Color(0xFF151C1A)
val OnSurfaceDark = Color(0xFFE0E8E5)
val SurfaceVariantDark = Color(0xFF3F4946)
val OnSurfaceVariantDark = Color(0xFFBFC9C5)
val OutlineDark = Color(0xFF899491)
val OutlineVariantDark = Color(0xFF3F4946)

// Colores estados
@Immutable
data class EstadoColor(
    val fondo: Color,
    val contenido: Color,
)

@Immutable
data class ExtendedColors(
    val pendiente: EstadoColor,
    val vencido: EstadoColor,
    val pagado: EstadoColor,
    val enRevision: EstadoColor,
    val anulado: EstadoColor,
)

val LightExtendedColors = ExtendedColors(
    pendiente = EstadoColor(fondo = Color(0xFFE1ECF7), contenido = Color(0xFF1F4E79)),
    vencido = EstadoColor(fondo = Color(0xFFFFEBD0), contenido = Color(0xFF7A3E00)),
    pagado = EstadoColor(fondo = Color(0xFFD9F1E1), contenido = Color(0xFF14532D)),
    enRevision = EstadoColor(fondo = Color(0xFFEADFF5), contenido = Color(0xFF4A2C73)),
    anulado = EstadoColor(fondo = Color(0xFFE6E8E7), contenido = Color(0xFF4A5250)),
)

val DarkExtendedColors = ExtendedColors(
    pendiente = EstadoColor(fondo = Color(0xFF1B3550), contenido = Color(0xFFB9D6F2)),
    vencido = EstadoColor(fondo = Color(0xFF5A3300), contenido = Color(0xFFFFD9A8)),
    pagado = EstadoColor(fondo = Color(0xFF0F3D22), contenido = Color(0xFFB6EBC9)),
    enRevision = EstadoColor(fondo = Color(0xFF3A2757), contenido = Color(0xFFDCC8F5)),
    anulado = EstadoColor(fondo = Color(0xFF2E3533), contenido = Color(0xFFC9D1CE)),
)