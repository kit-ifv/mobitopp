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

/**
 * Provides random RGB colors from a predefined set of KIT colors.
 */
object RandomRGBProvider {

    private val random = Random(42L)

    private val colors = KIT_GREEN.darkerShades(3) + KIT_RED.darkerShades(3) + KIT_BLUE.darkerShades(3) + listOf(
        KIT_BLACK, KIT_GREY, KIT_LIGHT_GREY, KIT_BROWN, KIT_GREEN, KIT_TEAL,
        KIT_MAYGREEN, KIT_YELLOW, KIT_ORANGE, KIT_RED, KIT_PURPLE, KIT_BLUE,
        KIT_STEELBLUE, KIT_CYAN,
    ).distinct().shuffled(random)

    /**
     * Returns the next random [RGB] color.
     *
     * @return A random [RGB] color.
     */
    fun next(): RGB = colors.random(random)
}

/**
 * Returns a random [RGB] color from the [RandomRGBProvider].
 *
 * @return A random [RGB] color.
 */
fun randomColor() = RandomRGBProvider.next()

/**
 * Returns [KIT_GREEN] if [value] is true, otherwise [KIT_BLUE].
 *
 * @param value The boolean value.
 * @return A [RGB] color.
 */
fun boolColor(value: Boolean) = if (value) KIT_GREEN else KIT_BLUE

/**
 * Returns a predefined color for common transport mode strings.
 *
 * @param modeString The transport mode string (e.g., "bike", "car").
 * @return A [RGB] color representing the mode.
 */
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

/**
 * Validates that an RGB component value is between 0 and 255.
 */
private fun validateRgbValue(label: String, value: Int) = require(value in (0..MAX_COLOR_INT)) {
    "RGB values must be between 0 and 255 but $label is $value!"
}

private const val CIRCLE_DEGREES = 360

/**
 * Represents a color in the RGB color space.
 *
 * @property r Red component (0-255).
 * @property g Green component (0-255).
 * @property b Blue component (0-255).
 */
data class RGB(val r: Int, val g: Int, val b: Int) {

    init {
        validateRgbValue("r", r)
        validateRgbValue("g", g)
        validateRgbValue("b", b)
    }

    /**
     * Converts this [RGB] color to a Kandy [StandardColor.RGB].
     *
     * @return A [StandardColor.RGB] object.
     */
    fun toColor(): StandardColor.RGB = Color.rgb(r, g, b)
}

/**
 * Converts this [RGB] color to HSL.
 */
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

/**
 * Represents a color in the HSL color space.
 */
private data class HSL(val h: Double, val s: Double, val l: Double) {

    init {
        require(0.0 <= h && h < CIRCLE_DEGREES) { "HSL hue value should be in [0,360) but is $h!" }
        require(s in 0.0..1.0) { "HSL saturation value should be in [0,1] but is $s!" }
        require(l in 0.0..1.0) { "HSL lightness value should be in [0,1] but is $l!" }
    }

    /**
     * Converts this [HSL] color back to [RGB].
     *
     * @return An [RGB] object.
     */
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
            ((b + m) * MAX_COLOR_INT).toInt(),
        )
    }

    /**
     * Converts this [HSL] color to a Kandy [Color].
     *
     * @return A [Color] object.
     */
    fun toColor(): Color = toRbg().toColor()
}

/**
 * Converts a hex color to [RGB].
 */
@Suppress("MagicNumber")
private fun HexColor.hexToRbg() = hexString.removePrefix("#").let {
    val r = it.substring(0, 2).toInt(16)
    val g = it.substring(2, 4).toInt(16)
    val b = it.substring(4, 6).toInt(16)

    RGB(r, g, b)
}

/**
 * Converts a Kandy [Color] to [RGB].
 *
 * @receiver The Kandy [Color] to convert.
 * @return An [RGB] object.
 * @throws IllegalStateException If the color type is not supported.
 */
fun Color.toRgb() = when (this) {
    is StandardColor.RGB -> RGB(r, g, b)
    is StandardColor.RGBA -> rgb.let { RGB(it.r, it.g, it.b) }
    is StandardColor.Hex -> this.hexToRbg()
    is StandardColor.AsHexColor -> this.hex.hexToRbg()
    else -> error("Cannot rgb values from ${this::class.simpleName}: '$this'")
}

/**
 * Converts a Kandy [Color] to [HSL].
 */
private fun Color.toHsl() = this.toRgb().toHsl()

/**
 * Scales the lightness of a [Color] by a factor.
 *
 * @receiver The [Color] to scale.
 * @param factor The scaling factor (>= 0).
 * @return A new [Color] with scaled lightness.
 */
fun Color.scaleLightness(factor: Double): Color = toRgb().scaleLightness(factor).toColor()

/**
 * Scales the lightness of an [RGB] color by a factor.
 *
 * @receiver The [RGB] color to scale.
 * @param factor The scaling factor (>= 0).
 * @return A new [RGB] color with scaled lightness.
 */
fun RGB.scaleLightness(factor: Double): RGB = require(factor >= 0.0) {
    "Scaling factor for color lightness should not be negative but is $factor!"
}.let {
    this.toHsl().let {
        val scaledLightness = (it.l * factor).coerceIn(0.0, 1.0)
        it.copy(l = scaledLightness)
    }.toRbg()
}

/**
 * Sets the lightness of a [Color].
 *
 * @receiver The [Color] to modify.
 * @param lightness The new lightness value (0-1).
 * @return A new [Color] with the specified lightness.
 */
fun Color.withLightness(lightness: UnitIntervalValue): Color = toRgb().withLightness(lightness).toColor()

/**
 * Sets the lightness of an [RGB] color.
 *
 * @receiver The [RGB] color to modify.
 * @param lightness The new lightness value (0-1).
 * @return A new [RGB] color with the specified lightness.
 */
fun RGB.withLightness(lightness: UnitIntervalValue): RGB = this.toHsl().copy(l = lightness.toDouble()).toRbg()

/**
 * Scales the saturation of a [Color] by a factor.
 *
 * @receiver The [Color] to scale.
 * @param factor The scaling factor (>= 0).
 * @return A new [Color] with scaled saturation.
 */
fun Color.scaleSaturation(factor: Double): Color = toRgb().scaleSaturation(factor).toColor()

/**
 * Scales the saturation of an [RGB] color by a factor.
 *
 * @receiver The [RGB] color to scale.
 * @param factor The scaling factor (>= 0).
 * @return A new [RGB] color with scaled saturation.
 */
fun RGB.scaleSaturation(factor: Double): RGB = require(factor >= 0.0) {
    "Scaling factor for color saturation should not be negative but is $factor!"
}.let {
    this.toHsl().let {
        val scaledSaturation = (it.l * factor).coerceIn(0.0, 1.0)
        it.copy(s = scaledSaturation)
    }.toRbg()
}

/**
 * Sets the saturation of a [Color].
 *
 * @receiver The [Color] to modify.
 * @param saturation The new saturation value (0-1).
 * @return A new [Color] with the specified saturation.
 */
fun Color.withSaturation(saturation: UnitIntervalValue): Color = toRgb().withSaturation(saturation).toColor()

/**
 * Sets the saturation of an [RGB] color.
 *
 * @receiver The [RGB] color to modify.
 * @param saturation The new saturation value (0-1).
 * @return A new [RGB] color with the specified saturation.
 */
fun RGB.withSaturation(saturation: UnitIntervalValue): RGB = this.toHsl().copy(s = saturation.toDouble()).toRbg()

/**
 * Shifts the hue of a [Color] by a given amount.
 *
 * @receiver The [Color] to shift.
 * @param by The amount to shift the hue in degrees.
 * @return A new [Color] with shifted hue.
 */
fun Color.shiftHue(by: Double): Color = toRgb().shiftHue(by).toColor()

/**
 * Shifts the hue of an [RGB] color by a given amount.
 *
 * @receiver The [RGB] color to shift.
 * @param by The amount to shift the hue in degrees.
 * @return A new [RGB] color with shifted hue.
 */
fun RGB.shiftHue(by: Double): RGB = this.toHsl().let {
    val shiftedHue = ((it.h + by) % 360.0).let { hue ->
        if (hue < 0.0) {
            hue + 360
        } else {
            hue
        }
    }
    it.copy(h = shiftedHue)
}.toRbg()

/**
 * Sets the hue of a [Color].
 *
 * @receiver The [Color] to modify.
 * @param hue The new hue value in degrees (0-360).
 * @return A new [Color] with the specified hue.
 */
fun Color.withHue(hue: Double): Color = toRgb().withHue(hue).toColor()

/**
 * Sets the hue of an [RGB] color.
 *
 * @receiver The [RGB] color to modify.
 * @param hue The new hue value in degrees (0-360).
 * @return A new [RGB] color with the specified hue.
 */
fun RGB.withHue(hue: Double): RGB = this.toHsl().let {
    val newHue = (hue % 360.0).let { h ->
        if (h < 0.0) {
            h + 360
        } else {
            h
        }
    }
    it.copy(h = newHue).toRbg()
}

/**
 * Generates [n] darker shades of a [Color].
 *
 * @receiver The base [Color].
 * @param n The number of shades to generate.
 * @return A list of [n] + 1 colors (including the base color).
 */
fun Color.darkerShades(n: Int): List<Color> = toRgb().darkerShades(n).map { it.toColor() }

/**
 * Generates [n] darker shades of an [RGB] color.
 *
 * @receiver The base [RGB] color.
 * @param n The number of shades to generate.
 * @return A list of [n] + 1 colors (including the base color).
 */
fun RGB.darkerShades(n: Int): List<RGB> {
    val step = 1.0 / n

    return (0..n).map {
        1.0 - it * step
    }.map {
        this.scaleLightness(it)
    }.toList()
}

/**
 * Generates [n] lighter shades of a [Color].
 *
 * @receiver The base [Color].
 * @param n The number of shades to generate.
 * @return A list of [n] + 1 colors (including the base color).
 */
fun Color.lighterShades(n: Int): List<Color> = toRgb().lighterShades(n).map { it.toColor() }

/**
 * Generates [n] lighter shades of an [RGB] color.
 *
 * @receiver The base [RGB] color.
 * @param n The number of shades to generate.
 * @return A list of [n] + 1 colors (including the base color).
 */
fun RGB.lighterShades(n: Int): List<RGB> {
    val lightness = this.toHsl().l
    val step = (1 - lightness) / n

    return (0..n).map {
        lightness + it * step
    }.map {
        this.withLightness(it.coerceIn(0.0, 1.0).share())
    }.toList()
}

/**
 * Generates a color scale by shifting the hue of a [Color].
 *
 * @receiver The base [Color].
 * @param n The number of colors in the scale.
 * @param range The total hue range to cover in degrees.
 * @return A list of [n] + 1 colors.
 */
fun Color.hueScale(n: Int, range: Double = CIRCLE_DEGREES.toDouble()): List<Color> = toRgb().hueScale(n, range).map {
    it.toColor()
}

/**
 * Generates a color scale by shifting the hue of an [RGB] color.
 *
 * @receiver The base [RGB] color.
 * @param n The number of colors in the scale.
 * @param range The total hue range to cover in degrees.
 * @return A list of [n] + 1 colors.
 */
fun RGB.hueScale(n: Int, range: Double = CIRCLE_DEGREES.toDouble()): List<RGB> {
    val hue = this.toHsl().h
    val step = range / n

    return (0..n).map {
        (hue + it * step) % CIRCLE_DEGREES
    }.map {
        this.withHue(it.coerceIn(0.0, CIRCLE_DEGREES.toDouble()))
    }.toList()
}
