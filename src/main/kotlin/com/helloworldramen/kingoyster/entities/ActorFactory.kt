package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ResistancesPart
import com.helloworldramen.kingoyster.parts.combat.attacks.BasicAttackPattern
import godot.core.Color
import godot.core.Vector2

object ActorFactory {

    fun player(): EntityFactoryFn = {
        Entity(
            name = "player",
            parts = listOf(
                AppearancePart(
                    ascii = '@',
                    color = Color.gold,
                    sprite = "knight"
                ),
                CombatPart(
                    maxHealth = 100,
                    maxMana = 4,
                    power = 10,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 0.5,
                        damageType = DamageType.Bash
                    )
                ),
                ItemSlotPart(),
                WeaponSlotPart(),
                FactionPart(Faction.Player,
                    enemies = setOf(
                        Faction.Goblin,
                        Faction.Monster,
                        Faction.Spirit
                    )
                ),
                PhysicalPart(
                    isPassable = false
                ),
                MemoryPart(),
                MovementPart(),
                SensoryPart(
                    visionRange = 10
                )
            ),
            timeFactor = 1.0,
            isPlayer = true
        )
    }

    fun debug(): EntityFactoryFn = {
        Entity(
            name = "player",
            parts = listOf(
                AppearancePart(
                    ascii = '@',
                    color = Color.gold,
                    sprite = "knight"
                ),
                CombatPart(
                    maxHealth = 100,
                    maxMana = 4,
                    power = 10,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 0.5,
                        damageType = DamageType.Bash
                    )
                ),
                ItemSlotPart(),
                WeaponSlotPart(),
                FactionPart(Faction.None,
                    enemies = setOf(
                        Faction.Goblin,
                        Faction.Monster,
                        Faction.Spirit
                    )
                ),
                PhysicalPart(
                    isPassable = false,
                    isCorporeal = false
                ),
                MemoryPart(),
                MovementPart(),
                SensoryPart(
                    visionRange = 10
                )
            ),
            timeFactor = 1.0,
            isPlayer = true
        )
    }

    fun ghost(): EntityFactoryFn = {
        Entity(
            name = "ghost",
            parts = listOf(
                AppearancePart(
                    ascii = 'G',
                    color = Color.darkblue,
                    sprite = "ghost"
                ),
                CombatPart(
                    maxHealth = 10,
                    maxMana = 6,
                    power = 0,
                    defaultAttackPattern = BasicAttackPattern(0.0)
                ),
                FactionPart(
                    Faction.Spirit,
                    enemies = setOf(Faction.Goblin, Faction.Player)
                ),
                PhysicalPart(
                    isPassable = true,
                    isCorporeal = false
                ),
                MovementPart(),
                ResistancesPart(
                    cutResFactor = 0.0,
                    bashResFactor = 0.0,
                    stabResFactor = 0.0,
                    magicResFactor = 1.5,
                    fireResFactor = 1.5,
                    iceResFactor = 1.5,
                    voltResFactor = 1.5
                ),
                SensoryPart(
                    visionRange = 4
                )
            ),
            timeFactor = 1.0
        )
    }

    fun goblin(): EntityFactoryFn = {
        Entity(
            name = "goblin",
            parts = listOf(
                AppearancePart(
                    ascii = 'g',
                    color = Color.darkred,
                    sprite = "goblin"
                ),
                CombatPart(
                    maxHealth = 30,
                    maxMana = 0,
                    power = 5,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 1.0,
                        damageType = DamageType.Bash
                    )
                ),
                ItemSlotPart(
                    EntityTable(
                        10 to ItemFactory.medicine(),
                        90 to EntityTable.NULL
                    ).generate()
                ),
                WeaponSlotPart(
                    EntityTable(
                        5 to WeaponFactory.newDagger(),
                        4 to WeaponFactory.newLongsword(),
                        3 to WeaponFactory.newGreatsword(),
                        88 to EntityTable.NULL
                    ).generate()
                ),
                FactionPart(Faction.Goblin,
                    enemies = setOf(Faction.Player, Faction.Spirit)
                ),
                PhysicalPart(
                    isPassable = false
                ),
                MovementPart(),
                SensoryPart(
                    visionRange = 7
                ),
                TelegraphPart()
            ),
            timeFactor = 1.0
        )
    }

    fun blueSlime(): EntityFactoryFn = slime("blue")
    fun redSlime(): EntityFactoryFn = slime("red")

    private fun slime(color: String): EntityFactoryFn = {
        val name = when (color) {
            "red" -> "red slime"
            else -> "blue slime"
        }

        val asciiColor = when (color) {
            "red" -> Color.red
            else -> Color.lightgreen
        }

        val sprite = when (color) {
            "red" -> "red_slime"
            else -> "blue_slime"
        }

        val puddle = when(color) {
            "red" -> FeatureFactory.firePuddle()
            else -> FeatureFactory.healingPuddle()
        }

        Entity(
            name = name,
            parts = listOf(
                AppearancePart(
                    ascii = 's',
                    color = asciiColor,
                    sprite = sprite,
                    offset = Vector2(0, -2)
                ),
                CombatPart(
                    maxHealth = 20,
                    maxMana = 0,
                    power = 2,
                    defaultAttackPattern = BasicAttackPattern(1.0)
                ),
                FactionPart(
                    Faction.Monster,
                    enemies = setOf(Faction.Player, Faction.Goblin)
                ),
                ItemSlotPart(
                    EntityTable(
                        30 to puddle,
                        70 to EntityTable.NULL
                    ).generate()
                ),
                PhysicalPart(
                    isPassable = false
                ),
                MovementPart(),
                SensoryPart(
                    visionRange = 4
                ),
                TelegraphPart()
            ),
            timeFactor = 1.0
        )
    }
}