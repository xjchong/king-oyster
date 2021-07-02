package com.helloworldramen.kingoyster.parts.combat.abilities

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity

abstract class Ability {

    abstract fun isUsable(context: Context, entity: Entity, user: Entity): Boolean
    protected abstract fun effect(context: Context, entity: Entity, user: Entity)

    fun execute(context: Context, entity: Entity, user: Entity): Boolean {
        if (!isUsable(context, entity, user)) return false

        effect(context, entity, user)

        return true
    }
}