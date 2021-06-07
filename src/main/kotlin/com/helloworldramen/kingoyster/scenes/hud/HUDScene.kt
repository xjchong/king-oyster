package com.helloworldramen.kingoyster.scenes.hud

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.durability
import com.helloworldramen.kingoyster.parts.item
import com.helloworldramen.kingoyster.parts.uses
import com.helloworldramen.kingoyster.parts.weapon
import com.helloworldramen.kingoyster.scenes.entity.EntitySprite
import com.helloworldramen.kingoyster.scenes.health.HealthScene
import godot.Label
import godot.MarginContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class HUDScene : MarginContainer() {

	private val healthScene: HealthScene by lazy { getNodeAs("$HEALTH_PREFIX/HealthPanel/HealthScene")!! }
	private val healthAmountLabel: Label by lazy { getNodeAs("$HEALTH_PREFIX/HealthLabelsContainer/HealthLabelsHBox/HealthAmountLabel")!! }

	private val weaponSprite: EntitySprite by lazy { getNodeAs("$WEAPON_PREFIX/WeaponPanel/WeaponSprite")!! }
	private val weaponDurabilityLabel: Label by lazy { getNodeAs("$WEAPON_PREFIX/WeaponDurabilityLabel")!! }
	private val weaponNameLabel: Label by lazy { getNodeAs("$WEAPON_PREFIX/WeaponNameLabel")!! }

	private val itemSprite: EntitySprite by lazy { getNodeAs("$ITEM_PREFIX/ItemPanel/ItemSprite")!! }
	private val itemUsesLabel: Label by lazy { getNodeAs("$ITEM_PREFIX/ItemUsesLabel")!! }
	private val itemNameLabel: Label by lazy { getNodeAs("$ITEM_PREFIX/ItemNameLabel")!! }

	private var entity: Entity = Entity.UNKNOWN

	@RegisterFunction
	override fun _process(delta: Double) {
		updateHealth()
		updateWeapon()
		updateItem()
	}

	fun bind(entity: Entity) {
		this.entity = entity

		healthScene.bind(entity, true)
	}

	private fun updateHealth() {
		healthAmountLabel.text = entity.health().toString()
	}

	private fun updateWeapon() {
		val weapon = entity.weapon()

		if (weapon == null) {
			weaponDurabilityLabel.text = ""
			weaponNameLabel.text = "(no weapon)"
			weaponSprite.hide()
		} else {
			weaponDurabilityLabel.text = "(${weapon.durability()})"
			weaponNameLabel.text = weapon.name.capitalize()
			weaponSprite.bind(weapon)
			weaponSprite.show()
		}
	}
	
	private fun updateItem() {
		val item = entity.item()

		if (item == null) {
			itemUsesLabel.text = ""
			itemNameLabel.text = "(no item)"
			itemSprite.hide()
		} else {
			itemUsesLabel.text = "(${item.uses()})"
			itemNameLabel.text = item.name.capitalize()
			itemSprite.bind(item)
			itemSprite.show()
		}
	}

	companion object {
		private const val HEALTH_PREFIX = "VBoxContainer/HealthVBox"
		private const val WEAPON_PREFIX = "VBoxContainer/WeaponContainer/WeaponHBox"
		private const val ITEM_PREFIX = "VBoxContainer/ItemContainer/ItemHBox"
	}
}
