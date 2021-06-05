package com.helloworldramen.kingoyster.utilities.worldgen.population

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World

open class PopulationRule(
     val entityFactory: () -> Entity,
     val predicate: (World, Position, player: Entity) -> Boolean = IS_EMPTY
) {

     companion object {
          val IS_EMPTY: (World, Position, Entity) -> Boolean = { world, position, _ ->
               world[position]?.isEmpty() == true
          }
     }
}