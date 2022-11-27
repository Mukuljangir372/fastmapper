package com.mukul.jan.fastmapper.lib.test

import com.mukul.jan.fastmapper.lib.FastMapper
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class FastMapperTest {
    private lateinit var mapper: FastMapper

    @Before
    fun setup() {
        mapper = FastMapper.Builder.build {
            withMapping<TestModelSource, TestModelTarget> {

            }
        }
    }

    @Test
    fun `mapper should map`() {
        val sourceModel = TestModelSource(
            name = "hi",
            model = TestModel(name = "hi")
        )
        val resultTarget: TestModelTarget = mapper.map(sourceModel)
        assertEquals(sourceModel.name,resultTarget.name2)
    }
}














