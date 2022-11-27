package com.mukul.jan.fastmapper.lib.test

import com.mukul.jan.fastmapper.lib.FastMapper
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FastMapperTest {
    @Test
    fun `should throw exception if mapper not defined`() {
        val mapper = FastMapper.Builder.build()
        val source = TestModelSource()
        assertThrows(IllegalStateException::class.java) {
            mapper.map(source)
        }
    }

    @Test
    fun `should map all fields by name without registration`() {
        val mapper = FastMapper.Builder.build {
            withMapping<TestModelSource, TestModelTarget> {}
        }
        val source = TestModelSource(
            nameString = "test"
        )
        val mapped: TestModelTarget = mapper.map(source)
        assertEquals(source.nameString, mapped.nameString)
    }

    @Test
    fun `test`() {
        val mapper = FastMapper.Builder.build {
            withTransformer<String, Long> { it?.toLong()!! }

            withMapping<TestModelSource, TestModelTarget> {
                (TestModelSource::nameString mapTo TestModelTarget::nameInt).withTransformer {
                    it.toInt()
                }
            }
        }
        val source = TestModelSource(
            nameString = "1"
        )
        val mapped: TestModelTarget = mapper.map(source)
        assertEquals(source.nameInt, mapped.nameString)
    }

}














