package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class Inventory(size: Int) : Part {

    var contents: List<Entity> = listOf()
        private set

    var money: Int = 0
        private set

    fun add(entity: Entity): Boolean {
        // Add as money.
        val moneyValue = entity.find(Money::class)?.value
        if (moneyValue != null) {
            money += moneyValue
            return true
        }

        // Add as item.
        if (entity.has(Item::class)) {
            contents = contents + listOf(entity)
            return true
        }

        return false
    }

    fun take(entity: Entity): Entity? {
        if (contents.contains(entity)) {
            contents = contents.filter { it != entity }
            return entity
        }

        return null
    }
}