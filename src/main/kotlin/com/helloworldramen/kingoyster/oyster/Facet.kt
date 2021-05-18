package com.helloworldramen.kingoyster.oyster

interface Facet<C : Context> {

    fun respondToAction(facetOwner: Entity<C>, action: Action): Boolean
}