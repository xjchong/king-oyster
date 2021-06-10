package com.helloworldramen.kingoyster.parts.combat.statuseffects

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType

class BurnStatusEffect(val potency: Int, override var turnsRemaining: Int) : StatusEffect() {

    override fun onTick(context: Context, owner: Entity) {
        owner.respondToAction(Damage(context, owner, potency, DamageType.Special, ElementType.Fire))
    }
}