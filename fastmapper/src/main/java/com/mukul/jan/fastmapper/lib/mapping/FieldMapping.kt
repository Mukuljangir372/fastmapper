package com.mukul.jan.fastmapper.lib.mapping

import com.mukul.jan.fastmapper.lib.transformer.FieldMappingTransformer
import java.lang.reflect.Field

data class FieldMapping(
    val fromField: Field,
    val toField: Field,
    val transformer: List<FieldMappingTransformer>,
)