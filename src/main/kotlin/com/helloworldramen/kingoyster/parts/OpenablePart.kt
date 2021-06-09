package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.DropItem
import com.helloworldramen.kingoyster.actions.DropWeapon
import com.helloworldramen.kingoyster.actions.Open
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.events.OpenEvent

class OpenablePart : Part {

    override fun copy(): Part {
        return OpenablePart()
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Open -> partOwner.respondToOpen(action)
            else -> false
        }
    }

    private fun Entity.respondToOpen(action: Open): Boolean {
        val (context, actor) = action

        respondToAction(DropWeapon(context, this))
        respondToAction(DropItem(context, this))

        EventBus.post(OpenEvent(this, actor))
        context.world.remove(this)

        return true
    }
}