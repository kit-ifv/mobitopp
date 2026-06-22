package edu.kit.ifv.utils

interface Builder<out E> {
    fun build(): E
    fun reset() {
    }
//    fun build(lambda: Builder<E>.() -> Unit): E {
//        val result = buildPreserving(lambda)
//        reset()
//        return result
//    }
//
//    fun buildPreserving(lambda: Builder<E>.() -> Unit): E {
//        this.apply(lambda)
//        return build()
//    }
}
