package core.results.plots.render

import org.jetbrains.kotlinx.kandy.dsl.categorical
import org.jetbrains.kotlinx.kandy.ir.bindings.NonPositionalMappingParameters
import org.jetbrains.kotlinx.kandy.ir.scale.NonPositionalCategoricalScale
import org.jetbrains.kotlinx.kandy.letsplot.internal.LetsPlotNonPositionalMappingParametersContinuous
import org.jetbrains.kotlinx.kandy.letsplot.layers.builders.aes.WithFillColor
import org.jetbrains.kotlinx.kandy.letsplot.scales.guide.LegendType
import org.jetbrains.kotlinx.kandy.util.color.Color
import java.awt.AlphaComposite
import java.awt.Image
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.InputStream
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.roundToInt

@Suppress("MagicNumber")
/** Default plot width and height in pixels. */
val DEFAULT_PLOT_SIZE = 1200 to 600

/** Default alpha/opacity used for filled geometries. */
const val DEFAULT_ALPHA = 0.8
private const val COLOR_LABEL = "color"

/**
 * Helper to apply a categorical color scale to a fillColor mapping from a prepared ColorScale.
 */
fun WithFillColor.fillColorFromMap(colorColumn: String, colorMap: ColorScale, legendTitle: String = COLOR_LABEL) {
    fillColor(colorColumn) {
        applyScale(colorMap, legendTitle)
    }
}

/** Apply the provided ColorScale to a non-positional color mapping and name its legend. */
fun LetsPlotNonPositionalMappingParametersContinuous<Any?, Color>.applyScale(
    colorMap: ColorScale,
    legendTitle: String = COLOR_LABEL
) {
    if (colorMap.range.size <= 1) {
        legend.type = LegendType.None
    }

    legend.name = legendTitle
    scale = categorical(
        domain = colorMap.domain,
        range = colorMap.range
    )
}

/** Build a Boolean categorical scale mapping true/false to two values. */
fun <RangeType> NonPositionalMappingParameters<*, *>.booleanScale(
    positive: RangeType,
    negative: RangeType
): NonPositionalCategoricalScale<Boolean, RangeType> = categorical(
    true to positive,
    false to negative,
)

/** Convenience for a Boolean color scale mapping to two Color values. */
fun NonPositionalMappingParameters<*, *>.booleanColorScale(
    positive: Color = Color.Companion.RED,
    negative: Color = Color.Companion.BLACK
) =
    booleanScale(positive, negative)

/** Provides the mobiTopp logo lazily from classpath. */
object LogoProvider {
    val logo: BufferedImage by lazy {
        val stream: InputStream = LogoProvider::class.java.getResourceAsStream("/logo/mobiTopp_logo.png")
            ?: error("Logo resource not found in classpath: /logo/mobiTopp_logo.png")
        stream.use { ImageIO.read(it) }
    }
}

/**
 * Overlay a PNG logo onto a base PNG, aligned top-right.
 *
 * @param basePng Path to base PNG image
 * @param outPng Path to write the resulting PNG
 * @param scale Fraction of base image width that logo should occupy (e.g., 0.15 = 15%)
 * @param alpha Opacity of the logo [0f..1f]
 * @param marginPx Margin from edges (top and right)
 */
fun overlayLogoTopRight(
    basePng: Path,
    outPng: Path,
    scale: Double = 0.15,
    alpha: Float = 1.0f,
    marginPx: Int = 8
) {
    require(scale > 0.0) { "scale must be > 0" }
    require(alpha in 0f..1f) { "alpha must be in [0, 1]" }

    val base = ImageIO.read(basePng.toFile())
        ?: error("Failed to read base image: $basePng")

    val logo = LogoProvider.logo

    // Scale logo to desired width (relative to base)
    val targetLogoWidth = (base.width * scale).roundToInt().coerceAtLeast(1)
    val aspect = logo.height / logo.width.toDouble()
    val targetLogoHeight = (targetLogoWidth * aspect).roundToInt()
    val scaledLogo = logo.getScaledInstance(targetLogoWidth, targetLogoHeight, Image.SCALE_SMOOTH)

    // Compose into a new image
    val out = BufferedImage(base.width, base.height, BufferedImage.TYPE_INT_ARGB)
    val g2 = out.createGraphics()
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)

    // Draw base
    g2.drawImage(base, 0, 0, null)

    // Compute top-right coordinates
    val x = base.width - marginPx - targetLogoWidth
    val y = marginPx

    // Draw logo with alpha
    g2.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
    g2.drawImage(scaledLogo, x, y, null)

    g2.dispose()

    ImageIO.write(out, "PNG", outPng.toFile())
}
