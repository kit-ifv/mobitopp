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
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import java.io.OutputStream

class Props(private val name: String, private val type: KSTypeReference, private val hasDefault: Boolean = false) {

    constructor(property: KSPropertyDeclaration) : this(property.simpleName.asString(), property.type)
    constructor(property: KSValueParameter) : this(property.name?.asString()!!, property.type, property.hasDefault)

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
            fun parse(type: String, hasDefault: Boolean): State {
                if(hasDefault && type in listOf("List", "Map", "Set", "MutableList", "MutableMap", "MutableSet")) return COLLECTION_WITH_DEFAULT
                return when (type) {
                    "MutableList", "MutableMap", "MutableSet" -> MODIFIABLE_COLLECTION
                    "List", "Map", "Set" -> UNMODIFIABLE_COLLECTION
                    "Int", "Double", "String" -> PRIMITIVE
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
    private val state = State.parse(typeString, hasDefault)
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

fun properType(property: KSValueParameter): String {
    val p = Props(property)
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

fun typify(text: KSValueParameter): String {
    val t = text.type.toString()
    return when (t) {
        "List" -> ".toList()"
        "Set" -> ".toSet()"
        "Map" -> ".toMap()"
        "MutableMap" -> ".toMutableMap()"
        "MutableSet" -> ".toMutableSet()"
        "MutableList" -> ".toMutableList()"
        else -> ""
    }
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
            file += "import kotlin.reflect.full.primaryConstructor\n"

            it.value.forEach { x -> x.accept(Visitor(file), Unit) }
            file.close()
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    inner class Visitor(private val file: OutputStream) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val className = classDeclaration.simpleName.asString()
            val newClassName = "${className}Builder"

            val cType = ClassType.fromDeclaration(classDeclaration)

            val createBuildFunction = when (cType) {
                ClassType.INTERFACE -> "override fun build(): $className" {
                    +"class Default$className(${
                        // !it.hasBackingField || it.findOverridee() == null
                        //
                        classDeclaration.getAllProperties()
                            .filter { it.getter?.modifiers?.contains(Modifier.ABSTRACT) ?: false }
                            .map {
                                "override val " + it.simpleName.asString() + ": " + typeInterpret(it.type) + " = this.${
                                    it.simpleName.asString() + "?: throw IllegalArgumentException(\"Required parameter [${it.simpleName.asString()}] is null\")"
                                }"
                            }
                            .joinToString(prefix = "\n", separator = ",\n")
                    }) :$className" {
                        +classDeclaration.getAllFunctions().filter { it.isAbstract }.map {
                            "override fun ${it.simpleName.asString()}(${
                                it.parameters.map {
                                    it.name?.asString() + ": " + typeInterpret(
                                        it.type
                                    )
                                }.joinToString(separator = ", ")
                            }): ${typeInterpret(it.returnType!!)}" {
                                +"throw NotImplementedError()"
                            }
                        }.joinToString(separator = "\n")
                    }
                    +"return Default$className()"
                }

                ClassType.ABSTRACT -> "override fun build(): $className" {
                    +"class Default$className :$className(${
                        classDeclaration.primaryConstructor?.parameters?.joinToString {
                            it.name?.asString() + stringify(
                                it.type
                            )
                        } ?: ""
                    })" {
                        +classDeclaration.getAllFunctions().filter { it.isAbstract }.map {
                            "override fun ${it.simpleName.asString()}(): ${it.returnType}" {
                                +"throw NotImplementedError()"
                            }
                        }.joinToString()
                    }
                    +"return Default$className()"
                }

                ClassType.CLASS -> "override fun build(): $className" {
                    val defaultables = classDeclaration.primaryConstructor?.parameters?.filter {
                        it.hasDefault
                    } ?: emptyList()
                    val nonDefaultables = classDeclaration.primaryConstructor?.parameters?.filter {
                        !it.hasDefault
                    } ?: emptyList()
                    if (defaultables.isNotEmpty()) {
                        +"val target = listOf(${
                            defaultables.joinToString(separator = ", ") { "${it.name?.asString()} != null" }
                        })"
                        val map = nonDefaultables.joinToString(
                            prefix = "listOf(",
                            postfix = ").toMap()"
                        ) { "${it.name?.asString()} to (${it.name?.asString()} != null)" }

                        if (nonDefaultables.isNotEmpty()) {
                            +"val map = $map"
                            +"val control = map.filter{it.value == false}.keys"
                            +"require(control.isEmpty())" {
                                +"\"The following attributes need to be set \${control}\""
                            }
                        }

                        +"return when(target)" {
                            for (i in 0..<2.pow(defaultables.size)) {
                                val zip = i.toBinaryRepresentation(defaultables.size).zip(defaultables)
                                val targets = zip.filter { it.first }.map { it.second }
                                val prin =
                                    targets.joinToString(separator = ", ") { "${it.name?.asString()} = ${it.name?.asString()}!!" }
                                val otter =
                                    nonDefaultables.joinToString(separator = ", ") { "${it.name?.asString()} = ${it.name?.asString()}!!" }
                                val tolo = mutableListOf<String>()
                                if (targets.isNotEmpty()) tolo.add(prin)
                                if (nonDefaultables.isNotEmpty()) tolo.add(otter)
                                +"listOf(${zip.map { it.first }.joinToString(", ")}) -> $className(${
                                    tolo.joinToString(
                                        separator = ", "
                                    )
                                })"

                            }
                            +"else -> throw IllegalArgumentException()"
                        }
                    } else {
                        val map = nonDefaultables.joinToString(
                            prefix = "listOf(",
                            postfix = ").toMap()"
                        ) { "${it.name?.asString()} to (${it.name?.asString()} != null)" }
                        +"val map = $map"
                        +"val control = map.filter{it.value == false}.keys"
                        +"require(control.isEmpty())" {
                            +"\"The following attributes need to be set \${control}\""
                        }
                        +"return $className(${nonDefaultables.joinToString(separator = ", ") { "${it.name?.asString()} = ${it.name?.asString()}!!" }})"
                    }
                }
            }
            val resetFunction = when (classDeclaration.classKind) {
                ClassKind.INTERFACE -> classDeclaration.getAllProperties()
                    .filter { it.getter?.modifiers?.contains(Modifier.ABSTRACT) ?: false }.map {
                        resetType(it)
                    }.joinToString(separator = "\n")

                else -> (
                        classDeclaration.primaryConstructor?.parameters?.joinToString(separator = "\n") { resetType(it) }
                            ?: ""
                        )
            }

            val parameterList = when (classDeclaration.classKind) {
                ClassKind.INTERFACE -> classDeclaration.getAllProperties()
                    .filter { !it.hasBackingField || it.findOverridee() == null }.map { properType(it) }
                    .joinToString(separator = "\n")

                else -> (
                        classDeclaration.primaryConstructor?.parameters?.joinToString(separator = "\n") { properType(it) }
                            ?: ""
                        )
            }

            val defaultableParameters = classDeclaration.primaryConstructor?.parameters ?: emptyList()

            file += "class $newClassName() : Builder<$className>" {
                +parameterList
                +"fun buildPreserving(lambda : $newClassName.() -> Unit) : $className" {
                    +"this.apply(lambda)"
                    +"return build()"
                }
                +"fun build(lambda: $newClassName.() -> Unit) : $className" {
                    +"val result = buildPreserving(lambda)"
                    +"reset()"
                    +"return result"
                }
                +"fun reset()" {
                    +resetFunction
                }
                +createBuildFunction
            } + "\n"
        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
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