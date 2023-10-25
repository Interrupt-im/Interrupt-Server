package com.interrupt.server.docs

import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.restdocs.snippet.Attributes.*
import kotlin.reflect.KFunction

class ConstrainedPath(function: KFunction<*>) {

    private val constraintDescriptions: Map<String?, List<String>> = function.parameters.associate { param ->
        param.name to param.annotations.filter { hasMessage(it) }.map { it.getMessage() }
    }

    fun withName(name: String): ParameterDescriptor = parameterWithName(name).attributes(getValidationMessage(name))

    private fun hasMessage(annotation: Annotation): Boolean =
        try {
            annotation.annotationClass.java.getDeclaredMethod("message")
                .invoke(annotation).toString().isBlank().not()
        } catch (e: Exception) {
            false
        }

    private fun Annotation.getMessage() =
        annotationClass.java.getDeclaredMethod("message").invoke(this).toString()

    private fun getValidationMessage(path: String): Attribute =
            key("constraints").value(
                this.constraintDescriptions[path]
                    ?.joinToString(separator = "\n\n") { "- $it" }
            )

}