package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

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
    object Spirit : Faction()
}

fun Entity.faction(): Faction? = find<FactionPart>()?.faction

fun Entity.isEnemyOf(otherEntity: Entity): Boolean {
    val otherFaction = otherEntity.faction() ?: return false
    val ownFaction = faction() ?: return false

    return otherFaction != ownFaction
}

fun Entity.isAllyOf(otherEntity: Entity): Boolean {
    val otherFaction = otherEntity.faction() ?: return false
    val ownFaction = faction() ?: return false

    return otherFaction == ownFaction
}