package com.helloworldramen.kingoyster.oyster

import kotlin.reflect.KClass

class Entity (
    val name: String,
    val parts: List<Part>,
    var timeFactor: Double = 0.0, // 0 means this entity needs no updates.
    var requiresInput: Boolean = false,
    var nextUpdateTime: Double = 0.0
) {

    fun copy(): Entity {
        return Entity(name, parts.map { it.copy() }, timeFactor, requiresInput, nextUpdateTime)
    }

    fun respondToAction(action: Action): Boolean {
        // Actors shouldn't be able to perform actions if its not their turn yet.
        if (action.context.world.currentTime < action.actor.nextUpdateTime) return false

        val didRespond = parts.sumBy { if (it.respondToAction(this, action)) 1 else 0 } > 0

        if (didRespond) {
            with (action.actor) {
                nextUpdateTime += (BASE_TIME_STEP * timeFactor * action.timeFactor).toInt()
            }
        }

        return didRespond
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

    companion object {
        const val BASE_TIME_STEP = 100
    }
}