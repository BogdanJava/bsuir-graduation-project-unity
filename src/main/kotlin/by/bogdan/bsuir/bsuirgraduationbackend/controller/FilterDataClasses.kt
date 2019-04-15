package by.bogdan.bsuir.bsuirgraduationbackend.controller

enum class Operator {
    EQ, IN, LT, GT, LTE, GTE
}

class ValueContainer() {
    lateinit var operator: Operator
    lateinit var value: Any

    constructor(operator: Operator, value: Any) : this() {
        this.operator = operator;
        this.value = value;
    }
}