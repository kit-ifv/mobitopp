@file:Suppress("MagicNumber", "TooManyFunctions")

package core.results.plots

import edu.kit.ifv.units.UnitIntervalValue
import edu.kit.ifv.units.share
import org.jetbrains.kotlinx.kandy.util.color.Color
import org.jetbrains.kotlinx.kandy.util.color.StandardColor
import kotlin.math.abs
import kotlin.random.Random

private typealias HexColor = StandardColor.Hex

val KIT_BLACK: RGB = Color.hex("#000000").toRgb()
val KIT_GREY: RGB = Color.hex("#404040").toRgb()
val KIT_LIGHT_GREY: RGB = Color.hex("#7F7F7F").toRgb()
val KIT_WHITE: RGB = Color.hex("#FFFFFF").toRgb()
val KIT_EGGSHELL_WHITE: RGB = Color.hex("#EEECE1").toRgb()
val KIT_BEIGE: RGB = Color.hex("#D9D9D9").toRgb()
val KIT_BROWN: RGB = Color.hex("#A7822E").toRgb()

val KIT_GREEN: RGB = Color.hex("#00876C").toRgb()
val KIT_TEAL: RGB = Color.hex("#009682").toRgb()
val KIT_MAYGREEN: RGB = Color.hex("#77A200").toRgb()
val KIT_YELLOW: RGB = Color.hex("#FCE500").toRgb()
val KIT_ORANGE: RGB = Color.hex("#DF9B1B").toRgb()
val KIT_RED: RGB = Color.hex("#A22223").toRgb()
val KIT_PURPLE: RGB = Color.hex("#A3107C").toRgb()
val KIT_BLUE: RGB = Color.hex("#4664AA").toRgb()
val KIT_STEELBLUE: RGB = Color.hex("#1F497D").toRgb()
val KIT_CYAN: RGB = Color.hex("#079EDE").toRgb()

val redishKitColors = listOf(KIT_RED, KIT_PURPLE)
val greenishKitColors = listOf(KIT_GREEN, KIT_TEAL, KIT_MAYGREEN)
val yellowishKitColors = listOf(KIT_YELLOW, KIT_ORANGE)
val blueishKitColors = listOf(KIT_BLUE, KIT_STEELBLUE, KIT_CYAN)

val kitGreenShades = KIT_GREEN.lighterShades(5)
val kitBlueShades = KIT_BLUE.lighterShades(5)
val kitRedShades = KIT_RED.lighterShades(5)

object RandomRGBProvider {

    private val random = Random(42L)

    private val colors = KIT_GREEN.lighterShades(3) + KIT_RED.lighterShades(3) + KIT_BLUE.lighterShades(3) + listOf(
        KIT_BLACK, KIT_GREY, KIT_LIGHT_GREY, KIT_BROWN, KIT_GREEN, KIT_TEAL,
        KIT_MAYGREEN, KIT_YELLOW, KIT_ORANGE, KIT_RED, KIT_PURPLE, KIT_BLUE,
        KIT_STEELBLUE, KIT_CYAN
    ).distinct().shuffled(random)

    fun next(): RGB = colors.random(random)
}

fun randomColor() = RandomRGBProvider.next()

fun boolColor(value: Boolean) = if (value) KIT_GREEN else KIT_BLUE

@Suppress("CyclomaticComplexMethod")
fun modeStringColor(modeString: String): RGB = when (modeString.lowercase()) {
    "bike" -> kitBlueShades[0]
    "e_scooter", "e scooter" -> kitBlueShades[1]
    "pedelec" -> kitBlueShades[2]
    "bikesharing", "bike_sharing", "bike sharing" -> kitBlueShades[3]

    "car" -> kitRedShades[0]
    "carsharing_station", "carsharing station" -> kitRedShades[1]
    "carsharing_free", "carsharing free" -> kitRedShades[2]
    "passenger" -> KIT_ORANGE

    "pedestrian" -> KIT_CYAN
    "publictransport", "public_transport", "public transport" -> KIT_MAYGREEN
    "truck" -> KIT_BROWN
    "taxi" -> KIT_YELLOW

    "ridepooling", "ride_pooling", "ride pooling" -> KIT_PURPLE

    else -> randomColor()
}

private const val MAX_COLOR_INT = 255

private fun validateRgbValue(label: String, value: Int) =
    require(value in (0..MAX_COLOR_INT)) { "RGB values must be between 0 and 255 but $label is $value!" }

private const val CIRCLE_DEGREES = 360

data class RGB(val r: Int, val g: Int, val b: Int) {

    init {
        validateRgbValue("r", r)
        validateRgbValue("g", g)
        validateRgbValue("b", b)
    }

    fun toColor(): StandardColor.RGB = Color.rgb(r, g, b)
}

@Suppress("MagicNumber")
private fun RGB.toHsl(): HSL {
    val r = this.r / 255.0
    val g = this.g / 255.0
    val b = this.b / 255.0

    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    val delta = max - min

    val l = (max + min) / 2.0

    val s = if (delta == 0.0) {
        0.0
    } else {
        delta / (1 - abs(2 * l - 1))
    }

    val h = when {
        delta == 0.0 -> 0.0
        max == r -> ((g - b) / delta + (if (g < b) 6 else 0)) * 60
        max == g -> ((b - r) / delta + 2) * 60
        else -> ((r - g) / delta + 4) * 60
    }

    return HSL(h % CIRCLE_DEGREES, s, l)
}

private data class HSL(val h: Double, val s: Double, val l: Double) {

    init {
        require(0.0 <= h && h < CIRCLE_DEGREES) { "HSL hue value should be in [0,360) but is $h!" }
        require(s in 0.0..1.0) { "HSL saturation value should be in [0,1] but is $s!" }
        require(l in 0.0..1.0) { "HSL lightness value should be in [0,1] but is $l!" }
    }

    @Suppress("MagicNumber")
    fun toRbg(): RGB {
        val c = (1 - abs(2 * this.l - 1)) * this.s
        val x = c * (1 - abs(this.h / 60 % 2 - 1))
        val m = this.l - c / 2

        val (r, g, b) = when {
            this.h < 60 -> Triple(c, x, 0.0)
            this.h < 120 -> Triple(x, c, 0.0)
            this.h < 180 -> Triple(0.0, c, x)
            this.h < 240 -> Triple(0.0, x, c)
            this.h < 300 -> Triple(x, 0.0, c)
            else -> Triple(c, 0.0, x)
        }

        return RGB(
            ((r + m) * MAX_COLOR_INT).toInt(),
            ((g + m) * MAX_COLOR_INT).toInt(),
            ((b + m) * MAX_COLOR_INT).toInt()
        )
    }

    fun toColor(): Color = toRbg().toColor()
}

@Suppress("MagicNumber")
private fun HexColor.hexToRbg() = hexString.removePrefix("#").let {
    val r = it.substring(0, 2).toInt(16)
    val g = it.substring(2, 4).toInt(16)
    val b = it.substring(4, 6).toInt(16)

    RGB(r, g, b)
}

fun Color.toRgb() = when (this) {
    is StandardColor.RGB -> RGB(r, g, b)
    is StandardColor.RGBA -> rgb.let { RGB(it.r, it.g, it.b) }
    is StandardColor.Hex -> this.hexToRbg()
    is StandardColor.AsHexColor -> this.hex.hexToRbg()
    else -> error("Cannot rgb values from ${this::class.simpleName}: '$this'")
}

private fun Color.toHsl() = this.toRgb().toHsl()

fun Color.scaleLightness(factor: Double): Color = toRgb().scaleLightness(factor).toColor()

fun RGB.scaleLightness(factor: Double): RGB =
    require(factor >= 0.0) {
        "Scaling factor for color lightness should not be negative but is $factor!"
    }.let {
        this.toHsl().let {
            val scaledLightness = (it.l * factor).coerceIn(0.0, 1.0)
            it.copy(l = scaledLightness)
        }.toRbg()
    }

fun Color.withLightness(lightness: UnitIntervalValue): Color = toRgb().withLightness(lightness).toColor()

fun RGB.withLightness(lightness: UnitIntervalValue): RGB =
    this.toHsl().copy(l = lightness.toDouble()).toRbg()

fun Color.scaleSaturation(factor: Double): Color = toRgb().scaleSaturation(factor).toColor()

fun RGB.scaleSaturation(factor: Double): RGB =
    require(factor >= 0.0) {
        "Scaling factor for color saturation should not be negative but is $factor!"
    }.let {
        this.toHsl().let {
            val scaledSaturation = (it.l * factor).coerceIn(0.0, 1.0)
            it.copy(s = scaledSaturation)
        }.toRbg()
    }

fun Color.withSaturation(saturation: UnitIntervalValue): Color = toRgb().withSaturation(saturation).toColor()

fun RGB.withSaturation(saturation: UnitIntervalValue): RGB =
    this.toHsl().copy(s = saturation.toDouble()).toRbg()

fun Color.shiftHue(by: Double): Color = toRgb().shiftHue(by).toColor()

fun RGB.shiftHue(by: Double): RGB =
    this.toHsl().let {
        val shiftedHue = ((it.h + by) % 360.0).let { hue ->
            if (hue < 0.0) {
                hue + 360
            } else {
                hue
            }
        }
        it.copy(h = shiftedHue)
    }.toRbg()

fun Color.withHue(hue: Double): Color = toRgb().withHue(hue).toColor()

fun RGB.withHue(hue: Double): RGB =
    this.toHsl().let {
        val newHue = (hue % 360.0).let { h ->
            if (h < 0.0) {
                h + 360
            } else {
                h
            }
        }
        it.copy(h = newHue).toRbg()
    }

fun Color.darkerShades(n: Int): List<Color> = toRgb().darkerShades(n).map { it.toColor() }

fun RGB.darkerShades(n: Int): List<RGB> {
    val step = 1.0 / n

    return (0..n).map {
        1.0 - it * step
    }.map {
        this.scaleLightness(it)
    }.toList()
}

fun Color.lighterShades(n: Int): List<Color> = toRgb().lighterShades(n).map { it.toColor() }

fun RGB.lighterShades(n: Int): List<RGB> {
    val lightness = this.toHsl().l
    val step = (1 - lightness) / n

    return (0..n).map {
        lightness + it * step
    }.map {
        this.withLightness(it.coerceIn(0.0, 1.0).share())
    }.toList()
}

fun Color.hueScale(n: Int, range: Double = CIRCLE_DEGREES.toDouble()): List<Color> =
    toRgb().hueScale(n, range).map { it.toColor() }

fun RGB.hueScale(n: Int, range: Double = CIRCLE_DEGREES.toDouble()): List<RGB> {
    val hue = this.toHsl().h
    val step = range / n

    return (0..n).map {
        (hue + it * step) % CIRCLE_DEGREES
    }.map {
        this.withHue(it.coerceIn(0.0, CIRCLE_DEGREES.toDouble()))
    }.toList()
}
