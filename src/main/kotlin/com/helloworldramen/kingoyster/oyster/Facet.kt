package com.helloworldramen.kingoyster.oyster

interface Facet<C : Context> {

    fun respondToAction(context: C, action: Action): Boolean
}