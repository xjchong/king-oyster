package com.helloworldramen.kingoyster.parts.combat.attacks

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.combat.AttackInfo
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.defaultDamageInfo
import com.helloworldramen.kingoyster.parts.equippedWeaponPart
import com.helloworldramen.kingoyster.parts.isEnemyOf

class BasicAttackInfo : AttackInfo() {

    override fun isUsable(context: Context, entity: Entity, direction: Direction): Boolean {
        val currentPosition = context.positionOf(entity) ?: return false
        val nextPosition = currentPosition.withRelative(direction.vector)

        return (context.entitiesAt(nextPosition)?.any { it.isEnemyOf(entity) } == true )
    }

    override fun calculateDamageForPosition(
        context: Context,
        entity: Entity,
        direction: Direction
    ): Map<Position, DamageInfo> {
        val currentPosition = context.positionOf(entity) ?: return mapOf()
        val equippedWeaponPart = entity.equippedWeaponPart()
        val damageInfo = equippedWeaponPart?.damageInfo ?: entity.defaultDamageInfo()

        return mapOf(currentPosition to damageInfo)
    }
}