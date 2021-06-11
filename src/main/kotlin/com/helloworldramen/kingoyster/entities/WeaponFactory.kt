package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.*
import godot.core.Color

object WeaponFactory {

    fun dagger(): EntityFactoryFn = {
        Entity(
            name = "dagger",
            parts = listOf(
                AppearancePart(
                    description = "Deals more damage in cramped spaces, and when thrown.",
                    ascii = '|',
                    color = Color.gray,
                    sprite = "dagger"
                ),
                WeaponPart(
                    attackPattern = DaggerAttackPattern(1.5),
                    throwInfo = DamageInfo(
                        powerFactor = 4.0,
                        damageType = DamageType.Stab,
                    ),
                    maxDurability = 10
                )
            )
        )
    }

    fun longsword(): EntityFactoryFn = {
        Entity(
            name = "longsword",
            parts = listOf(
                AppearancePart(
                    description = "Long attack. Deals more damage for each enemy hit.",
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
                    maxDurability = 8
                )
            )
        )
    }

    fun greatsword(): EntityFactoryFn = {
        Entity(
            name = "greatsword",
            parts = listOf(
                AppearancePart(
                    description = "Wide attack. Deals more damage for each enemy hit.",
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

    fun rapier(): EntityFactoryFn = {
        Entity(
            name = "rapier",
            parts = listOf(
                AppearancePart(
                    description = "Lunging attack, lunge deals more damage. Lower thrown damage.",
                    ascii = '|',
                    color = Color.yellow,
                    sprite = "weapons",
                    frameIndex = 3
                ),
                WeaponPart(
                    attackPattern = RapierAttackPattern(1.5),
                    throwInfo = DamageInfo(
                        powerFactor = 2.0,
                        damageType = DamageType.Stab
                    ),
                    maxDurability = 8
                )
            )
        )
    }

    fun scythe(): EntityFactoryFn = {
        Entity(
            name = "scythe",
            parts = listOf(
                AppearancePart(
                    description = "Lunging arc attack. Deals more damage for each enemy hit. Lower thrown damage.",
                    ascii = '7',
                    color = Color.purple,
                    sprite = "weapons",
                    frameIndex = 19
                ),
                WeaponPart(
                    attackPattern = ScytheAttackPattern(1.5),
                    throwInfo = DamageInfo(
                        powerFactor = 2.0,
                        damageType = DamageType.Cut
                    ),
                    maxDurability = 8
                )
            )
        )
    }
}