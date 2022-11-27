package com.mukul.jan.fastmapper.lib.transformer.inbuilt

import com.mukul.jan.fastmapper.lib.transformer.MappingTransformer
import com.mukul.jan.fastmapper.lib.transformer.MappingTransformerContext

class IntegerToStringMappingTransformer: MappingTransformer<Int, String>{
    override fun transform(context: MappingTransformerContext<Int, String>): String {
        context.fromFieldOriginalValue ?: return ""
        return context.fromFieldOriginalValue.toString()
    }
}