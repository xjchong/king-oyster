package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class Movement : Part {

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when(action) {
            is Move -> respondToMove(partOwner, action)
            else -> false
        }
    }

    private fun respondToMove(partOwner: Entity, action: Move): Boolean {
        val (context, position) = action

        if (context.world[position]?.any { it.find(Physical::class)?.isPassable == false} == true) {
            return false
        }

        return context.world.move(partOwner, position)
    }
}