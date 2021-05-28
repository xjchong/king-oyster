package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Close
import com.helloworldramen.kingoyster.actions.Open
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class DoorPart(var isOpen: Boolean) : Part {

    override fun copy(): Part {
        return DoorPart(isOpen)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Open -> partOwner.respondToOpen()
            is Close -> partOwner.respondToClose(action.context)
            else -> false
        }
    }

    private fun Entity.respondToOpen(): Boolean {
        if (isOpen) return false

        isOpen = true
        find(PhysicalPart::class)?.run {
            isPassable = true
            doesBlockVision = false
        }

        return true
    }

    private fun Entity.respondToClose(context: Context): Boolean {
        if (!isOpen) return false

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

        return true
    }
}