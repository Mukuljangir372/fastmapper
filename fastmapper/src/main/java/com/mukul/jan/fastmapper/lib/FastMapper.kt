package com.mukul.jan.fastmapper.lib

import com.mukul.jan.fastmapper.lib.dsl.KotlinDslMappingDefinitionBuilder
import com.mukul.jan.fastmapper.lib.mapping.FieldMapping
import com.mukul.jan.fastmapper.lib.mapping.MappingDefinition
import com.mukul.jan.fastmapper.lib.mapping.ResolvedFieldMapping
import com.mukul.jan.fastmapper.lib.transformer.DefaultMappingTransformer
import com.mukul.jan.fastmapper.lib.transformer.MappingTransformer
import com.mukul.jan.fastmapper.lib.transformer.MappingTransformerContext
import com.mukul.jan.fastmapper.lib.utils.getValue
import com.mukul.jan.fastmapper.lib.utils.setValue
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class FastMapper private constructor(
    val mappingDefinitions: MutableSet<MappingDefinition>,
    val transformers: MutableSet<MappingTransformer<*, *>>
) {

    /**
     * Map From -> To
     */
    private fun <From : Any, To : Any> map(from: From, to: To): To {
        val definition = withMappingDefinition(from, to)
        definition.fields.forEach {
            withMapField(from = from, to = to, fieldMapping = it)
        }
        return to
    }

    /**
     * Map From -> To
     */
    fun <From : Any, To : Any> map(from: From, to: Class<To>): To {
        val toObject = initializeObject(to)
        return map(from, toObject)
    }

    /**
     * Map From -> To
     */
    inline fun <reified From : Any, reified To : Any> map(from: From): To {
        return map(from = from, to = To::class.java)
    }

    /**
     * Returns the mapping definition if found
     */
    private fun <From : Any, To : Any> withMappingDefinition(
        from: From, to: To
    ): MappingDefinition {
        val definition = mappingDefinitions.find {
            it.fromClazz == from::class.java && it.toClazz == to::class.java
        }
            ?: error("No Mapping Definition Found. You need to add mapping for ${from::class.java.simpleName} And ${to::class.java.simpleName}")
        return definition
    }

    /**
     * Map the field
     */
    @Suppress("UNCHECKED_CAST")
    private fun <From : Any, To : Any> withMapField(
        from: From, to: To, fieldMapping: ResolvedFieldMapping
    ): ResolvedFieldMapping {
        val fromField = fieldMapping.fromField
        val toField = fieldMapping.toField
        val fromFieldType = fromField.type
        val toFieldType = toField.type

        fromField.isAccessible = true
        toField.isAccessible = true

        val autoRegisteredTransformer = findTransformer(
            transformers = transformers.toList(),
            fromType = fromFieldType,
            toType = toFieldType
        )

        try {
            val valueToSet = if (fieldMapping.transformer.isNotEmpty()) {
                val transformer = findTransformer(
                    transformers = fieldMapping.transformer,
                    fromType = fromFieldType,
                    toType = toFieldType
                )
                val context = MappingTransformerContext(
                    toField = toField,
                    fromField = fromField,
                    fromType = fromFieldType,
                    toType = toFieldType,
                    fromFieldOriginalValue = fromField.getValue(from)
                )
                transformer?.transform(context)
            } else if (autoRegisteredTransformer != null) {
                val context = MappingTransformerContext(
                    toField = toField,
                    fromField = fromField,
                    fromType = fromFieldType,
                    toType = toFieldType,
                    fromFieldOriginalValue = fromField.getValue(from)
                )
                autoRegisteredTransformer.transform(context)
            } else {
                fromField.getValue(from)
            }
            toField.setValue(to, valueToSet)
        } catch (e: Exception) {
            val exception =
                java.lang.Exception("Failed to map: ${from::class.java.simpleName} ${fromField.name} ${fromFieldType.name} while mapping with ${to::class.java.simpleName} ${toField.name} ${toFieldType.name}")
            exception.initCause(e)
            throw exception
        }
        return fieldMapping
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified FromType : Any, reified ToType : Any> findTransformer(
        transformers: List<MappingTransformer<*, *>>,
        fromType: FromType,
        toType: ToType
    ): MappingTransformer<FromType, ToType>? {
        var transformer: MappingTransformer<FromType, ToType>? = null
        run breaker@{
            transformers.forEach {
                transformer = try {
                    it as MappingTransformer<FromType, ToType>
                    return@breaker
                } catch (e: Exception) {
                    null
                }
            }
        }
        return transformer
    }

    /**
     * Initialize the object
     */
    @Suppress("UNCHECKED_CAST")
    fun <Type> initializeObject(clazz: Class<Type>): Type {
        val constructor = clazz.constructors.firstOrNull { it.parameterCount == 0 }
        if (constructor != null) {
            constructor.isAccessible = true
            return constructor.newInstance() as Type
        }
        error("Could not find no-args constructor for class $clazz")
    }

    class Builder private constructor(
        /**
         * Using thread-safe set for defining all mapping definitions
         */
        private val mappingDefinitions: MutableSet<MappingDefinition> = Collections.newSetFromMap(
            ConcurrentHashMap()
        ),
        val transformers: MutableSet<MappingTransformer<*, *>> = Collections.newSetFromMap(
            ConcurrentHashMap()
        )
    ) {
        companion object {
            fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
            fun build() = Builder().build()
        }

        fun build(): FastMapper {
            return FastMapper(
                mappingDefinitions = mappingDefinitions, transformers = transformers
            )
        }

        /**
         * Add/Build Mapping Definition
         */
        inline fun <reified From : Any, reified To : Any> withMapping(block: KotlinDslMappingDefinitionBuilder<From, To>.() -> Unit): Builder {
            val definition = KotlinDslMappingDefinitionBuilder(
                fromClazz = From::class.java, toClazz = To::class.java
            ).apply(block).build()
            withMapping(definition)
            return this
        }

        /**
         * Add/Build Mapping Definition
         */
        inline fun <reified From : Any, reified To : Any> withMapping(): Builder {
            val definition = KotlinDslMappingDefinitionBuilder(
                fromClazz = From::class.java, toClazz = To::class.java
            ).build()
            withMapping(definition)
            return this
        }

        /**
         * Add Mapping Definition
         */
        fun withMapping(definition: MappingDefinition): Builder {
            this.mappingDefinitions.add(definition)
            return this
        }

        /**
         * Add/Build Mapping Field Transformer
         */

        inline fun <reified FromType, reified ToType> withTransformer(
            noinline transform: (value: FromType?) -> ToType
        ): Builder {
            val transformer = DefaultMappingTransformer(transform)
            withTransformer(transformer)
            return this
        }

        /**
         * Add Mapping Field Transformer to Builder
         */

        inline fun <reified FromType, reified ToType> withTransformer(transformer: MappingTransformer<FromType, ToType>): Builder {
            this.transformers.add(transformer)
            return this
        }
    }
}











