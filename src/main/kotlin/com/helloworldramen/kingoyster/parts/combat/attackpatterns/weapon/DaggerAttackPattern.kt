package com.helloworldramen.kingoyster.parts.combat.attackpatterns.weapon

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.BasicAttackPattern
import com.helloworldramen.kingoyster.parts.isPassable

class DaggerAttackPattern(
    powerFactor: Double,
    damageType: DamageType = DamageType.Cut,
    elementType: ElementType = ElementType.None
) : BasicAttackPattern(powerFactor, damageType, elementType) {

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        val hitPosition = hitPositions(context, entity, direction).firstOrNull() ?: return mapOf()
        val impassableNeighborsCount = hitPosition.neighbors().filter { position ->
            context.entitiesAt(position)?.any { !it.isPassable() } != false
        }.size

        // Daggers do more damaged in spaces where the target is surrounded by obstacles.
        val crampedFactor = when {
            impassableNeighborsCount >= 4 -> 3.5
            impassableNeighborsCount == 3 -> 2.0
            else -> 0.8
        }

        return mapOf(hitPosition to DamageInfo(powerFactor * crampedFactor, damageType, elementType))
    }
}