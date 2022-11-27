package com.mukul.jan.fastmapper.lib.dsl

import com.mukul.jan.fastmapper.lib.annotation.MappingDefinitionKotlinDsl
import com.mukul.jan.fastmapper.lib.mapping.FieldMapping
import com.mukul.jan.fastmapper.lib.mapping.MappingDefinition
import com.mukul.jan.fastmapper.lib.utils.getDeclaredFieldsRecursive
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.javaField

@MappingDefinitionKotlinDsl
class KotlinDslMappingDefinitionBuilder<RootFrom : Any, RootTo : Any>(
    private val fromClazz: Class<RootFrom>, private val toClazz: Class<RootTo>
) {

    private val classTarget = this@KotlinDslMappingDefinitionBuilder

    /**
     * Field Mappings are the mappings of field from class
     */
    private val fieldMappings: MutableList<FieldMapping> = mutableListOf()

    /**
     * Map field from -> to
     */
    infix fun <From : Any, FromValue : Any, To : Any, ToValue : Any> KProperty1<From, FromValue>.mapTo(
        to: KProperty1<To, ToValue>
    ): KotlinDslMappingDefinitionBuilder<RootFrom, RootTo> {
        val fromField = this
        val fieldMapping = FieldMapping(
            fromField = fromField.javaField!!, toField = to.javaField!!, transformer = emptyList()
        )
        withFieldMapping(fieldMapping)
        return classTarget
    }

    /**
     * Add Field Mapping
     */
    private fun withFieldMapping(fieldMapping: FieldMapping): KotlinDslMappingDefinitionBuilder<RootFrom, RootTo> {
        fieldMappings.add(fieldMapping)
        return this
    }

    /**
     * AutoFieldMappings are undefined mappings
     */
    private fun autoFieldMappings(): List<FieldMapping> {
        val mappings = mutableListOf<FieldMapping>()
        val fromFields = fromClazz.getDeclaredFieldsRecursive()
        val toFields = toClazz.getDeclaredFieldsRecursive()

        fromFields.forEach { fromField ->
            val toField = toFields.find { it.name == fromField.name }
            fromField.isAccessible = true
            toField?.isAccessible = true

            if (toField != null) {
                val mapping = FieldMapping(
                    fromField = fromField, toField = toField, transformer = emptyList()
                )
                mappings.add(mapping)
            }
        }
        return mappings
    }

    fun build(): MappingDefinition {
        val mappedAutoFieldMappings = autoFieldMappings().filter { autoFieldMapping ->
            fieldMappings.none {
                it.toField == autoFieldMapping.toField || it.fromField == autoFieldMapping.fromField
            }
        }
        val mappings = (mappedAutoFieldMappings + fieldMappings)

        return MappingDefinition(
            fromClazz = fromClazz,
            toClazz = toClazz,
            fields = mappings
        )
    }
}