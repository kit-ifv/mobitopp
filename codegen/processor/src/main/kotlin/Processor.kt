import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.OutputStream






class Processor(private val codeGenerator: CodeGenerator, private val logger: KSPLogger, private val options: Map<String, String>) : SymbolProcessor {

    operator fun OutputStream.plusAssign(str: String) {
        this.write(str.toByteArray())
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {

        val symbols = resolver.getSymbolsWithAnnotation("Mutable").filterIsInstance<KSClassDeclaration>()
        if (!symbols.iterator().hasNext()) return emptyList()

        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
            packageName = "changeLater",
            fileName = "TestFile"
        )
        file += "package data\n"

        symbols.forEach { it.accept(Visitor(file), Unit) }

        file.close()
        return symbols.filterNot { it.validate() }.toList()
    }
    inner class Visitor(private val file: OutputStream): KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val className = classDeclaration.simpleName.asString()
            file += "class Mutable$className (\n"
            file += classDeclaration.getAllProperties().map {"var " +
                    it.simpleName.asString() + ": " + it.type.toString()}.joinToString(prefix = "    ", postfix = "\n", separator = ",\n    ")

            file += ") {\n"

            file += "    fun build(): $className {\n"

            file += "         return $className(${classDeclaration.getAllProperties().map { it.simpleName.asString() }.joinToString()})\n"

            file +=  "    }\n"
            file +=  "}\n"


        }

        override fun visitPropertyDeclaration(property: KSPropertyDeclaration, data: Unit) {

        }

        override fun visitTypeArgument(typeArgument: KSTypeArgument, data: Unit) {
        }

    }
}