package com.mukul.jan.fastmapper.lib.mapping

data class MappingDefinition(
    val fromClazz: Class<*>,
    val toClazz: Class<*>,
    val fields: List<ResolvedFieldMapping>
)