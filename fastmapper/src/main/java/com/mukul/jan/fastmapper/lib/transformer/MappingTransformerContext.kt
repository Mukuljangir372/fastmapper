package com.mukul.jan.fastmapper.lib.transformer

import java.lang.reflect.Field

data class MappingTransformerContext<FromType, ToType>(
    val fromField: Field,
    val toField: Field,
    val fromType: FromType,
    val toType: ToType,
    val fromFieldOriginalValue: Any?
)