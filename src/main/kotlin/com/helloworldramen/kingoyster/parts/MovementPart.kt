package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class MovementPart : Part {

    override fun copy(): Part {
        return MovementPart()
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Move -> respondToMove(partOwner, action)
            else -> false
        }
    }

    private fun respondToMove(partOwner: Entity, action: Move): Boolean {
        val (context, _, position) = action

        if (context.entitiesAt(position)?.any { !partOwner.canPass(it)} == true) {
            return false
        }

        return context.world.move(partOwner, position)
    }
}