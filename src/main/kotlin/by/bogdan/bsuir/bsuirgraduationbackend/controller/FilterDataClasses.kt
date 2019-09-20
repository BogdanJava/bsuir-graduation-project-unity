package by.bogdan.bsuir.bsuirgraduationbackend.controller

enum class Operator {
    EQ, IN, LT, GT, LTE, GTE, CONTAINS,
    CONTAINS_I,// case insensitive
    AND, OR
}

enum class ValueType {
    DATE, ANY
}

class ValueContainer {
    lateinit var operator: Operator
    lateinit var value: Any
    var not: Boolean = false
    var type: ValueType = ValueType.ANY
}