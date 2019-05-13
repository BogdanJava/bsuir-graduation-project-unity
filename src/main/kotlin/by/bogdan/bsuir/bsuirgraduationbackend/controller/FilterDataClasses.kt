package by.bogdan.bsuir.bsuirgraduationbackend.controller

enum class Operator {
    EQ, IN, LT, GT, LTE, GTE, CONTAINS,
    CONTAINS_I // case insensitive
}

class ValueContainer {
    lateinit var operator: Operator
    lateinit var value: Any
}