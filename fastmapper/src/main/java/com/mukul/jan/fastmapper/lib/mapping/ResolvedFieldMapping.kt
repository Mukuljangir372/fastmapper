package com.mukul.jan.fastmapper.lib.mapping

import com.mukul.jan.fastmapper.lib.transformer.MappingTransformer
import java.lang.reflect.Field

data class ResolvedFieldMapping(
    val fromField: Field,
    val toField: Field,
    val transformer: List<MappingTransformer<*, *>>,
)