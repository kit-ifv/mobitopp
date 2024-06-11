import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter

class Parameter(
    val original: KSValueParameter,
    externalDefaultValues: Map<String, String>
) {
    val name = original.name?.asString() ?: ""
    val externalDefaultValue = externalDefaultValues[name]
    val hasExternalDefault = externalDefaultValue != null
    val hasInternalDefault = original.hasDefault

    val state = PotentialStates.parse(original.type.toString())

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
        return "$keyword $name : ${original.type} = ${externalDefaultValue ?: "null"}"
    }

    /**
     * Creates a string representation on how the attribute should be invoked so that the constructor of the underlying
     * class references the correct value to the correct attribute of the target class.
     */
    fun evaluate(): String {
        val enforceNoNull = if (isNullable()) "!!" else ""
        return "$name = $name$enforceNoNull${state.instantiate()}"
    }

    fun reset(): String {
        return "$name${state.reset(externalDefaultValue = externalDefaultValue)}"
    }

    fun debug(): String {
        return "name=$name ext.Def=$externalDefaultValue int.Def=$hasInternalDefault state=${state.name}"
    }
}

enum class PotentialStates {

    PRIMITIVE {
        override val nullable: Boolean
            get() = true
    },
    MUTABLE_SET {
        override fun reset(mutable: Boolean, externalDefaultValue: String?): String {
            return super.reset(mutable, externalDefaultValue)
        }

        override fun instantiate(): String = ".toMutableSet()"
    },
    MUTABLE_LIST {
        override fun instantiate(): String {
            return ".toMutableList()"
        }
    },
    UNMODIFIABLE_LIST {
        override fun instantiate(): String {
            return ".toList()"
        }
    },
    MUTABLE_MAP {
        override fun instantiate(): String {
            return ".toMutableMap()"
        }
    },
    UNMODIFIABLE_MAP {
        override fun instantiate(): String {
            return ".toMap()"
        }
    },
    UNMODIFIABLE_SET {
        override fun instantiate(): String {
            return ".toSet()"
        }
    }
    ;

    open fun reset(mutable: Boolean = true, externalDefaultValue: String? = null): String {
        require(mutable) {
            "Cannot reset this state $this, as it is not mutable"
        }
        return " = ${externalDefaultValue ?: "null"}"
    }

    open fun instantiate(): String = ""
    open fun power(): String = ""

    open val nullable = false
    open val variable = true

    companion object {
        fun parse(s: String): PotentialStates {
            return when (s) {
                "List" -> UNMODIFIABLE_LIST
                "Map" -> UNMODIFIABLE_MAP
                "MutableSet" -> MUTABLE_SET
                "Set" -> UNMODIFIABLE_SET
                "MutableList" -> MUTABLE_LIST
                "MutableMap" -> MUTABLE_MAP
                else -> PRIMITIVE
            }
        }
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
    primaryConstructor?.parameters?.filter { it.hasDefault }?.map { Parameter(it, externalDefaultValues) }
        ?: emptyList()

fun KSClassDeclaration.parameters(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    primaryConstructor?.parameters?.map { Parameter(it, externalDefaultValues) } ?: emptyList()

fun KSClassDeclaration.nonDefaultableParameters(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    primaryConstructor?.parameters?.filter { !it.hasDefault }?.map { Parameter(it, externalDefaultValues) }
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
        return simpleName.asString() + bounds.joinToString(separator = ", ", prefix = " ") { it.toString() }
    }