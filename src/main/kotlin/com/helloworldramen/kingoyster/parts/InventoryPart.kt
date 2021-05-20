package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Part

class InventoryPart(val capacity: Int) : Part {

    var contents: List<Entity> = listOf()
        private set

    var money: Int = 0
        private set

    private constructor(capacity: Int, contents: List<Entity>, money: Int): this(capacity) {
        this.contents = contents
        this.money = money
    }

    override fun copy(): Part {
        return InventoryPart(capacity, contents, money)
    }

    fun put(entity: Entity): Boolean {
        // Add as money.
        val moneyValue = entity.find(MoneyPart::class)?.value
        if (moneyValue != null) {
            money += moneyValue
            return true
        }

        // Add as item.
        if (contents.size < capacity && entity.has(ItemPart::class)) {
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