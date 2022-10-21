package com.example

import com.example.annotation.QueryFragment

class Fragment(val query: String)

interface Component<T> {
    val fragment: Fragment
    fun runLogic(inputs: T)
}

@QueryFragment(
    """
        val foo: Int
        val xd: Long
    """
)
class FooComponent : Component<FooComponentFragment> {
    override val fragment: Fragment = Fragment(FooComponentFragment.TEXT)
    override fun runLogic(inputs: FooComponentFragment) {

    }
}

fun main(){

}