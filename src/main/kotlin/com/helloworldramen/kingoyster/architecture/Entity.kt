package com.helloworldramen.kingoyster.architecture

class Entity (
    val name: String,
    val parts: List<Part> = listOf(),
    var timeFactor: Double = 0.0, // 0 means this entity needs no updates.
    var time: Double = 0.0,
    var isPlayer: Boolean = false
) {

    private var lastSuccessfulResponse: Long = System.nanoTime()
    private var lastSuccessfulUpdate: Long = 0

    fun copy(): Entity {
        return Entity(name, parts.map { it.copy() }, timeFactor, time)
    }

    fun respondToAction(action: Action): Boolean {
        val didRespond = parts.sumBy { if (it.respondToAction(this, action)) 1 else 0 } > 0
        val actor = action.actor

        if (didRespond) {
            actor.time += (BASE_TIME_STEP * actor.timeFactor * action.timeFactor)
            actor.lastSuccessfulResponse = System.nanoTime()
        }

        return didRespond
    }

    fun update(context: Context, world: World): Boolean {
        if (time > world.currentTime) return false
        if (lastSuccessfulUpdate > lastSuccessfulResponse) return false

        lastSuccessfulUpdate = System.nanoTime()

        parts.forEach { it.update(context, this) }

        return true
    }

    fun idle(world: World): Boolean {
        if (time > world.currentTime) return false

        time += BASE_TIME_STEP * timeFactor
        return true
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