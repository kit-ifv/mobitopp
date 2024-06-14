
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import java.io.OutputStream

class Props(
    val name: String,
    val type: KSTypeReference,
    private val hasDefault: Boolean = false,
    private val genericLabels: List<String> = emptyList()
) {

    constructor(property: KSPropertyDeclaration) : this(property.simpleName.asString(), property.type)
    constructor(
        property: KSValueParameter,
        genericLabels: List<String> = emptyList()
    ) : this(property.name?.asString()!!, property.type, property.hasDefault, genericLabels)

    enum class State {
        PRIMITIVE {
            override fun variability(): String {
                return "var"
            }

            override fun defaultValue(type: String): String {
                return "null"
            }
        },
        OBJECT {
            override fun variability(): String {
                return "var"
            }

            override fun type(type: String, fullRef: KSTypeReference): String {
                return fullRef.resolve().declaration.qualifiedName?.asString() ?: ""
            }

            override fun defaultValue(type: String): String {
                return "null"
            }
        },
        UNMODIFIABLE_COLLECTION {
            override val nullable = ""
            override fun variability(): String {
                return "val"
            }

            override fun defaultValue(type: String): String {
                return "mutable$type" + "Of()"
            }

            override fun type(type: String, fullRef: KSTypeReference): String {
                return "Mutable$type"
            }

            override fun reset(type: String): String {
                return ".clear()"
            }
        },
        MODIFIABLE_COLLECTION {
            override val nullable = ""
            override fun variability(): String {
                return "val"
            }

            override fun defaultValue(type: String): String {
                return type[0].lowercase() + type.substring(1) + "Of()"
            }

            override fun reset(type: String): String {
                return ".clear()"
            }
        },
        COLLECTION_WITH_DEFAULT {
            override fun variability(): String {
                return "var"
            }

            override fun defaultValue(type: String): String {
                return "null"
            }
        }
        ;

        companion object {
            fun parse(type: String, hasDefault: Boolean, genericLabels: List<String> = emptyList()): State {
                if (hasDefault && type in listOf(
                        "List",
                        "Map",
                        "Set",
                        "MutableList",
                        "MutableMap",
                        "MutableSet"
                    )
                ) return COLLECTION_WITH_DEFAULT
                return when (type) {
                    "MutableList", "MutableMap", "MutableSet" -> MODIFIABLE_COLLECTION
                    "List", "Map", "Set" -> UNMODIFIABLE_COLLECTION
                    "Int", "Double", "String" -> PRIMITIVE
                    in genericLabels -> PRIMITIVE
                    else -> OBJECT
                }
            }
        }

        open val nullable = "?"


        abstract fun variability(): String
        abstract fun defaultValue(type: String): String

        open fun type(type: String, fullRef: KSTypeReference): String {
            return type
        }

        open fun reset(type: String): String {
            return " = null"
        }
    }

    private val typeString = type.toString()
    val state = State.parse(typeString, hasDefault, genericLabels)

    fun isNullable() = state.nullable == "?"
    private val generics = type.resolve().let {
        if (it.arguments.isNotEmpty()) {
            it.arguments.joinToString(
                separator = ", ",
                prefix = "<",
                postfix = ">"
            ) { inner -> inner.type.toString() }
        } else {
            ""
        }
    }

    fun initialize(): String {
        return "${state.variability()} $name : ${
            state.type(
                typeString,
                type
            )
        }$generics${state.nullable} = ${state.defaultValue(typeString)}"
    }

    fun reset(): String {
        return "$name${state.reset(typeString)}"
    }
}

fun properType(property: KSPropertyDeclaration): String {
    val p = Props(property)
    return p.initialize()
}

fun resetType(property: KSPropertyDeclaration): String {
    val p = Props(property)
    return p.reset()
}

fun properType(property: KSValueParameter, templates: List<String> = emptyList()): String {
    val p = Props(property, genericLabels = templates)
    return p.initialize()
}

fun resetType(property: KSValueParameter): String {
    val p = Props(property)
    return p.reset()
}

fun stringify(text: KSTypeReference): String {
    val t = text.toString()
    return when (t) {
        "List" -> ".toList()"
        "Set" -> ".toSet()"
        "Map" -> ".toMap()"
        "MutableMap" -> ".toMutableMap()"
        "MutableSet" -> ".toMutableSet()"
        "MutableList" -> ".toMutableList()"
        else -> "!!"
    }
}

fun stringify(props: Props): String {
    if (props.state == Props.State.COLLECTION_WITH_DEFAULT) return "!!${stringify(props.type)}"
    return stringify(props.type)
}

enum class ClassType {
    INTERFACE,
    ABSTRACT,
    CLASS;

    companion object {
        fun fromDeclaration(decl: KSClassDeclaration): ClassType {
            return when (decl.classKind to decl.isAbstract()) {
                ClassKind.INTERFACE to true -> INTERFACE
                ClassKind.INTERFACE to false -> INTERFACE
                ClassKind.CLASS to true -> ABSTRACT
                else -> CLASS
            }
        }
    }
}

fun typeInterpret(type: KSTypeReference): String {
    val res = type.resolve()
    val generics = type.resolve().let {
        if (it.arguments.isNotEmpty()) {
            it.arguments.joinToString(
                separator = ", ",
                prefix = "<",
                postfix = ">"
            ) { inner -> inner.type.toString() }
        } else {
            ""
        }
    }
    return res.declaration.qualifiedName?.asString() + generics
}

fun typeInterpret(type: KSTypeParameter): String {
    return type.bounds.joinToString(separator = ", ") { it.toString() }
}

/**
 * Get the keyword translation for default values from the passed argument by splitting along "," and then along "="
 */
fun splitDefaults(default: String): Map<String, String> {
    val targets = default.split(",").map { it.trim() }
    return targets.associate { it.splitOnce("=") }
}

fun String.splitOnce(delimiter: String): Pair<String, String> {
    val index = this.indexOf(delimiter)
    return if (index == -1) {
        Pair(this, "")
    } else {
        Pair(this.substring(0, index), this.substring(index + delimiter.length))
    }
}
fun typeInterpret(classDeclaration: KSClassDeclaration): String {
    if (classDeclaration.typeParameters.isEmpty()) {

        return ""
    }

    return classDeclaration.typeParameters.joinToString(
        separator = ", ",
        prefix = "<",
        postfix = ">"
    ) {
        val type = typeInterpret(it)
        it.name.asString() + if (type != "") ": $type" else ""
    }

}

fun typeNames(classDeclaration: KSClassDeclaration): String {
    if (classDeclaration.typeParameters.isEmpty()) return ""
    return classDeclaration.typeParameters.joinToString(
        separator = ", ",
        prefix = "<",
        postfix = ">"
    ) {
        it.name.asString()
    }
}

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

    inner class Visitor(private val file: OutputStream) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val className = classDeclaration.simpleName.asString()
            val typeInterpret = typeInterpret(classDeclaration)
            val typeList = typeNames(classDeclaration)
            val classNameTyped = className + typeList
            val newClassName = "${className}Builder$typeList"
            val newClassNameTyped = "${className}Builder$typeInterpret"
            val labls = classDeclaration.typeParameters.map { it.name.asString() }
            val cType = ClassType.fromDeclaration(classDeclaration)
            val buildableAnnotation = classDeclaration.annotations
                .firstOrNull { it.shortName.asString() == "Buildable" }
            val tempeee = mutableListOf(1)
            val defaults = buildableAnnotation?.arguments
                ?.firstOrNull { it.name?.asString() == "defaults" }
                ?.value as? String ?: ""
            val defaultMap = splitDefaults(defaults)
            val potentialParameters = classDeclaration.primaryConstructor?.parameters ?: emptyList()
            val h = Holder(classDeclaration)
            val createBuildFunction = createBuildFunction(classDeclaration)
            val resetFunction = when (classDeclaration.classKind) {
                ClassKind.INTERFACE -> classDeclaration.getAllProperties()
                    .filter { it.getter?.modifiers?.contains(Modifier.ABSTRACT) ?: false }.map {
                        resetType(it)
                    }.joinToString(separator = "\n")

                else -> (
                        h.allParameters.joinToString(separator = "\n") {it.reset()}
                        )
            }

            val parameterList = when (classDeclaration.classKind) {
                ClassKind.INTERFACE -> classDeclaration.getAllProperties()
                    .filter { !it.hasBackingField || it.findOverridee() == null }.map { properType(it) }
                    .joinToString(separator = "\n")

                else ->
                {
                    h.allParameters.joinToString("\n") { it.toAttribute() }
                }

//                        classDeclaration.primaryConstructor?.parameters?.joinToString(separator = "\n") {
//                            properType(
//                                it,
//                                labls
//                            )
//                        }
//                            ?: ""

            }
            file += "class ${classDeclaration.builderNameWithResolvedGenerics}() : Builder<${classDeclaration.nameWithGenerics}>" {
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
            } + "\n"
        }
    }
}

private fun Int.toBinaryRepresentation(size: Int): List<Boolean> {
    return Integer.toBinaryString(this).padStart(size, '0').map { it == '1' }
}

/** Slow but no conversion errors. In the future someone may want to turn this into a reasonably useful function
 *
 */
private fun Int.pow(exponent: Int): Int {
    var result = 1
    repeat(exponent) {
        result *= this
    }
    return result
}