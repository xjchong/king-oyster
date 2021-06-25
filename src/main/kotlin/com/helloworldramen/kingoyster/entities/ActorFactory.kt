package com.helloworldramen.kingoyster.entities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.CombatPart
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.ResistancesPart
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.BasicAttackPattern
import com.helloworldramen.kingoyster.parts.combat.statuseffects.PoisonStatusEffect
import com.helloworldramen.kingoyster.parts.MovementPart
import com.helloworldramen.kingoyster.parts.combat.attackpatterns.movement.ShoulderBashPattern
import godot.core.Color
import godot.core.Vector2
import kotlin.random.Random

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
                    maxStamina = 1000,
                    power = 10,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 0.5,
                        damageType = DamageType.Bash
                    )
                ),
                ItemSlotPart(
                ),
                WeaponSlotPart(
                    weapon = WeaponFactory.dagger()()
                ),
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
                MovementPart(
                    attackPattern = ShoulderBashPattern(
                        powerFactor = 1.5,
                        staminaCost = 100
                    )
                ),
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
                    maxStamina = 4,
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

    fun boar(): EntityFactoryFn = {
        Entity(
            name = "boar",
            parts = listOf(
                AppearancePart(
                    ascii = 'B',
                    color = Color.brown,
                    sprite = "boar",
                ),
                CombatPart(
                    maxHealth = 30,
                    maxStamina = 0,
                    power = 12,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 1.0,
                    )
                ),
                FactionPart(
                    Faction.Monster,
                    enemies = setOf(Faction.Player)
                ),
                PhysicalPart(
                    isPassable = false
                ),
                MovementPart(
                    attackPattern = ShoulderBashPattern(
                        powerFactor = 2.0,
                        staminaCost = 0
                    )
                ),
                SensoryPart(
                    visionRange = 5
                ),
                TelegraphPart()
            ),
            timeFactor = 1.0
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
                    maxStamina = 6,
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
                    resistanceForDamageType = mapOf(
                        DamageType.Cut to 0.0,
                        DamageType.Bash to 0.0,
                        DamageType.Stab to 0.0,
                        DamageType.Magic to 1.5,
                    ),
                    resistanceForElementType = mapOf(
                        ElementType.Fire to 1.5,
                        ElementType.Ice to 1.5,
                        ElementType.Volt to 1.5,
                        ElementType.Poison to 0.0
                    )
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
                    maxStamina = 0,
                    power = 8,
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
                        10 to WeaponFactory.spear(),
                        8 to WeaponFactory.longsword(),
                        5 to WeaponFactory.dagger(),
                        3 to WeaponFactory.greatsword(),
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
                    visionRange = 7,
                    canHavePlayerSense = true
                ),
                TelegraphPart()
            ),
            timeFactor = 1.0
        )
    }

    fun hobgoblin(): EntityFactoryFn = {
        Entity(
            name = "hobgoblin",
            parts = listOf(
                AppearancePart(
                    ascii = 'g',
                    color = Color.purple,
                    sprite = "hobgoblin",
                    offset = Vector2(0, -4)
                ),
                CombatPart(
                    maxHealth = 80,
                    maxStamina = 0,
                    power = 12,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 1.0,
                        damageType = DamageType.Bash
                    )
                ),
                ItemSlotPart(
                    EntityTable(
                        30 to ItemFactory.medicine(),
                        70 to EntityTable.NULL
                    ).generate()
                ),
                WeaponSlotPart(
                    EntityTable(
                        30 to WeaponFactory.longsword(),
                        30 to WeaponFactory.greatsword(),
                        30 to WeaponFactory.spear(),
                        15 to WeaponFactory.dagger(),
                        25 to EntityTable.NULL
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
                    visionRange = 7,
                    canHavePlayerSense = true
                ),
                TelegraphPart()
            ),
            timeFactor = 1.0
        )
    }

    fun blueSlime(): EntityFactoryFn = {
        Entity(
            name = "blue slime",
            parts = listOf(
                AppearancePart(
                    ascii = 's',
                    color = Color.lightblue,
                    sprite = "blue_slime",
                    offset = Vector2(0, -2)
                ),
                CombatPart(
                    maxHealth = 15,
                    maxStamina = 0,
                    power = 4,
                    defaultAttackPattern = BasicAttackPattern(1.0)
                ),
                FactionPart(
                    Faction.Monster,
                    enemies = setOf(Faction.Player, Faction.Goblin)
                ),
                ItemSlotPart(
                    EntityTable(
                        30 to FeatureFactory.healingPuddle(),
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

    fun giantRat(): EntityFactoryFn = {
        Entity(
            name = "giant rat",
            parts = listOf(
                AppearancePart(
                    ascii = 'r',
                    color = Color.white,
                    sprite = "giant_rat"
                ),
                BreederPart(
                    maxChildCount = 3,
                    entityFactoryFn = giantRat()
                ),
                CombatPart(
                    maxHealth = 24,
                    maxStamina =  0,
                    power = 6,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 1.0,
                        damageType = DamageType.Stab,
                        statusEffect = PoisonStatusEffect(1, 0.7, Random.nextInt(10, 25))
                    )
                ),
                FactionPart(
                    Faction.Monster,
                    enemies = setOf(Faction.Player)
                ),
                ItemSlotPart(),
                MovementPart(),
                PhysicalPart(
                    isPassable = false
                ),
                SensoryPart(
                    visionRange = 5,
                    canHavePlayerSense = true
                ),
                TelegraphPart()
            ),
            timeFactor = 1.0
        )
    }

    fun redSlime(): EntityFactoryFn = {
        Entity(
            name = "red slime",
            parts = listOf(
                AppearancePart(
                    ascii = 's',
                    color = Color.red,
                    sprite = "red_slime",
                    offset = Vector2(0, -2)
                ),
                CombatPart(
                    maxHealth = 15,
                    maxStamina = 0,
                    power = 6,
                    defaultAttackPattern = BasicAttackPattern(
                        powerFactor = 1.0,
                        elementType = ElementType.Fire
                    )
                ),
                FactionPart(
                    Faction.Monster,
                    enemies = setOf(Faction.Player, Faction.Goblin)
                ),
                ItemSlotPart(
                    EntityTable(
                        30 to FeatureFactory.firePuddle(),
                        70 to EntityTable.NULL
                    ).generate()
                ),
                MovementPart(),
                PhysicalPart(
                    isPassable = false
                ),
                ResistancesPart(
                    resistanceForElementType = mapOf(
                        ElementType.Fire to 0.0
                    )
                ),
                SensoryPart(
                    visionRange = 4
                ),
                TelegraphPart()
            ),
            timeFactor = 1.0
        )
    }
}