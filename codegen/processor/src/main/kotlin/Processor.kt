import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream


fun properType(property: KSPropertyDeclaration): String {
    val prefix = property.simpleName.asString()
    val text = property.type.toString()
    val collection = listOf("List", "Map", "Set").contains(text)
    val resolved = property.type.resolve()
    var generics = ""
    if (resolved.arguments.isNotEmpty()) {
        generics += resolved.arguments.joinToString(
            separator = ", ",
            prefix = "<",
            postfix = ">"
        ) { it.type.toString() }
    }
    val nullable = if (collection) "" else "?"
    val announce = if (collection) "val" else "var"
    val pronounceMutability = if (collection) "Mutable" else ""
    val default = if (collection) " = mutable${text}Of()" else ""
    return "$announce $prefix : $pronounceMutability$text$generics$nullable$default"
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

        val symbols = resolver.getSymbolsWithAnnotation("Mutable").filterIsInstance<KSClassDeclaration>()
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
            file += "class Mutable$className (\n"
            classDeclaration.getAllProperties().map { it.accept(this, Unit) }
            file += classDeclaration.getAllProperties().map {
                properType(it)
            }.joinToString(prefix = "    ", postfix = "\n", separator = ",\n    ")

            file += ") {\n"

            file += "    fun build(): $className {\n"

            file += "         return $className(${
                classDeclaration.getAllProperties().map { it.simpleName.asString() + "!!" }.joinToString()
            })\n"

            file += "    }\n"
            file += "}\n"


        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {
        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
        }

    }
}