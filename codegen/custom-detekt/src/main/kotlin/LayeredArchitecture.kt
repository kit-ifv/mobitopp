import dev.detekt.api.Config
import dev.detekt.api.Entity
import dev.detekt.api.Finding
import dev.detekt.api.Rule
import dev.detekt.api.RuleName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective

internal const val LAYERED_ARCHITECTURE = "LayeredArchitecture"

class LayeredArchitecture(config: Config): Rule(
    config,
    "Detects violations of import direction for the specified layered architecture!",
) {

    override val ruleName = RuleName(LAYERED_ARCHITECTURE)

    private val layers: List<String> =
        config.valueOrDefault("layers", emptyList())

    private val allowSameLayerImports: Boolean =
        config.valueOrDefault("allowSameLayerImports", true)

    private val transparentLayerAccess: Boolean =
        config.valueOrDefault("transparentLayerAccess", true)


    override fun visitKtFile(file: KtFile) {
        super.visitKtFile(file)
        val filePackage = file.packageFqName.asString()
        val currentLayer = layers.find { filePackage.contains(it) } ?: return
        val currentIndex = layers.indexOf(currentLayer)

        file.importDirectives.forEach { importDirective ->
            val importedFqName = importDirective.importedFqName
            val shortName = importedFqName?.shortName()?.asString()

            val importPath = importDirective.importPath?.pathStr ?: return@forEach
            val targetLayer = layers.find { importPath.contains(it) } ?: return@forEach
            val targetIndex = layers.indexOf(targetLayer)

            val sameLayer = currentIndex == targetIndex

            val violatesSameLayerRule = !allowSameLayerImports && sameLayer
            val violatesDirectionRule = targetIndex > currentIndex
            val violatesImportDepth = !transparentLayerAccess &&
                    !sameLayer &&
                    targetIndex != currentIndex - 1

            if (violatesSameLayerRule || violatesDirectionRule || violatesImportDepth) {
                val message = buildViolationMessage(
                    name = shortName ?: importDirective.toString(),
                    currentLayer = currentLayer,
                    targetLayer = targetLayer,
                    sameLayerViolation = violatesSameLayerRule,
                    directionViolation = violatesDirectionRule,
                    depthViolation = violatesImportDepth
                )

                report(createFinding(importDirective, message))
            }
        }
    }

    private fun createFinding(importDirective: KtImportDirective, message: String): Finding {
        return Finding(
            entity = Entity.from(importDirective),
            message = message
        )
    }

    private fun buildViolationMessage(
        name: String,
        currentLayer: String,
        targetLayer: String,
        sameLayerViolation: Boolean,
        directionViolation: Boolean,
        depthViolation: Boolean
    ): String {
        return buildString {
            append("Layer '$currentLayer' is not allowed to import [$name] from '$targetLayer'.")
            when {
                directionViolation -> append(" Violates dependency direction.")
                depthViolation -> append(" Only direct layer imports are allowed.")
                sameLayerViolation -> append(" Same-layer imports are disallowed.")
            }
        }
    }
}