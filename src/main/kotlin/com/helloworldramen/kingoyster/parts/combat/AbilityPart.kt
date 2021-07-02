package com.helloworldramen.kingoyster.parts.combat

import com.helloworldramen.kingoyster.actions.UseAbility
import com.helloworldramen.kingoyster.architecture.Action
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part
import com.helloworldramen.kingoyster.parts.combat.abilities.Ability

class AbilityPart(
    val ability: Ability
) : Part {

    override fun copy(): Part {
        return AbilityPart(ability)
    }

    override fun respondToAction(partOwner: Entity, action: Action): Boolean {
        return when (action) {
            is UseAbility -> partOwner.respondToUseAbility(action)
            else -> false
        }
    }

    private fun Entity.respondToUseAbility(action: UseAbility): Boolean {
        val (context, user) = action

        ability.execute(context, this, user)

        return false
    }
}

fun Entity.ability(): Ability? = find<AbilityPart>()?.ability
