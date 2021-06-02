package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.*

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
                maxDurability = 5
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
                maxDurability = 5
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
                maxDurability = 4
            )
        )
    )
}