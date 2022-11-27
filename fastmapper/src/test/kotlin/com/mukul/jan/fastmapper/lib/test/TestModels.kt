package com.mukul.jan.fastmapper.lib.test

data class TestModelSource(
    val nameInt: Int = 0,
    val nameString: String = "",
    val model: TestModel = TestModel(),
    val nameDouble: Double = 0.0,
    val nameFloat: Float = 0f
)

data class TestModelTarget(
    val nameInt: Int = 0,
    val nameString: String = "",
    val model: TestModel = TestModel(),
    val nameDouble: Double = 0.0,
    val nameFloat: Float = 0f
)

data class TestModel(
    val name: String = ""
)