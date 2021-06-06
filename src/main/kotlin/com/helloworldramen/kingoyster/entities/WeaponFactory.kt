package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.attacks.BasicAttackPattern
import com.helloworldramen.kingoyster.parts.combat.attacks.DaggerAttackPattern

object WeaponFactory {

    fun newDagger() = Entity(
        name = "dagger",
        parts = listOf(
            WeaponPart(
                attackPattern = DaggerAttackPattern(1.0),
                throwInfo = DamageInfo(
                    powerFactor = 6.0,
                    damageType = DamageType.Stab,
                ),
                maxDurability = 10
            )
        )
    )

    fun newSword() = Entity(
        name = "sword",
        parts = listOf(
            WeaponPart(
                attackPattern = DaggerAttackPattern(1.5),
                throwInfo = DamageInfo(
                    powerFactor = 3.0,
                    damageType = DamageType.Cut,
                ),
                maxDurability = 9
            )
        )
    )

    fun newGreatsword() = Entity(
        name = "greatsword",
        parts = listOf(
            WeaponPart(
                attackPattern = DaggerAttackPattern(3.0),
                throwInfo = DamageInfo(
                    powerFactor = 6.0,
                    damageType = DamageType.Cut,
                ),
                maxDurability = 8
            )
        )
    )
}