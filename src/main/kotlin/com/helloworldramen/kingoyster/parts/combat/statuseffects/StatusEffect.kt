package com.helloworldramen.kingoyster.parts.combat.statuseffects

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity

abstract class StatusEffect {

    abstract var turnsRemaining: Int

    open fun onApply(context: Context, owner: Entity) {

    }

    open fun onTick(context: Context, owner: Entity) {

    }

    open fun onExpire(context: Context, owner: Entity) {

    }
}