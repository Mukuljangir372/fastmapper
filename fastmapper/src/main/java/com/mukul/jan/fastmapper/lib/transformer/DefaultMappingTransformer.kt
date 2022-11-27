package com.mukul.jan.fastmapper.lib.transformer

@Suppress("UNCHECKED_CAST")
class DefaultMappingTransformer<From, To>(
    private val transform: (From) -> To?
) : MappingTransformer<From, To> {
    override fun transform(context: MappingTransformerContext<From, To>): To? {
        return transform.invoke(context.fromFieldOriginalValue as From)
    }
}