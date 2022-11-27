package com.mukul.jan.fastmapper.lib

import com.mukul.jan.fastmapper.lib.dsl.KotlinDslMappingDefinitionBuilder
import com.mukul.jan.fastmapper.lib.mapping.FieldMapping
import com.mukul.jan.fastmapper.lib.mapping.MappingDefinition
import com.mukul.jan.fastmapper.lib.utils.getValue
import com.mukul.jan.fastmapper.lib.utils.setValue
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

class FastMapper private constructor(
    val mappingDefinitions: MutableSet<MappingDefinition>
) {

    /**
     * Map From -> To
     */
    fun <From : Any, To : Any> map(from: From, to: To): To {
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
    private fun <From : Any, To : Any> withMapField(
        from: From, to: To, fieldMapping: FieldMapping
    ): FieldMapping {
        val fromField = fieldMapping.fromField
        val toField = fieldMapping.toField
        fromField.isAccessible = true
        toField.isAccessible = true


        toField.setValue(to, fromField.getValue(from))
        return fieldMapping
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
        )
    ) {
        companion object {
            fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
        }

        fun build(): FastMapper {
            return FastMapper(
                mappingDefinitions = mappingDefinitions
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
    }
}











