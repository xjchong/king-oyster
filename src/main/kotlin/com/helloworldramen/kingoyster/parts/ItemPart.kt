package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.DropItem
import com.helloworldramen.kingoyster.actions.Take
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.TakeItemEvent

class ItemPart(
    var uses: Int,
    var effect: (context: Context, user: Entity) -> Boolean
) : Part {

    override fun copy(): Part {
        return ItemPart(uses, effect)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Take -> partOwner.respondToTake(action)
            else -> false
        }
    }

    private fun Entity.respondToTake(action: Take): Boolean {
        val (context, actor) = action
        val itemSlot = actor.find<ItemSlotPart>() ?: return false

        // Remove the item from the floor.
        if (!context.world.move(this, null)) return false

        // Drop the current item if any.
        actor.respondToAction(DropItem(context, actor))

        // Take this item.
        itemSlot.item = this

        EventBus.post(TakeItemEvent(actor, this))

        return true
    }
}

fun Entity.uses(): Int = find<ItemPart>()?.uses ?: -1