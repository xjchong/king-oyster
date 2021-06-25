package com.helloworldramen.kingoyster.scenes.hud

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.combat.stamina
import com.helloworldramen.kingoyster.scenes.entity.EntitySprite
import com.helloworldramen.kingoyster.scenes.health.HealthScene
import com.helloworldramen.kingoyster.scenes.stamina.StaminaScene
import godot.Label
import godot.MarginContainer
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class HUDScene : MarginContainer() {

	private val healthScene: HealthScene by lazy { getNodeAs("$HEALTH_PREFIX/HealthPanel/HealthScene")!! }
	private val healthAmountLabel: Label by lazy { getNodeAs("$HEALTH_PREFIX/HealthLabelsContainer/HealthLabelsHBox/HealthAmountLabel")!! }

	private val staminaScene: StaminaScene by lazy { getNodeAs("$STAMINA_PREFIX/StaminaPanel/StaminaScene")!! }
	private val staminaAmountLabel: Label by lazy { getNodeAs("$STAMINA_PREFIX/StaminaLabelsContainer/StaminaLabelsHBox/StaminaAmountLabel")!! }

	private val weaponSprite: EntitySprite by lazy { getNodeAs("$WEAPON_PREFIX/WeaponHBox/WeaponCenterContainer/WeaponPanel/WeaponSprite")!! }
	private val weaponDurabilityLabel: Label by lazy { getNodeAs("$WEAPON_PREFIX/WeaponHBox/WeaponDurabilityLabel")!! }
	private val weaponNameLabel: Label by lazy { getNodeAs("$WEAPON_PREFIX/WeaponHBox/WeaponNameLabel")!! }
	private val weaponDescriptionLabel: Label by lazy { getNodeAs("$WEAPON_PREFIX/WeaponDescriptionLabel")!! }

	private val itemSprite: EntitySprite by lazy { getNodeAs("$ITEM_PREFIX/ItemHBox/ItemCenterContainer/ItemPanel/ItemSprite")!! }
	private val itemUsesLabel: Label by lazy { getNodeAs("$ITEM_PREFIX/ItemHBox/ItemUsesLabel")!! }
	private val itemNameLabel: Label by lazy { getNodeAs("$ITEM_PREFIX/ItemHBox/ItemNameLabel")!! }
	private val itemDescriptionLabel: Label by lazy { getNodeAs("$ITEM_PREFIX/ItemDescriptionLabel")!! }

	private var entity: Entity = Entity.UNKNOWN

	@RegisterFunction
	override fun _process(delta: Double) {
		updateHealth()
		updateStamina()
		updateWeapon()
		updateItem()
	}

	fun bind(entity: Entity) {
		this.entity = entity

		healthScene.bind(entity, true)
		staminaScene.bind(entity, true)
	}

	private fun updateHealth() {
		healthAmountLabel.text = entity.health().toString()
	}

	private fun updateStamina() {
		staminaAmountLabel.text = entity.stamina().toString()
	}

	private fun updateWeapon() {
		val weapon = entity.weapon()

		if (weapon == null) {
			weaponDurabilityLabel.text = ""
			weaponNameLabel.text = "(no weapon)"
			weaponDescriptionLabel.text = ""
			weaponSprite.hide()
		} else {
			weaponDurabilityLabel.text = "(${weapon.durability()})"
			weaponNameLabel.text = weapon.name.capitalize()
			weaponDescriptionLabel.text = weapon.description()
			weaponSprite.bind(weapon)
			weaponSprite.show()
		}
	}
	
	private fun updateItem() {
		val item = entity.item()

		if (item == null) {
			itemUsesLabel.text = ""
			itemNameLabel.text = "(no item)"
			itemDescriptionLabel.text = ""
			itemSprite.hide()
		} else {
			itemUsesLabel.text = "(${item.itemUses()})"
			itemNameLabel.text = item.name.capitalize()
			itemDescriptionLabel.text = item.description()
			itemSprite.bind(item)
			itemSprite.show()
		}
	}

	companion object {
		private const val HEALTH_PREFIX = "VBoxContainer/HealthVBox"
		private const val STAMINA_PREFIX = "VBoxContainer/StaminaVBox"
		private const val WEAPON_PREFIX = "VBoxContainer/WeaponContainer/WeaponVBox"
		private const val ITEM_PREFIX = "VBoxContainer/ItemContainer/ItemVBox"
	}
}
