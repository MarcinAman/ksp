import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import java.io.File
import java.io.OutputStream

fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

class BuilderProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.warn("DUPAAAAA")
        val symbols = resolver.getSymbolsWithAnnotation("com.example.annotation.QueryFragment", true)
        symbols
            .filter { it is KSClassDeclaration }
            .forEach { it.accept(BuilderVisitor(logger), Unit) }
        return emptyList()
    }

    inner class BuilderVisitor(val logger: KSPLogger) : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val content = classDeclaration.annotations.first().arguments.first().value.toString().trim()
            val className = classDeclaration.simpleName.asString() + "Fragment"
            val `package` = classDeclaration.packageName.asString()
            codeGenerator.createNewFile(
                Dependencies(false),
                `package`,
                classDeclaration.simpleName.asString() + "Fragment",
                "kt"
            ).use {
                it.write(
                    """
                        package $`package`
                        data class $className (
                            ${content.split("\n").joinToString { it.trim() }}
                        ){
                            companion object {
                                val TEXT = "DUPA"
                            }
                        }
                    """.trimIndent().toByteArray()
                )
            }
        }
    }
}

class BuilderProcessorProvider : SymbolProcessorProvider {
    override fun create(
        env: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return BuilderProcessor(env.codeGenerator, env.logger)
    }
}