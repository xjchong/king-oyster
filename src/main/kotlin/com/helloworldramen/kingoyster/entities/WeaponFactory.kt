package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.attacks.BasicAttackPattern
import com.helloworldramen.kingoyster.parts.combat.attacks.DaggerAttackPattern
import com.helloworldramen.kingoyster.parts.combat.attacks.GreatswordAttackPattern
import com.helloworldramen.kingoyster.parts.combat.attacks.LongswordAttackPattern
import godot.core.Color

object WeaponFactory {

    fun newDagger(): EntityFactoryFn = {
        Entity(
            name = "dagger",
            parts = listOf(
                AppearancePart(
                    ascii = '|',
                    color = Color.gray,
                    sprite = "dagger"
                ),
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
    }

    fun newLongsword(): EntityFactoryFn = {
        Entity(
            name = "longsword",
            parts = listOf(
                AppearancePart(
                    ascii = '|',
                    color = Color.white,
                    sprite = "longsword"
                ),
                WeaponPart(
                    attackPattern = LongswordAttackPattern(1.5),
                    throwInfo = DamageInfo(
                        powerFactor = 3.0,
                        damageType = DamageType.Cut,
                    ),
                    maxDurability = 9
                )
            )
        )
    }

    fun newGreatsword(): EntityFactoryFn = {
        Entity(
            name = "greatsword",
            parts = listOf(
                AppearancePart(
                    ascii = '|',
                    color = Color.lightblue,
                    sprite = "greatsword"
                ),
                WeaponPart(
                    attackPattern = GreatswordAttackPattern(1.5),
                    throwInfo = DamageInfo(
                        powerFactor = 3.0,
                        damageType = DamageType.Cut,
                    ),
                    maxDurability = 8
                )
            )
        )
    }
}