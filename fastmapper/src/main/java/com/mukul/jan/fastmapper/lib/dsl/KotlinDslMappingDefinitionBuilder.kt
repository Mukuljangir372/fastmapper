package com.mukul.jan.fastmapper.lib.dsl

import com.mukul.jan.fastmapper.lib.annotation.MappingDefinitionKotlinDsl
import com.mukul.jan.fastmapper.lib.mapping.FieldMapping
import com.mukul.jan.fastmapper.lib.mapping.MappingDefinition
import com.mukul.jan.fastmapper.lib.mapping.ResolvedFieldMapping
import com.mukul.jan.fastmapper.lib.transformer.DefaultMappingTransformer
import com.mukul.jan.fastmapper.lib.transformer.EmptyMappingTransformer
import com.mukul.jan.fastmapper.lib.transformer.MappingTransformer
import com.mukul.jan.fastmapper.lib.utils.getDeclaredFieldsRecursive
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaField

@MappingDefinitionKotlinDsl
class KotlinDslMappingDefinitionBuilder<RootFrom : Any, RootTo : Any>(
    private val fromClazz: Class<RootFrom>, private val toClazz: Class<RootTo>
) {

    /**
     * Field Mappings are the mappings of field from class
     */
    private val fieldMappings: MutableList<ResolvedFieldMapping> = mutableListOf()

    /**
     * Map field from -> to with Transformer
     */
    inline fun <From : Any, reified FromValue : Any, To : Any, reified ToValue : Any> KProperty1<From, FromValue>.mapTo(
        to: KProperty1<To, ToValue>,
        transformer: MappingTransformer<*, *>,
    ): FieldMapping<FromValue, ToValue> {
        val fromField = this
        val fieldMapping = FieldMapping<FromValue, ToValue>(
            fromField = fromField.javaField!!,
            toField = to.javaField!!,
            transformer = mutableListOf(transformer)
        )
        withFieldMapping(fieldMapping)
        return fieldMapping
    }

    /**
     * Map field from -> to
     */

    inline infix fun <From : Any, reified FromValue : Any, To : Any, reified ToValue : Any> KProperty1<From, FromValue>.mapTo(
        to: KProperty1<To, ToValue>,
    ): FieldMapping<FromValue, ToValue> {
        return mapTo(to, transformer = EmptyMappingTransformer())
    }

    /**
     * Map field from -> to with Transformer
     */

    inline fun <reified FromValue : Any, reified ToValue : Any> FieldMapping<FromValue, ToValue>.withTransformer(
        noinline transform: (value: FromValue) -> ToValue
    ): FieldMapping<FromValue, ToValue> {
        val fieldMapping = this
        val transformer = DefaultMappingTransformer(transform)
        fieldMapping.transformer.add(transformer)
        return fieldMapping
    }

    /**
     * Add Field Mapping
     */
    fun withFieldMapping(fieldMapping: FieldMapping<*, *>): KotlinDslMappingDefinitionBuilder<RootFrom, RootTo> {
        fieldMappings.add(
            ResolvedFieldMapping(
                fromField = fieldMapping.fromField,
                toField = fieldMapping.toField,
                transformer = fieldMapping.transformer
            )
        )
        return this
    }

    /**
     * AutoFieldMappings are undefined mappings
     */
    private fun autoFieldMappings(): List<ResolvedFieldMapping> {
        val mappings = mutableListOf<ResolvedFieldMapping>()
        val fromFields = fromClazz.getDeclaredFieldsRecursive()
        val toFields = toClazz.getDeclaredFieldsRecursive()

        fromFields.forEach { fromField ->
            val toField = toFields.find { it.name == fromField.name }
            fromField.isAccessible = true
            toField?.isAccessible = true

            if (toField != null) {
                val mapping = ResolvedFieldMapping(
                    fromField = fromField,
                    toField = toField,
                    transformer = emptyList(),
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
            fromClazz = fromClazz, toClazz = toClazz, fields = mappings
        )
    }
}