
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import java.io.OutputStream


class Processor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) : SymbolProcessor {

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("Buildable").filterIsInstance<KSClassDeclaration>()
        if (!symbols.iterator().hasNext()) return emptyList()
        val symbolList = symbols.toList()

        val packageMap = symbolList.groupBy { it.packageName }
        packageMap.forEach {
            val packageName = it.key.asString()
            val file = codeGenerator.createNewFile(
                dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
                packageName = packageName,
                fileName = "Builders"
            )
            if (packageName.isNotEmpty()) {
                file += "package $packageName\n"
            }
            file += "import utils.Builder\n"
            file += "import utils.ID\n"
            // TODO only add reflection if too many default parameters in one of the builders in the package
            file += "import kotlin.reflect.full.primaryConstructor\n"

            it.value.forEach { x -> x.accept(Visitor(file), Unit) }
            file.close()
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    /**
     * The Visitor stepping through the Source code, as we currently only parse a single annotation targeting classes
     * the logic takes place in this class. If more annotations are added the logic needs to be adapted.
     */

    inner class Visitor(private val file: OutputStream) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            if(classDeclaration.classKind == ClassKind.ENUM_CLASS || classDeclaration.classKind == ClassKind.OBJECT) {
                // TODO don't know how to handle enums or objects.
                return
            }
            val metaBuilder = BuildFunction.fromDeclaration(classDeclaration)
            val createBuildFunction = metaBuilder.createBuildFunction()
            val resetFunction = metaBuilder.resetFunction()

            val parameterList = metaBuilder.builderParameters()
            val classDeclarationString = metaBuilder.classDeclaration()
            file += classDeclarationString {
                +parameterList
                +"fun buildPreserving(lambda : ${classDeclaration.builderWithGenerics}.() -> Unit) : ${classDeclaration.nameWithGenerics}" {
                    +"this.apply(lambda)"
                    +"return build()"
                }
                +"fun build(lambda: ${classDeclaration.builderWithGenerics}.() -> Unit) : ${classDeclaration.nameWithGenerics}" {
                    +"val result = buildPreserving(lambda)"
                    +"reset()"
                    +"return result"
                }
                +"override fun reset()" {
                    +resetFunction
                }
                +createBuildFunction
            } + "\n" + metaBuilder.asBuilderExtension() + metaBuilder.asWeakBuilderExtension()
        }
    }
}

/**
 * Get the keyword translation for default values from the passed argument by splitting along "," and then along "="
 */
fun splitDefaults(default: String): Map<String, String> {
    val targets = default.split(",").map { it.trim() }
    return targets.associate { it.splitOnce("=") }
}

/**
 * Separate a string at a target delimiter, exactly once.
 */
fun String.splitOnce(delimiter: String): Pair<String, String> {
    val index = this.indexOf(delimiter)
    return if (index == -1) {
        Pair(this, "")
    } else {
        Pair(this.substring(0, index), this.substring(index + delimiter.length))
    }
}