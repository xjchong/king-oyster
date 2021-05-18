package com.helloworldramen.kingoyster.oyster

interface Facet {

    fun respondToAction(context: Context, action: Action): Boolean
}