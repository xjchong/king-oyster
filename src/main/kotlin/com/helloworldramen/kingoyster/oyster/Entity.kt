package com.helloworldramen.kingoyster.oyster

import kotlin.reflect.KClass

class Entity(
    val attributes: List<Attribute> = listOf(),
    val facets: List<Facet> = listOf(),
    val behaviors: List<Behavior> = listOf(),
    var requiresInput: Boolean = false,
    var nextUpdateTime: Int = 0
) {

    fun respondToAction(context: Context, action: Action): Boolean {
        return facets.sumBy { if (it.respondToAction(context, action)) 1 else 0 } > 0
    }

    fun executeBehaviors(context: Context) {
        behaviors.forEach { it.execute(context, this) }
    }

    inline fun <reified A : Attribute> findAttribute(klass: KClass<A>): A? {
        return attributes.find { klass.isInstance(it) } as? A
    }

    inline fun <reified A : Attribute> hasAttribute(klass: KClass<A>): Boolean {
        return attributes.any { klass.isInstance(it) }
    }

    inline fun <reified F : Facet> hasFacet(klass: KClass<F>): Boolean {
        return facets.any { klass.isInstance(it) }
    }

    inline fun <reified B : Behavior> hasBehavior(klass: KClass<B>): Boolean {
        return behaviors.any { klass.isInstance(it) }
    }
}