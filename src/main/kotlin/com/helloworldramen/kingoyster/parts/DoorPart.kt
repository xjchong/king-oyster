package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Close
import com.helloworldramen.kingoyster.actions.Open
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DoorEvent

class DoorPart(var isOpen: Boolean) : Part {

    override fun copy(): Part {
        return DoorPart(isOpen)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Open -> partOwner.respondToOpen(action)
            is Close -> partOwner.respondToClose(action)
            else -> false
        }
    }

    private fun Entity.respondToOpen(action: Open): Boolean {
        if (isOpen) return false

        isOpen = true
        find(PhysicalPart::class)?.run {
            isPassable = true
            doesBlockVision = false
        }

        EventBus.post(DoorEvent(this, action.actor, true))

        return true
    }

    private fun Entity.respondToClose(action: Close): Boolean {
        if (!isOpen) return false

        val (context, actor) = action
        val currentPosition = context.positionOf(this) ?: return false
        val isBlocked = context.entitiesAt(currentPosition)?.any {
            it != this && it.isCorporeal()
        } != false

        if (isBlocked) return false

        isOpen = false
        find(PhysicalPart::class)?.run{
            isPassable = false
            doesBlockVision = true
        }

        EventBus.post(DoorEvent(this, actor, false))

        return true
    }
}