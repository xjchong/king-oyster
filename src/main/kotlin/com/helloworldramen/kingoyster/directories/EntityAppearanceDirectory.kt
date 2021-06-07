package com.helloworldramen.kingoyster.utilities

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.OpenablePart
import godot.core.Color
import godot.core.Vector2

object EntityAppearanceDirectory {

    operator fun get(entity: Entity?): EntityAppearance {
        if (entity == null) return EntityAppearance(' ')

        return when (entity.name) {
            // ACTORS
            "player" -> EntityAppearance(
                ascii = '@',
                color = Color.gold,
                sprite = "knight"
            )
            "blue slime" -> EntityAppearance(
                ascii = 's',
                color = Color.lightgreen,
                sprite = "blue_slime",
                offset = Vector2(0, -2)
            )
            "ghost" -> EntityAppearance(
                ascii = 'G',
                color = Color.darkblue,
                sprite = "ghost"
            )
            "goblin" -> EntityAppearance(
                ascii = 'g',
                color = Color.darkred,
                sprite = "goblin"
            )
            "red slime" -> EntityAppearance(
                ascii = 's',
                color = Color.red,
                sprite = "red_slime",
                offset = Vector2(0, -2)
            )

            // FEATURES
            "door" -> EntityAppearance(
                ascii = '+',
                color = Color.orange
            )
            "healing puddle" -> EntityAppearance(
                ascii = '^',
                color = Color.mediumseagreen
            )
            "fire puddle" -> EntityAppearance(
                ascii = '^',
                color = Color.red
            )
            "stairs" -> EntityAppearance(
                ascii = '<',
                Color.white,
                sprite = "stone_stairs_up"
            )
            "wall" -> EntityAppearance(
                ascii = '#',
                Color.white,
                sprite = "grass_stone_wall",
                offset = Vector2(0, 8)
            )

            // ITEMS
            "medicine" -> EntityAppearance(
                ascii = '!',
                color = Color.mediumseagreen
            )

            // WEAPONS
            "dagger" -> EntityAppearance(
                ascii = '|',
                color = Color.gray,
                sprite = "dagger"
            )
            "greatsword" -> EntityAppearance(
                ascii = '|',
                color = Color.lightblue,
                sprite = "greatsword"
            )
            "longsword" -> EntityAppearance(
                ascii = '|',
                color = Color.white,
                sprite = "longsword"
            )

            // OTHER
            else -> EntityAppearance()
        }
    }
}

data class EntityAppearance(
    val ascii: Char = '?',
    val color: Color = Color.white,
    val sprite: String? = null,
    val offset: Vector2 = Vector2.ZERO,
    val isAnimated: Boolean = sprite != null,
    val frameIndexWeight: WeightedCollection<Int> = WeightedCollection()
)