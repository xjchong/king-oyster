package com.helloworldramen.kingoyster.extensions

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.architecture.World

fun World.positionsForEach(block: (Position) -> Unit) {
   Position(width - 1, height - 1).forEach(block)
}

fun World.randomPositionWhere(predicate: (Position, List<Entity>) -> Boolean): Position? {
   return Position(width - 1, height - 1)
      .map { it }
      .shuffled()
      .firstOrNull { position ->
         val entities = get(position)

         entities != null && predicate(position, entities)
      }
}