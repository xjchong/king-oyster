package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.TelegraphActions
import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.TelegraphEvent

class TelegraphPart private constructor(telegraphs: List<Telegraph>) : Part {

    var telegraphs: List<Telegraph> = listOf()
        private set

    constructor(): this(listOf())

    override fun copy(): Part {
        return TelegraphPart(telegraphs)
    }

    override fun update(context: Context, partOwner: Entity) {
        val remainingTelegraphs = mutableListOf<Telegraph>()

        for (telegraph in telegraphs) {
            if (partOwner.health() < 0) break

            telegraph.update()

            if (telegraph.turnsRemaining > 0) {
                remainingTelegraphs.add(telegraph)
            }
        }

        telegraphs = remainingTelegraphs

        EventBus.post(TelegraphEvent(partOwner, telegraphs))
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is TelegraphActions -> partOwner.respondToTelegraphActions(action)
            else -> false
        }
    }

    private fun Entity.respondToTelegraphActions(action: TelegraphActions): Boolean {
        if (action.actor != this) return false

        telegraphs = telegraphs + listOf(action.telegraph)

        EventBus.post(TelegraphEvent(this, telegraphs))

        return true
    }
}

class Telegraph(val actor: Entity, turnsRemaining: Int, vararg payloads: TelegraphPayload){

    var turnsRemaining: Int = turnsRemaining
        private set
    
    val payloads: List<TelegraphPayload> = payloads.toList()

    fun update() {
        turnsRemaining--

        if (turnsRemaining <= 0) {
            // Execute the payloads.
            for (payload in payloads) {
                val (action, position) = payload
                val actor = action.actor

                if (actor.health() <= 0) break

                action.world.respondToActions(position, action)
            }
        }
    }
}

data class TelegraphPayload(val action: Action, val position: Position)

fun Entity.telegraphedPositions(): List<Position> {
    return find<TelegraphPart>()?.telegraphs?.flatMap { telegraph ->
        telegraph.payloads.map { payload ->
            payload.position
        }
    } ?: listOf()
}