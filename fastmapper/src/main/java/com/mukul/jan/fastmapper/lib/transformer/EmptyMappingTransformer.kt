package com.mukul.jan.fastmapper.lib.transformer

class EmptyMappingTransformer: MappingTransformer<Nothing, Nothing> {
    override fun transform(context: MappingTransformerContext<Nothing, Nothing>): Nothing? {
        return null
    }
}