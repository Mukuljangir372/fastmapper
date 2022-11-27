package com.mukul.jan.fastmapper.lib.test

data class TestModelSource(
    val name: String,
    val model: TestModel,
)

data class TestModelTarget(
    val name2: String = "",
    val name3: String = "",
    val model: TestModel = TestModel()
)


data class TestModel(
    val name: String = ""
)