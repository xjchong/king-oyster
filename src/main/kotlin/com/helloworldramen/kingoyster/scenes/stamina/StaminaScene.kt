package com.helloworldramen.kingoyster.scenes.stamina

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.combat.maxStamina
import com.helloworldramen.kingoyster.parts.combat.stamina
import godot.Node2D
import godot.TextureProgress
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.extensions.getNodeAs

@RegisterClass
class StaminaScene : Node2D() {

	private val staminaBarFade: TextureProgress by lazy { getNodeAs("StaminaBarFade")!! }
	private val staminaBarFill: TextureProgress by lazy { getNodeAs("StaminaBarFill")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }

	private var entity: Entity = Entity.UNKNOWN
	private var maxStamina: Double = 0.0
	private var lastStamina: Int = 0
	private var shouldShowWhenFull = false

	@RegisterFunction
	override fun _process(delta: Double) {
		if (!visible) return

		updateStamina()
	}

	fun bind(entity: Entity, shouldShowWhenFull: Boolean = false) {
		this.entity = entity
		this.shouldShowWhenFull = shouldShowWhenFull

		visible = entity.maxStamina() > 0
		maxStamina = entity.maxStamina().toDouble()
		lastStamina = -1

		updateStamina(false)
	}

	private fun updateStamina(shouldAnimate: Boolean = true) {
		val currentStamina = entity.stamina()

		if (currentStamina == lastStamina) return

		val nextValue = (currentStamina / maxStamina.coerceAtLeast(1.0)) * 100

		staminaBarFade.visible = shouldShowWhenFull || currentStamina != maxStamina.toInt()
		staminaBarFill.visible = shouldShowWhenFull || currentStamina != maxStamina.toInt()
		staminaBarFill.value = nextValue
		lastStamina = currentStamina

		if (shouldAnimate) {
			tween.interpolateProperty(staminaBarFade, NodePath("value"),
				initialVal = staminaBarFade.value,
				finalVal = nextValue,
				duration = 0.2, transType = Tween.TRANS_SINE, easeType = Tween.EASE_IN
			)
			tween.start()
		} else {
			staminaBarFade.value = nextValue
		}
	}
}
