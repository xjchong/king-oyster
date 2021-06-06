package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.AttackPattern
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.DamageInfo
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.attacks.BasicAttackPattern

object ActorFactory {

    fun player() = Entity(
        name = "player",
        parts = listOf(
            CombatPart(
                maxHealth = 100,
                maxMana = 4,
                power = 10,
                defaultAttackPattern = BasicAttackPattern(
                    powerFactor = 0.5,
                    damageType = DamageType.Bash
                )
            ),
            EquipmentPart(),
            FactionPart(Faction.Player,
                enemies = setOf(
                    Faction.Goblin,
                    Faction.Monster,
                    Faction.Spirit
                )
            ),
            InventoryPart(6),
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

    fun ghost() = Entity(
        name = "ghost",
        parts = listOf(
            CombatPart(
                maxHealth = 10,
                maxMana = 6,
                power = 0,
                defaultAttackPattern = BasicAttackPattern(0.0)
            ),
            FactionPart(Faction.Spirit),
            PhysicalPart(
                isPassable = true,
                isCorporeal = false
            ),
            MovementPart(),
            SensoryPart(
                visionRange = 4
            )
        ),
        timeFactor = 1.0
    )

    fun goblin() = Entity(
        name = "goblin",
        parts = listOf(
            CombatPart(
                maxHealth = 30,
                maxMana = 0,
                power = 5,
                defaultAttackPattern = BasicAttackPattern(
                    powerFactor = 1.0,
                    damageType = DamageType.Bash
                )
            ),
            EquipmentPart(),
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

    fun slime() = Entity(
        name = "slime",
        parts = listOf(
            CombatPart(
                maxHealth = 20,
                maxMana = 0,
                power = 2,
                defaultAttackPattern = BasicAttackPattern(1.0)
            ),
            FactionPart(Faction.Monster,
                enemies = setOf(Faction.Player, Faction.Goblin)
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