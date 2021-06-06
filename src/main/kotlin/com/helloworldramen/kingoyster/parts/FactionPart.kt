package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class FactionPart(
    val faction: Faction,
    val enemies: Set<Faction> = setOf(),
    val allies: Set<Faction> = setOf(faction)
) : Part {

    override fun copy(): Part {
        return FactionPart(faction, enemies, allies)
    }
}

sealed class Faction {
    object None : Faction()
    object Player : Faction()
    object Goblin : Faction()
    object Monster : Faction()
    object Spirit : Faction()
}

fun Entity.allies(): Set<Faction> = find<FactionPart>()?.allies ?: setOf()
fun Entity.enemies(): Set<Faction> = find<FactionPart>()?.enemies ?: setOf()
fun Entity.faction(): Faction = find<FactionPart>()?.faction ?: Faction.None

fun Entity.isEnemyOf(otherEntity: Entity): Boolean {
    return otherEntity.enemies().contains(faction())
}

fun Entity.isAllyOf(otherEntity: Entity): Boolean {
    return otherEntity.allies().contains(faction())
}