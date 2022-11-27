package com.mukul.jan.fastmapper.lib.utils

import java.lang.reflect.Field

data class ClassPair<From, To>(val from: Class<out From>, val to: Class<out To>)

internal fun Field.getValue(target: Any): Any? {
    return this.get(target)
}

internal fun Field.setValue(target: Any, value: Any?) {
    this.set(target, value)
}

internal fun Class<*>.getDeclaredFieldsRecursive(): List<Field> {
    var clazz: Class<*>? = this
    val fields = mutableListOf<Field>()
    while (clazz != null) {
        fields += clazz.declaredFields
        clazz = clazz.superclass
    }

    return fields
}

internal fun Class<*>.getDeclaredFieldRecursive(name: String): Field {
    var clazz: Class<*>? = this
    while (clazz != null) {
        try {
            return clazz.getDeclaredField(name)
        } catch (e: NoSuchFieldException) {
            clazz = clazz.superclass
        }
    }
    throw NoSuchFieldException(name)
}