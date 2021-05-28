package com.helloworldramen.kingoyster.scenes.health

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.parts.HealthPart
import com.helloworldramen.kingoyster.parts.health
import godot.Label
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class HealthScene : Node2D() {

	private val label: Label by lazy { getNodeAs("Label")!! }

	private var entity: Entity = Entity.UNKNOWN

	@RegisterFunction
	override fun _process(delta: Double) {
		if (!visible) return

		updateLabel()
	}

	fun bind(entity: Entity) {
		this.entity = entity

		visible = entity.has<HealthPart>()
	}

	private fun updateLabel() {
		val health = entity.health()

		label.text = if (health <= 0) "" else health.toString()
	}
}
