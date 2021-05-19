package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Take
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

object ItemPart : Part {

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
            context.world.remove(this)

            return true
        }

        return false
    }
}