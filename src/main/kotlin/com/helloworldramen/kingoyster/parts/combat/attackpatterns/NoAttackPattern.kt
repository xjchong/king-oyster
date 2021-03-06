package com.helloworldramen.kingoyster.parts.combat.attackpatterns

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo

class NoAttackPattern : AttackPattern() {

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        return mapOf()
    }

    override fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        return listOf()
    }

    override fun hitPositions(context: Context, entity: Entity, direction: Direction): List<Position> {
        return listOf()
    }

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        return false
    }
}