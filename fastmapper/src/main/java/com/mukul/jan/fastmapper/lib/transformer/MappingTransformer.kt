package com.mukul.jan.fastmapper.lib.transformer

interface MappingTransformer<From, To> {
    fun transform(context: MappingTransformerContext<From, To>): To?
}