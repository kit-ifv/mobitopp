package edu.kit.ifv.util

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.squareup.kotlinpoet.FileSpec
import java.io.PrintStream

@Suppress("SpreadOperator")
internal fun FileSpec.generateFile(
    codeGenerator: CodeGenerator,
    resolver: Resolver,
    packageName: String,
    fileName: String,
) {

    val targetFile = codeGenerator.createNewFile(
        dependencies = Dependencies(false, *resolver.getAllFiles().toList().toTypedArray()),
        packageName = packageName,
        fileName = fileName
    )

    this.writeTo(PrintStream(targetFile))

}