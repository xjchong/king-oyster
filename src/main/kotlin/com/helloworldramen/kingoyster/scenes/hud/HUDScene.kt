package com.helloworldramen.kingoyster.scenes.hud

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.DamageEvent
import com.helloworldramen.kingoyster.eventbus.events.DamageWeaponEvent
import com.helloworldramen.kingoyster.eventbus.events.EquipWeaponEvent
import com.helloworldramen.kingoyster.eventbus.events.ThrowWeaponEvent
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.durability
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

	private var entity: Entity = Entity.UNKNOWN

	@RegisterFunction
	override fun _process(delta: Double) {
		updateHealth()
		updateWeapon()
	}

	fun bind(entity: Entity) {
		this.entity = entity

		healthScene.bind(entity, true)
	}

	private fun updateHealth() {
		healthAmountLabel.text = entity.health().toString()
	}

	private fun updateWeapon() {
		// Update weapon display.
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

	companion object {
		private const val HEALTH_PREFIX = "VBoxContainer/HealthVBox"
		private const val WEAPON_PREFIX = "VBoxContainer/WeaponContainer/WeaponHBox"
	}
}
