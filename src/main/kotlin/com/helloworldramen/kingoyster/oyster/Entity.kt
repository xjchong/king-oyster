package com.helloworldramen.kingoyster.oyster

import kotlin.reflect.KClass

class Entity (
    val name: String,
    val parts: List<Part> = listOf(),
    var timeFactor: Double = 0.0, // 0 means this entity needs no updates.
    var time: Double = 0.0
) {

    fun copy(): Entity {
        return Entity(name, parts.map { it.copy() }, timeFactor, time)
    }

    fun respondToAction(action: Action): Boolean {
        // Actors shouldn't be able to perform actions if its not their turn yet.
        if (action.context.world.currentTime < action.actor.time) return false

        val didRespond = parts.sumBy { if (it.respondToAction(this, action)) 1 else 0 } > 0

        if (didRespond) {
            with (action.actor) {
                time += (BASE_TIME_STEP * timeFactor * action.timeFactor)
            }
        }

        return didRespond
    }

    fun update(context: Context, world: World) {
        if (time > world.currentTime) return

        parts.forEach { it.update(context, this) }
    }

    fun idle(world: World): Boolean {
        if (time > world.currentTime) return false

        time += BASE_TIME_STEP * timeFactor
        return true
    }

    // Deprecate this.
    inline fun <reified P : Part> find(klass: KClass<P>): P? {
        return parts.find { klass.isInstance(it) } as? P
    }

    // Deprecate this.
    inline fun <reified P : Part> has(klass: KClass<P>): Boolean {
        return parts.any { klass.isInstance(it) }
    }

    inline fun <reified P : Part> find(): P? {
        return parts.find { P::class.isInstance(it) } as? P
    }

    inline fun <reified P : Part> has() : Boolean {
        return parts.any { P::class.isInstance(it) }
    }

    companion object {
        private const val BASE_TIME_STEP = 100

        val UNKNOWN: Entity = Entity("unknown")
    }
}