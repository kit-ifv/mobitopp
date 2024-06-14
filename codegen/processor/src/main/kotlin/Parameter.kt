import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter

class Parameter(
    val original: KSValueParameter,
    externalDefaultValues: Map<String, String>,
    templateParameters: List<String>
) {
    val name = original.name?.asString() ?: ""
    val externalDefaultValue = externalDefaultValues[name]
    val hasExternalDefault = externalDefaultValue != null
    val hasInternalDefault = original.hasDefault
    val originalIsNullable = original.type.resolve().isMarkedNullable
    val state = Wrapper.parse(original.type, templateParameters)
    private val generics = original.type.resolve().let {
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

    /**
     * If an external default value is defined the variable is never nullable otherwise it is nullable if either the
     * state or the internal default make it nullable to represent absence of a value
     */
    fun isNullable() = !hasExternalDefault && (state.nullable || hasInternalDefault)

    fun isVariable() = state.variable || hasExternalDefault

    /**
     * Creates a string represenation of the attribute for the builder.
     */
    fun toAttribute(mutable: Boolean = true): String {
        val keyword = if (mutable) "var" else "val"
        val nullableString = if (isNullable()) "?" else ""
        val typeString = state.typeOverride ?: original.type
        // If the parameter is nullable because a default value exists, the start value should be null regardless of
        // what the type specifies as start value
        val startValue = if (isNullable()) "null" else state.startValue
        return "$keyword $name : ${typeString}$generics$nullableString = ${externalDefaultValue ?: startValue}"
    }

    /**
     * Creates a string representation on how the attribute should be invoked so that the constructor of the underlying
     * class references the correct value to the correct attribute of the target class.
     */
    fun evaluate(): String {
        val enforceNoNull = if (isNullable() && !originalIsNullable) "!!" else ""
        return "$name = $name$enforceNoNull${state.instantiate()}"
    }

    /**
     * Creates the call function to reset the builder attribute to the start value. i.e. "i = null" if i would be
     * nullable or "list = emptyList()" if the value were a list.
     */
    fun reset(): String {
        val resetTarget = if (isNullable()) " = null" else state.reset(externalDefaultValue = externalDefaultValue)
        return "$name$resetTarget"
    }

    fun debug(): String {
        return "name=$name ext.Def=$externalDefaultValue int.Def=$hasInternalDefault state=${state.name}"
    }
}

val PRIMITIVETYPES = listOf(
    "Byte",
    "Short",
    "Int",
    "Long",
    "Float",
    "Double",
    "UByte",
    "UShort",
    "UInt",
    "ULong",
    "Boolean",
    "Char",
    "String",
    "Array<SomeComplexObject>",
    "IntArray",
    "ByteArray",
    "BooleanArray",
    "CharArray",
    "DoubleArray",
    "FloatArray",
    "LongArray",
    "ShortArray"
)

enum class PotentialStates : Temp {

    PRIMITIVE {
        override val nullable: Boolean = true

        override fun reset(externalDefaultValue: String?): String {
            return " = ${externalDefaultValue ?: "null"}"
        }
    },
    OBJECT {
        override val nullable: Boolean = true
        override fun KSTypeReference.name(): String {
            return resolve().declaration.qualifiedName?.asString() ?: ""
        }

        override fun reset(externalDefaultValue: String?): String {
            return " = ${externalDefaultValue ?: "null"}"
        }
    },
    MUTABLE_SET {

        override val startValue = "mutableSetOf()"
        override fun instantiate(): String = ".toMutableSet()"
    },
    MUTABLE_LIST {
        override fun instantiate(): String {
            return ".toMutableList()"
        }

        override val startValue = "mutableListOf()"
    },
    UNMODIFIABLE_LIST {
        override fun instantiate(): String {
            return ".toList()"
        }

        override val startValue = "mutableListOf()"
        override val typeOverride: String = "MutableList"
    },
    MUTABLE_MAP {
        override fun instantiate(): String {
            return ".toMutableMap()"
        }

        override val startValue = "mutableMapOf()"
    },
    UNMODIFIABLE_MAP {
        override fun instantiate(): String {
            return ".toMap()"
        }

        override val startValue = "mutableMapOf()"
        override val typeOverride: String = "MutableMap"
    },
    UNMODIFIABLE_SET {
        override fun instantiate(): String {
            return ".toSet()"
        }

        override fun reset(externalDefaultValue: String?): String = ".clear()"

        override val startValue = "mutableSetOf()"
        override val typeOverride: String = "MutableSet"
    }
    ;

    override fun reset(externalDefaultValue: String?): String = ".clear()"

    override fun instantiate(): String = ""
    override fun power(): String = ""

    override val nullable = false
    override val startValue = "null"
    override val variable = true
    override val typeOverride: String? = null

    override fun KSTypeReference.name(): String {
        return toString()
    }

    companion object {
        fun parse(type: KSTypeReference, templateParameters: List<String> = emptyList()): PotentialStates {
            val s = type.toString()

            return when (s) {
                "List" -> UNMODIFIABLE_LIST
                "Map" -> UNMODIFIABLE_MAP
                "MutableSet" -> MUTABLE_SET
                "Set" -> UNMODIFIABLE_SET
                "MutableList" -> MUTABLE_LIST
                "MutableMap" -> MUTABLE_MAP
                in PRIMITIVETYPES -> PRIMITIVE
                in templateParameters -> PRIMITIVE // Template behaves like a primitive
                else -> OBJECT
            }
        }
    }
}

class Wrapper(private val enum: PotentialStates, val startString: String, private val stopString: String?) :
    Temp by enum {

    val name get() = enum.name
    override val typeOverride: String? = stopString ?: enum.typeOverride
    override fun reset(externalDefaultValue: String?): String {
        return if (externalDefaultValue != null) {
            " = $externalDefaultValue"
        } else {
            enum.reset(null)
        }
    }

    companion object {
        fun parse(type: KSTypeReference, templateParameters: List<String>): Wrapper {
            val s = type.toString()
            val full = type.resolve().declaration.qualifiedName?.asString() ?: ""
            val enum = PotentialStates.parse(type, templateParameters)
            return Wrapper(enum, s, if (enum == PotentialStates.OBJECT) full else null)
        }
    }
}

interface Temp {
    fun reset(externalDefaultValue: String? = null): String {
        return " = ${externalDefaultValue ?: "null"}"
    }

    fun instantiate(): String = ""
    fun power(): String = ""

    val nullable: Boolean
    val startValue: String
    val variable: Boolean
    val typeOverride: String?

    fun KSTypeReference.name(): String {
        return toString()
    }
}

val KSClassDeclaration.name: String
    get() = this.simpleName.asString()

fun KSClassDeclaration.generics(resolve: KSTypeParameter.() -> String = { nameWithUpperBounds }): String {
    if (typeParameters.isEmpty()) {
        return ""
    }
    return typeParameters.joinToString(
        separator = ", ",
        prefix = "<",
        postfix = ">"
    ) {
        it.resolve()
    }
}

val KSClassDeclaration.genericNames: List<String> get() = typeParameters.map{it.name.asString()}
val KSClassDeclaration.builderNameWithResolvedGenerics: String get() = "${name}Builder$resolvedGenerics"
val KSClassDeclaration.nameWithGenerics: String get() = "${name}$simpleGenerics"
val KSClassDeclaration.builderWithGenerics: String get() = "${name}Builder$simpleGenerics"

/**
 * Resolves the potential generics of the class with upper bounds. So if the class has a generic parameter, the bounds
 * are added to the string, if they exist. Examples:
 *  Class<T: Numeric> -> "<T: Numeric>"
 *  Class<T, S: Random, Other> -> "<T, S: Random, Other>"
 *  Class -> ""
 */
val KSClassDeclaration.resolvedGenerics: String get() = generics { nameWithUpperBounds }

/**
 * Returns a string representation of the class without upper bounds.
 *  Class<T: Numeric> -> "<T>"
 *  Class<T, S: Random, Other> -> "<T, S>"
 *  Class -> ""
 */
val KSClassDeclaration.simpleGenerics: String get() = generics { name.asString() }

fun KSClassDeclaration.defaultableParameters(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    primaryConstructor?.parameters?.filter { it.hasDefault }?.map { Parameter(it, externalDefaultValues,genericNames ) }
        ?: emptyList()

fun KSClassDeclaration.parameters(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    primaryConstructor?.parameters?.map { Parameter(it, externalDefaultValues, genericNames) } ?: emptyList()

fun KSClassDeclaration.nonDefaultableParameters(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    primaryConstructor?.parameters?.filter { !it.hasDefault }?.map { Parameter(it, externalDefaultValues, genericNames) }
        ?: emptyList()

/**
 * Parses the default values transmitted in the [Buildable] annotation via the [Buildable.defaults] parameter.
 * Returns a map of "Attribute Name" -> "Default value"
 */
val KSClassDeclaration.externalDefaultValues: Map<String, String>
    get() {
        // The Buildable annotation should be present when this code is called. Thus we use first instead of firstOrNull
        val annotation = annotations.first { it.shortName.asString() == "Buildable" }
        // The default value in the annotation may not be set, therefore it can be null
        val defaults = annotation.arguments.firstOrNull { it.name?.asString() == "defaults" }?.value as String
        return splitDefaults(defaults)
    }

/**
 * Returns a string representation of a KSTypeparameter. Additionally, if there are upper bounds these are returned
 * as they would occur in normal written text, for example  T : Numeric, Comparable<T>
 */
val KSTypeParameter.nameWithUpperBounds: String
    get() {
        if (bounds.none()) {
            return simpleName.asString()
        }
        return simpleName.asString() + ": " + bounds.joinToString(separator = ", ", prefix = "") { it.toString() }
    }
