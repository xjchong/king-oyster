package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.AttackInfo
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType

object WeaponFactory {

    fun newDagger() = Entity(
        name = "dagger",
        parts = listOf(
            WeaponPart(
                attackInfo = AttackInfo(
                    damageInfo = DamageInfo(
                        powerFactor = 1.1,
                        damageType = DamageType.Cut
                    ),
                    timeFactor = 1.0
                ),
                maxDurability = 10
            )
        )
    )

    fun newSword() = Entity(
        name = "sword",
        parts = listOf(
            WeaponPart(
                attackInfo = AttackInfo(
                    damageInfo = DamageInfo(
                        powerFactor = 1.5,
                        damageType = DamageType.Cut
                    ),
                    timeFactor = 1.2
                ),
                maxDurability = 9
            )
        )
    )

    fun newGreatsword() = Entity(
        name = "greatsword",
        parts = listOf(
            WeaponPart(
                attackInfo = AttackInfo(
                    damageInfo = DamageInfo(
                        powerFactor = 3.0,
                        damageType = DamageType.Cut
                    ),
                    timeFactor = 2.0
                ),
                maxDurability = 8
            )
        )
    )
}