package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.weapon.*
import com.helloworldramen.kingoyster.utilities.ColorX11

object WeaponFactory {

    fun dagger(): EntityFactoryFn = {
        Entity(
            name = "dagger",
            parts = listOf(
                AppearancePart(
                    description = "Deals more damage in cramped spaces, and when thrown.",
                    ascii = '|',
                    color = ColorX11.gray,
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
                    description = "Deep attack. Deals more damage for each enemy hit.",
                    ascii = '|',
                    color = ColorX11.white,
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
                    color = ColorX11.lightBlue,
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
                    color = ColorX11.yellow,
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
                    color = ColorX11.purple,
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

    fun spear(): EntityFactoryFn = {
        Entity(
            name = "spear",
            parts = listOf(
                AppearancePart(
                    description = "Long attack. Deals more damage from farther away, and when thrown.",
                    ascii = '/',
                    color = ColorX11.brown,
                    sprite = "weapons",
                    frameIndex = 10
                ),
                WeaponPart(
                    attackPattern = SpearAttackPattern(1.5),
                    throwInfo = DamageInfo(
                        powerFactor = 4.0,
                        damageType = DamageType.Stab
                    ),
                    maxDurability = 8
                )
            )
        )
    }
}