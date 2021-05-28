package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Take
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.TakeEvent

class ItemPart : Part {

    override fun copy(): Part {
        return ItemPart()
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Take -> partOwner.respondToTake(action)
            else -> false
        }
    }

    private fun Entity.respondToTake(action: Take): Boolean {
        val (context, taker) = action
        val takerInventory = taker.find(InventoryPart::class) ?: return false

        if (takerInventory.put(this)) {
            EventBus.post(TakeEvent(taker, this))
            context.world.remove(this)

            return true
        }

        return false
    }
}