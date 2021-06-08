package com.helloworldramen.kingoyster.parts.combat.attacks

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.*
import com.helloworldramen.kingoyster.parts.isEnemyOf
import com.helloworldramen.kingoyster.parts.isPassable

class RapierAttackPattern(
    private val powerFactor: Double,
    private val damageType: DamageType = DamageType.Stab,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val nextPosition = currentPosition.withRelative(direction.vector)

        // The square in front of the user must be passable or attackable.
        if (context.entitiesAt(nextPosition)?.all { it.isPassable() || (it.has<CombatPart>() && it.isEnemyOf(entity)) } != true) {
            return false
        }

        return getHitPositions(context, entity, direction).any { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }
    }

    override fun followupPath(context: Context, entity: Entity, direction: Direction): List<Position> {
        // Move to the square next to the user.
        val currentPosition = context.positionOf(entity) ?: return listOf()
        val nextPosition = currentPosition + direction.vector

        return if (context.entitiesAt(nextPosition)?.all { it.isPassable() } == true) {
            listOf(nextPosition)
        } else {
            listOf()
        }
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        return getHitPositions(context, entity, direction).associateWith {
            DamageInfo(powerFactor, damageType, elementType)
        }
    }

    private fun getHitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()
        val hitPosition = listOf(
            currentPosition.withRelative(direction.vector),
            currentPosition.withRelative(direction.vector * 2)).firstOrNull { position ->
            context.entitiesAt(position)?.any { it.isEnemyOf(entity) } == true
        }

        return if (hitPosition != null) {
            listOf(hitPosition)
        } else {
            listOf()
        }
    }
}