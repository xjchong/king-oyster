package com.helloworldramen.kingoyster.oyster

interface Behavior<C : Context> {

    fun execute(context: C, entity: Entity<C>)
}