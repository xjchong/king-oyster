package com.helloworldramen.kingoyster.parts.combat

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position

abstract class AttackPattern {

    abstract fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean
    abstract fun calculateDamageForPosition(context: Context, entity: Entity, direction: Direction): Map<Position, DamageInfo>
    open fun followupPath(context: Context, entity: Entity, direction: Direction): List<Position> { return listOf() }
    open fun afterEffect(context: Context, entity: Entity, direction: Direction) {}
}