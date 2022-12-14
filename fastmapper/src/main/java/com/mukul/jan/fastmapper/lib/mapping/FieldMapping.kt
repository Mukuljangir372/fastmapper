package com.mukul.jan.fastmapper.lib.mapping

import com.mukul.jan.fastmapper.lib.transformer.MappingTransformer
import java.lang.reflect.Field

data class FieldMapping<FromValue: Any, ToValue: Any>(
    val fromField: Field,
    val toField: Field,
    val transformer: MutableList<MappingTransformer<*, *>>,
)