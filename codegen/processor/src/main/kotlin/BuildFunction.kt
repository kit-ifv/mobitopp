import com.google.devtools.ksp.symbol.KSClassDeclaration

class Holder(val classDeclaration: KSClassDeclaration) {
    val defaultableParameters = classDeclaration.defaultableParameters()
    val nonDefaultableParameters = classDeclaration.nonDefaultableParameters()
    val allParameters = classDeclaration.parameters()
}

fun createBuildFunction(classDeclaration: KSClassDeclaration): String {
    val holder = Holder(classDeclaration)
    return holder.createBuildFunction()
}

private fun Holder.createBuildFunction(): String {
    return "override fun build(): ${classDeclaration.name}${classDeclaration.simpleGenerics}" {
        if (defaultableParameters.isEmpty()) {
            +buildWithoutDefaults()
        } else {
            if (defaultableParameters.size > 5) +buildWithReflection() else +buildWithListConstruction()
        }
    }
}

private fun Holder.buildWithReflection(): String {
    return TextBuilder().apply {
        +"val targetConstructor = ${classDeclaration.name}::class.primaryConstructor ?: throw NoSuchElementException(\"A primary constructor is required for the Buildable to work\")"
        +"val requiredParameters = targetConstructor.parameters.filter{!it.isOptional}"
        +"val defaultables : Map<String, () -> Any?> = ${
            allParameters.joinToString(
                prefix = "mapOf(",
                postfix = ")",
                separator = ","
            ) { "\"${it.name}\" to {${it.name}}" }
        }"

        +"require(requiredParameters.all { defaultables[it.name]?.invoke() != null })" {
            +"\"The following attributes are not set and required \${requiredParameters.filter { defaultables[it.name]?.invoke() == null }.map{it.name}}\""
        }
        +"val paramMap = targetConstructor.parameters.map { it to defaultables[it.name]?.invoke() }.filter { it.second != null }.toMap()"
        +"return targetConstructor.callBy(paramMap)"
    }.makeText()
}

private fun Holder.buildWithListConstruction(): String {
    return TextBuilder().apply {
        +"val target = listOf(${
            defaultableParameters.joinToString(separator = ", ") { "${it.name} != null" }
        })"
        +"return when(target)" {
            for (i in 0..<2.pow(defaultableParameters.size)) {
                val zip = i.toBinaryRepresentation(defaultableParameters.size).zip(defaultableParameters)
                val targets = zip.filter { it.first }.map { it.second }
                val prin =
                    targets.joinToString(separator = ", ") { it.evaluate() }
                val otter =
                    nonDefaultableParameters.joinToString(separator = ", ") {
                        it.evaluate()
                    }
                val tolo = mutableListOf<String>()
                if (targets.isNotEmpty()) tolo.add(prin)
                if (nonDefaultableParameters.isNotEmpty()) tolo.add(otter)
                +"listOf(${zip.map { it.first }.joinToString(", ")}) -> ${classDeclaration.name}(${
                    tolo.joinToString(
                        separator = ", "
                    )
                })"
            }
            +"else -> throw IllegalArgumentException()"
        }
    }.makeText()
}

private fun Holder.buildWithoutDefaults(): String {
    return TextBuilder().apply {
        +"// Created by build Without Defaults"
        val nullableParameters = nonDefaultableParameters.filter { it.isNullable() }
        if (nullableParameters.isNotEmpty()) {
            val map = nullableParameters.joinToString(
                prefix = "listOf(",
                postfix = ").toMap()"
            ) { "\"${it.name}\" to (${it.name} != null)" }
            +"val map = $map"
            +"val control = map.filter{it.value == false}.keys"
            +"require(control.isEmpty())" {
                +"\"The following attributes need to be set \${control}\""
            }
        }
        + "// ${nonDefaultableParameters.joinToString { it.debug() }}"

        +"return ${classDeclaration.name}${classDeclaration.simpleGenerics}(${
            nonDefaultableParameters.joinToString(separator = ", ") {
                it.evaluate()
            }
        })"
    }.makeText()
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
