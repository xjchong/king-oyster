package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class FactionPart(
    val faction: Faction
) : Part {

    override fun copy(): Part {
        return FactionPart(faction)
    }
}

sealed class Faction {
    object Player : Faction()
    object Monster : Faction()
}

fun Entity.faction(): Faction? = find<FactionPart>()?.faction

fun Entity.isEnemyOf(otherEntity: Entity): Boolean {
    return otherEntity.faction() ?: faction() != faction()
}