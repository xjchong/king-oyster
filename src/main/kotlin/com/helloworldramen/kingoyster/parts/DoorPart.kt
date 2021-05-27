package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Close
import com.helloworldramen.kingoyster.actions.Open
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class DoorPart(var isOpen: Boolean) : Part {

    override fun copy(): Part {
        return DoorPart(isOpen)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Open -> respondToOpen(partOwner)
            is Close -> respondToClose(partOwner)
            else -> false
        }
    }

    private fun respondToOpen(partOwner: Entity): Boolean {
        if (isOpen) return false

        isOpen = true
        partOwner.find(PhysicalPart::class)?.run {
            isPassable = true
            doesBlockVision = false
        }

        return true
    }

    private fun respondToClose(partOwner: Entity): Boolean {
        if (!isOpen) return false

        isOpen = false
        partOwner.find(PhysicalPart::class)?.run{
            isPassable = false
            doesBlockVision = true
        }

        return true
    }
}