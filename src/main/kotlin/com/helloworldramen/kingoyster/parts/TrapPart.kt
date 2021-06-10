package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.TriggerTrapEvent

class TrapPart(private val effect: (context: Context, trap: Entity, entity: Entity) -> Boolean, uses: Int = 1) : Part {

    var uses: Int = uses
        private set

    override fun copy(): Part {
        return TrapPart(effect)
    }

    fun trigger(context: Context, partOwner: Entity, triggerer: Entity): Boolean {
        if (partOwner.find<TrapPart>() != this) return false
        if (!effect(context, partOwner, triggerer)) return false

        if (--uses <= 0) {
            context.world.remove(partOwner)
        }

        EventBus.post(TriggerTrapEvent(partOwner, triggerer))

        return true
    }
}

fun Entity.trigger(context: Context, triggerer: Entity): Boolean {
    return find<TrapPart>()?.trigger(context, this, triggerer) ?: false
}

fun Entity.trapUses(): Int = find<TrapPart>()?.uses ?: -1