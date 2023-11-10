
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import java.io.OutputStream

class Props(property: KSPropertyDeclaration) {
    enum class State {
        PRIMITIVE {
            override fun variability(): String {
                return "var"
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

            override fun type(type: String): String {
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
        };

        companion object {
            fun parse(type: String): State {
                return when (type) {
                    "MutableList", "MutableMap", "MutableSet" -> MODIFIABLE_COLLECTION
                    "List", "Map", "Set" -> UNMODIFIABLE_COLLECTION
                    else -> PRIMITIVE
                }
            }
        }
        open val nullable = "?"
        abstract fun variability(): String
        abstract fun defaultValue(type: String): String

        open fun type(type: String): String {
            return type
        }
        open fun reset(type: String): String {
            return " = null"
        }
    }

    private val name = property.simpleName.asString()
    private val type = property.type.toString()
    private val state = State.parse(type)
    private val generics = property.type.resolve().let {
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
        return "${state.variability()} $name : ${state.type(type)}$generics${state.nullable} = ${state.defaultValue(type)}"
    }

    fun reset(): String {
        return "$name${state.reset(type)}"
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
            file += "import Builder\n"

            it.value.forEach { x -> x.accept(Visitor(file), Unit) }
            file.close()
        }

        return symbols.filterNot { it.validate() }.toList()
    }

    inner class Visitor(private val file: OutputStream) : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            // Currently this disables mutable annotation for abstract classes and interfaces
            if (classDeclaration.isAbstract()) {
                return
            }
            val className = classDeclaration.simpleName.asString()
            val newClassName = "Mutable$className"
            file += "class $newClassName() : Builder<$className>" {
                +classDeclaration.getAllProperties().map {
                    properType(it)
                }.joinToString(separator = "\n")
                +"fun buildPreserving(lambda : $newClassName.() -> Unit) : $className" {
                    +"this.apply(lambda)"
                    +"return build()"
                }
                + "fun build(lambda: $newClassName.() -> Unit) : $className" {
                    + "val result = buildPreserving(lambda)"
                    + "reset()"
                    + "return result"
                }
                +"fun reset()" {
                    +classDeclaration.getAllProperties().map {
                        resetType(it)
                    }.joinToString(separator = "\n")

                }
                +"override fun build(): $className" {
                    +"return $className(${
                        classDeclaration.getAllProperties().map { it.simpleName.asString() + stringify(it.type) }
                            .joinToString()
                    })"
                }

            } + "\n"


        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
        }

    }
}