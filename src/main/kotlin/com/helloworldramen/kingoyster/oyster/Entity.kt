package com.helloworldramen.kingoyster.oyster

import kotlin.reflect.KClass

open class Entity (
    val parts: List<Part> = listOf(),
    var requiresUpdate: Boolean = false,
    var requiresInput: Boolean = false,
    var nextUpdateTime: Int = 0
) {

    constructor(vararg parts: Part) : this(parts.toList())

    fun copy(): Entity {
        return Entity(parts.map { it.copy() }, requiresUpdate, requiresInput, nextUpdateTime)
    }

    fun respondToAction(action: Action): Boolean {
        return parts.sumBy { if (it.respondToAction(this, action)) 1 else 0 } > 0
    }

    fun update(context: Context) {
        parts.forEach { it.update(context, this) }
    }

    inline fun <reified P : Part> find(klass: KClass<P>): P? {
        return parts.find { klass.isInstance(it) } as? P
    }

    inline fun <reified P : Part> has(klass: KClass<P>): Boolean {
        return parts.any { klass.isInstance(it) }
    }
}