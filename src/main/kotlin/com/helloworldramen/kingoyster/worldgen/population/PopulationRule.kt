package com.helloworldramen.kingoyster.worldgen.population

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World
import com.helloworldramen.kingoyster.extensions.EntityFactoryFn
import com.helloworldramen.kingoyster.extensions.EntityFactoryFnNullable

open class PopulationRule(
     val entityFactoryFn: EntityFactoryFnNullable,
     val predicate: (World, Position, player: Entity) -> Boolean = IS_EMPTY
) {

     companion object {
          val IS_EMPTY: (World, Position, Entity) -> Boolean = { world, position, _ ->
               world[position]?.isEmpty() == true
          }
     }
}