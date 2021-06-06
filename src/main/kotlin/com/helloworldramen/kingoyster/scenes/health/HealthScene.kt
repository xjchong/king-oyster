package com.helloworldramen.kingoyster.scenes.health

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.combat.maxHealth
import godot.Node2D
import godot.TextureProgress
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.extensions.getNodeAs

@RegisterClass
class HealthScene : Node2D() {

	private val healthBarFade: TextureProgress by lazy { getNodeAs("HealthBarFade")!! }
	private val healthBarFill: TextureProgress by lazy { getNodeAs("HealthBarFill")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }

	private var entity: Entity = Entity.UNKNOWN
	private var maxHealth: Double = 0.0
	private var lastHealth: Int = 0
	private var shouldShowWhenFull = false

	@RegisterFunction
	override fun _process(delta: Double) {
		if (!visible) return

		updateHealth()
	}

	fun bind(entity: Entity, shouldShowWhenFull: Boolean = false) {
		this.entity = entity
		this.shouldShowWhenFull = shouldShowWhenFull

		visible = entity.maxHealth() > 0
		maxHealth = entity.maxHealth().toDouble()
		lastHealth = -1

		updateHealth(false)
	}

	private fun updateHealth(shouldAnimate: Boolean = true) {
		val currentHealth = entity.health()

		if (currentHealth == lastHealth) return

		val nextValue = (currentHealth / maxHealth.coerceAtLeast(1.0)) * 100

		healthBarFade.visible = shouldShowWhenFull || currentHealth != maxHealth.toInt()
		healthBarFill.visible = shouldShowWhenFull || currentHealth != maxHealth.toInt()
		healthBarFill.value = nextValue
		lastHealth = currentHealth

		if (shouldAnimate) {
			tween.interpolateProperty(healthBarFade, NodePath("value"),
				initialVal = healthBarFade.value,
				finalVal = nextValue,
				duration = 0.2, transType = Tween.TRANS_SINE, easeType = Tween.EASE_IN
			)
			tween.start()
		} else {
			healthBarFade.value = nextValue
		}
	}
}
