package com.interrupt.server.docs

import org.springframework.restdocs.constraints.ConstraintDescriptions
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.snippet.Attributes.Attribute
import org.springframework.restdocs.snippet.Attributes.key

class ConstrainedFields(clazz: Class<*>) {
    private val constraintDescriptions = ConstraintDescriptions(clazz) { it ->
        return@ConstraintDescriptions it.configuration["message"].toString()
    }

    fun withPath(path: String): FieldDescriptor = fieldWithPath(path).attributes(getValidationMessage(path))

    fun withName(name: String): ParameterDescriptor = parameterWithName(name).attributes(getValidationMessage(name))

    private fun getValidationMessage(field: String): Attribute =
        key("constraints").value(
            this.constraintDescriptions.descriptionsForProperty(field)
                .joinToString(separator = "\n\n") { "- $it" }
        )

}