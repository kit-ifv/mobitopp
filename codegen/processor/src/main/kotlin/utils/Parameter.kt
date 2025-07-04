package utils
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import processor.builder.splitDefaults
import util.toTypeName

/**
 * An adapter class, altering a [KSValueParameter] or [KSPropertyDeclaration] to a unified type.
 * @param name the name of the original parameter
 * @param type the type of the original parameter
 * @param hasDefault whether the original parameter has a default value
 * @param originalIsVar whether the original is mutable
 * @param isFreeFloating if the variable in the constructor is just a reference that is not held later on.
 */
class Parameter(
    val name: String,
    val type: KSTypeReference,
    val hasDefault: Boolean,
    val originalIsVar: Boolean,
    val isFreeFloating: Boolean,
    externalDefaultValues: Map<String, String>,
    templateParameters: List<String>
) {
    constructor(ksValueParameter: KSValueParameter, externalDefaultValues: Map<String, String>, templateParameters: List<String>) :
            this(
                ksValueParameter.name?.asString() ?: "",
                ksValueParameter.type,
                ksValueParameter.hasDefault,
                ksValueParameter.isVar,
                !ksValueParameter.isVar && !ksValueParameter.isVal,
                externalDefaultValues,
                templateParameters
            )

    constructor(
        prop: KSPropertyDeclaration,
        externalDefaultValues: Map<String, String>,
        templateParameters: List<String>
    ) : this(
        prop.simpleName.asString(), prop.type, false, prop.isMutable, false, externalDefaultValues, templateParameters
    )

    /**
     * If a default value is externally specified in the [Buildable] annotation. via @Buildable(default="...")
     * it is set here.
     */
    private val externalDefaultValue = externalDefaultValues[name]

    /**
     * Whether an externally specified default value is present
     */
    val hasExternalDefault = externalDefaultValue != null

    /**
     * Whether the original value is nullable
     */
    val originalIsNullable = type.resolve().isMarkedNullable

    /**
     * The internal State of the parameter.
     */
    val state = StateWithQualifiedName.parse(type, templateParameters)

    /**
     * If the Parameter has generics, this attribute will return a string represenation similar to the instantiation of
     * the bracket part <X>. As in Set<Person> -> <Person>
     */
    val generics: String = type.resolve().let {
        if (it.arguments.isNotEmpty()) {
            it.arguments.joinToString(
                separator = ", ",
                prefix = "<",
                postfix = ">"
            ) {
                inner -> val asString = inner.type?.resolve()?.declaration?.qualifiedName?.asString()
                if(inner.type.toString() in templateParameters) {
                    inner.type.toString()
                } else {
                    // Das hier ist der generator mit dem Kommentar /* Klassenname */ d.h. die brackets davor werden schon an einer anderen Stelle generiert
                    (asString?.substring(0, (asString.lastIndexOf(".") + 1)) ?: "") +inner.type.toString() + "/* ${inner.type.toString() } $templateParameters */"
                }
                }

        } else {
            ""
        }
    }


    private val externalInstantiation = externalDefaultValue?.let { it + state.reverseInstantiate() }
    /**
     * If an external default value is defined the variable is never nullable otherwise it is nullable if either the
     * state or the internal default make it nullable to represent absence of a value
     */
    fun isNullable() = !hasExternalDefault && (state.nullable || hasDefault)

    /**
     * Creates a string represenation of the attribute for the builder. Is used in the instantiation
     * """Builder.... {
     *    var param: Type = *
     * }
     */
    fun toAttribute(): String {
        val keyword = "var"
        val nullableString = if (isNullable()) "?" else ""
        // The state can override the type. This is mostly necessary for collections.
        //val typeString = overrideableType() //TODO type string is never used?
        // If the parameter is nullable because a default value exists, the start value should be null regardless of
        // what the type specifies as start value
        val startValue = if (isNullable()) "null" else state.initialValue
        // The external type may not match the internal type if collections are used and must be transformed

        return "$keyword ${nameWithOverrideableType()}$nullableString = ${externalInstantiation ?: startValue}"
    }

    /**
     * Returns the parameter name with the generics, separated by ":"
     * example myMap : Map<Int, String>
     */
    fun nameWithOverrideableType(override: Boolean = true) = "$name : ${overrideableTypeWithGenerics(override)}"

    /**
     * Returns the type with generics
     * Map<Int, String>
     */
    fun overrideableTypeWithGenerics(override: Boolean = true) = "${overrideableType(override)}$generics"

    /**
     *
     * Returns the type string. Override enables the specification of a different type if required. This is used for
     * Map -> Mutable Map primarily
     */
    fun overrideableType(override: Boolean = true): String {
        if(!override && state.name != "OBJECT") return type.simpleTypeName
        return state.typeOverride ?: type.simpleTypeName
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
     * Does not specify name = ... rather just ... Is used in interface instantiation.
     *
     * builder(a!!, b.toList(), c)
     */
    fun evaluateSimple(): String {
        val enforceNoNull = if (isNullable() && !originalIsNullable) "!!" else ""
        return "$name$enforceNoNull${state.instantiate()}"
    }

    /**
     * Generates a "param: type" String. Used in abstract class instantiation where only a reference call is needed in
     * the dummy class. The parameters should be the values of the primary constructor here.
     *          ↓          ↓ - this is generated.
     * DefaultX(param: Type) : AbstractClass(param)
     */
    fun toReferenceInstantiation(): String {
        val setNullable = if (originalIsNullable) "?" else ""
        return "$name: $type$setNullable$generics"
    }

    /**
     * Generates an instantiation "override var param: X" needed for the dummy class for an interface.
     */
    fun toOverrideInstantiation(override: Boolean): String {
        val temp = if (originalIsVar) "var" else "val"
        return "override $temp ${nameWithOverrideableType(override)}"
    }

    /**
     * Creates the call function to reset the builder attribute to the start value. i.e. "i = null" if i would be
     * nullable or "list = emptyList()" if the value were a list.
     */
    fun reset(): String {
        val resetTarget = if (isNullable()) " = null" else state.reset(externalDefaultValue = externalInstantiation)
        return "$name$resetTarget"
    }

}

/**
 * A list of all primitive types in Kotlin
 */
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

/**
 * A collection of potential Classes that may be parsed.
 */
enum class PotentialStates : ParameterInfos {
    /**
     * If the type is in the list of primitives it should be treated as a primitive.
     */
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
        override fun reverseInstantiate(): String {
            return ".toMutableSet()"
        }

        override val initialValue = "mutableSetOf()"
        override fun instantiate(): String = ".toMutableSet()"
    },
    MUTABLE_LIST {
        override fun instantiate(): String {
            return ".toMutableList()"
        }

        override fun reverseInstantiate(): String {
            return ".toMutableList()"
        }

        override val initialValue = "mutableListOf()"
    },
    UNMODIFIABLE_LIST {
        override fun instantiate(): String {
            return ".toList()"
        }
        override fun reverseInstantiate(): String {
            return ".toMutableList()"
        }
        override val initialValue = "mutableListOf()"
        override val typeOverride: String = "MutableList"
    },
    MUTABLE_MAP {
        override fun instantiate(): String {
            return ".toMutableMap()"
        }
        override fun reverseInstantiate(): String {
            return ".toMutableMap()"
        }
        override val initialValue = "mutableMapOf()"
    },
    UNMODIFIABLE_MAP {
        override fun instantiate(): String {
            return ".toMap()"
        }

        override fun reverseInstantiate(): String {
            return ".toMutableMap()"
        }
        override val initialValue = "mutableMapOf()"
        override val typeOverride: String = "MutableMap"
    },
    UNMODIFIABLE_SET {
        override fun instantiate(): String {
            return ".toSet()"
        }
        override fun reverseInstantiate(): String {
            return ".toMutableSet()"
        }
        override fun reset(externalDefaultValue: String?): String = ".clear()"

        override val initialValue = "mutableSetOf()"
        override val typeOverride: String = "MutableSet"
    }
    ;

    /**
     * The reset function determines how a certain object is reset to the standard. For Example, collections can be
     * cleared. But an object may be set to o = null
     */
    override fun reset(externalDefaultValue: String?): String = ".clear()"

    /**
     * Determines how an object is instantiated. Relevant for lists where a copy should be used rather than a reference.
     */
    override fun instantiate(): String = ""

    /**
     * Inverse the instantiation in a collection to be the type actually used in the builder
     */


    override val nullable = false
    override val initialValue = "null"
    override val variable = true
    override val typeOverride: String? = null

    override fun KSTypeReference.name(): String {
        return toString()
    }
    companion object {
        fun parse(type: KSTypeReference, templateParameters: List<String> = emptyList()): PotentialStates {
            val s = type.simpleTypeName
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

private val KSTypeReference.simpleTypeName: String get() = this.resolve().declaration.simpleName.asString()

/**
 * Wraps around the [PotentialStates] class and overwrites the typeOverride to be the qualified name, if one is set
 */
class StateWithQualifiedName(private val enum: PotentialStates, qualifiedName: String?) :
    ParameterInfos by enum {

    val name get() = enum.name

    /**
     * Since the type of a complex object may reside anywhere, and not necessarily the same package we need the
     * fully qualified name.
     */
    override val typeOverride: String? = qualifiedName ?: enum.typeOverride
    override fun reset(externalDefaultValue: String?): String {
        return if (externalDefaultValue != null) {
            " = $externalDefaultValue"
        } else {
            enum.reset(null)
        }
    }

    companion object {
        /**
         * Determines the object state via parsing and checking against the list of primitives.
         */
        fun parse(type: KSTypeReference, templateParameters: List<String>): StateWithQualifiedName {
            val full = type.resolve().declaration.qualifiedName?.asString() ?: ""
            val enum = PotentialStates.parse(type, templateParameters)
            // Sets the qualified name if the target state is an object
            return StateWithQualifiedName(enum, if (enum == PotentialStates.OBJECT) full else null)
        }
    }
}

/**
 * A wrapper interface so that [StateWithQualifiedName] can delegate to the enum. Does not work with the enum directly
 */
interface ParameterInfos {
    fun reset(externalDefaultValue: String? = null): String {
        return " = ${externalDefaultValue ?: "null"}"
    }

    fun instantiate(): String = ""
    fun reverseInstantiate(): String = ""

    val nullable: Boolean
    val initialValue: String
    val variable: Boolean
    val typeOverride: String?

    fun KSTypeReference.name(): String {
        return toString()
    }
}
// KSP Extension functions below.
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

val KSClassDeclaration.genericNames: List<String> get() = typeParameters.map { it.name.asString() }
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
    primaryConstructor?.parameters?.filter { it.hasDefault }?.map { Parameter(it, externalDefaultValues, genericNames) }
        ?: emptyList()

fun KSClassDeclaration.parameters(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    primaryConstructor?.parameters?.map { Parameter(it, externalDefaultValues, genericNames) } ?: emptyList()

fun KSClassDeclaration.nonDefaultableParameters(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    primaryConstructor?.parameters?.filter { !it.hasDefault }
        ?.map { Parameter(it, externalDefaultValues, genericNames) }
        ?: emptyList()

fun KSClassDeclaration.allProperties(externalDefaultValues: Map<String, String> = this.externalDefaultValues) =
    getAllProperties().map { Parameter(it, externalDefaultValues, genericNames) }.toList()

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

val KSFunctionDeclaration.mimic: String
    get() {
        return "fun ${toString()}(" +
            parameters.joinToString {
                val p = Parameter(it, emptyMap(), emptyList())
                p.nameWithOverrideableType()
            } +
        ")" + ": ${returnType.toString()}"
    }