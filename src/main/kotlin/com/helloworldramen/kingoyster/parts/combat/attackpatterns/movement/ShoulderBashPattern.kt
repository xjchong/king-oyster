package com.helloworldramen.kingoyster.parts.combat.attackpatterns.movement

import com.helloworldramen.kingoyster.actions.Damage
import com.helloworldramen.kingoyster.actions.Move
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.AttackPattern
import com.helloworldramen.kingoyster.parts.isBarrier
import com.helloworldramen.kingoyster.parts.isPassable

class ShoulderBashPattern(
    private val powerFactor: Double,
    private val damageType: DamageType = DamageType.Bash,
    private val elementType: ElementType = ElementType.None
) : AttackPattern() {

    override fun hitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        val endPosition = calculateEndPosition(context, entity, direction) ?: return listOf()

        return endPosition.neighbors().filter { it != endPosition - direction.vector }
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        if (!isUsable(context, entity, direction)) return listOf()

        val currentPosition = context.positionOf(entity) ?: return listOf()

        val guaranteedPath = context.straightPathWhile(currentPosition + (direction.vector * 2), direction) { position ->
            context.entitiesAt(position)?.all { !it.isBarrier() } == true
        }

        val lastPosition = guaranteedPath.lastOrNull() ?: return listOf()

        return guaranteedPath + (lastPosition.neighbors().filter {
            it != lastPosition - direction.vector
        })
    }

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val adjacentPosition = currentPosition + direction.vector
        val adjacentEntities = context.entitiesAt(adjacentPosition) ?: return false

        return adjacentEntities.all { it.isPassable() }
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        return hitPositions(context, entity, direction)
            .associateWith { DamageInfo(powerFactor, damageType, elementType) }
    }

    override fun beforeEffect(context: Context, entity: Entity, direction: Direction) {
        val endPosition = calculateEndPosition(context, entity, direction) ?: return

        entity.respondToAction(Move(context, entity, endPosition, timeFactor = 0.0))
    }

    override fun afterEffect(context: Context, entity: Entity, direction: Direction) {
        entity.respondToAction(Damage(context, Entity.UNKNOWN, 1, DamageType.Bash))
    }

    private fun calculateEndPosition(context: Context, entity: Entity, direction: Direction): Position? {
        return calculatePath(context, entity, direction).lastOrNull()
    }

    private fun calculatePath(context: Context, entity: Entity, direction: Direction): List<Position> {
        val currentPosition = context.positionOf(entity) ?: return listOf()

        return context.straightPathWhile(currentPosition, direction) { position ->
            val entities = context.entitiesAt(position)

            entities != null && (entities.all { it.isPassable() } || entities.contains(entity))
        }
    }
}