package com.mukul.jan.fastmapper.lib.coordinates

import kotlin.reflect.KProperty1

data class FieldCoordinate(
    val fromField: KProperty1<*, *>,
    val toField: KProperty1<*, *>
)