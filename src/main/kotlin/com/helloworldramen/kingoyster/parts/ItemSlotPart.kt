package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.DropItem
import com.helloworldramen.kingoyster.actions.UseItem
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.DropItemEvent
import com.helloworldramen.kingoyster.eventbus.events.UseItemEvent

class ItemSlotPart(
    var item: Entity? = null
) : Part {

    override fun copy(): Part {
        return ItemSlotPart(item?.copy())
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is DropItem -> partOwner.respondToDropItem(action)
            is UseItem -> partOwner.respondToUseItem(action)
            else -> false
        }
    }

    private fun Entity.respondToDropItem(action: DropItem): Boolean {
        val (context, actor) = action

        if (this != actor) return false

        val currentPosition = context.positionOf(actor) ?: return false
        val item = item ?: return false

        if (!context.world.move(item, context.findDropPosition(currentPosition))) return false

        this@ItemSlotPart.item = null

        EventBus.post(DropItemEvent(actor, item))

        return true
    }

    private fun Entity.respondToUseItem(action: UseItem): Boolean {
        val (context, user) = action

        if (user != this) return false

        val itemSlot = user.find<ItemSlotPart>() ?: return false
        val item = itemSlot.item ?: return false
        val itemPart = item.find<ItemPart>() ?: return false

        if (!itemPart.effect(context, user)) return false

        EventBus.post(UseItemEvent(user, item))

        if (--itemPart.uses <= 0) {
            // Remove the item.
            itemSlot.item = null
            context.world.remove(item)
        }

        return true
    }
}

fun Entity.item(): Entity? {
    return find<ItemSlotPart>()?.item
}
