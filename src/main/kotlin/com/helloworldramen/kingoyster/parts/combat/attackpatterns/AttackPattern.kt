package com.helloworldramen.kingoyster.parts.combat.attackpatterns

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.DamageInfo

abstract class AttackPattern {

    abstract fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean
    abstract fun calculateDamageForPosition(context: Context, entity: Entity, direction: Direction): Map<Position, DamageInfo>
    abstract fun telegraphPositions(context: Context, entity: Entity, direction: Direction): List<Position>
    open fun beforeEffect(context: Context, entity: Entity, direction: Direction) {}
    open fun afterEffect(context: Context, entity: Entity, direction: Direction) {}
}