package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Telegraph
import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.TelegraphEvent
import com.helloworldramen.kingoyster.parts.combat.health

class TelegraphPart private constructor(telegraphs: List<TelegraphInfo>) : Part {

    var telegraphs: List<TelegraphInfo> = listOf()
        private set

    constructor(): this(listOf())

    override fun copy(): Part {
        return TelegraphPart(telegraphs)
    }

    override fun update(context: Context, partOwner: Entity) {
        val remainingTelegraphs = mutableListOf<TelegraphInfo>()

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
            is Telegraph -> partOwner.respondToTelegraph(action)
            else -> false
        }
    }

    private fun Entity.respondToTelegraph(action: Telegraph): Boolean {
        if (action.actor != this) return false

        telegraphs = telegraphs + listOf(action.telegraph)

        EventBus.post(TelegraphEvent(this, telegraphs))

        return true
    }
}

class TelegraphInfo(val actor: Entity, turnsRemaining: Int, val payloads: List<TelegraphPayload>){

    var turnsRemaining: Int = turnsRemaining
        private set

    fun update() {
        if (--turnsRemaining > 0) return

        // Execute the payloads.
        for (payload in payloads) {
            val (action, _) = payload
            val actor = action.actor

            if (actor.health() <= 0) break

            actor.respondToAction(action)
        }
    }
}

data class TelegraphPayload(val action: Action, val positions: List<Position>)

fun Entity.telegraphedPositions(): List<Position> {
    return find<TelegraphPart>()?.telegraphs?.flatMap { telegraph ->
        telegraph.payloads.flatMap { payload ->
            payload.positions
        }
    } ?: listOf()
}