package com.helloworldramen.kingoyster.scenes.memory

import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.parts.MemoryPart
import com.helloworldramen.kingoyster.parts.SensoryPart
import com.helloworldramen.kingoyster.scenes.entity.EntitySprite
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class MemoryScene : Node2D() {

	private val entitySprite: EntitySprite by lazy { getNodeAs("EntitySprite")!! }

	private var entity: Entity = Entity.UNKNOWN()
	private var worldPosition: Position = Position(0, 0)

	@RegisterFunction
	override fun _process(delta: Double) {
		updateAppearance()
	}

	fun bind(entity: Entity, worldPosition: Position) {
		this.entity = entity
		this.worldPosition = worldPosition
	}

	private fun updateAppearance() {
		val visiblePositions = entity.find(SensoryPart::class)?.visiblePositions ?: listOf()
		val rememberedEntities = entity.find(MemoryPart::class)?.get(worldPosition)

		if (rememberedEntities == null || visiblePositions.contains(worldPosition)) {
			visible = false
		} else {
			visible = true
			entitySprite.bind(rememberedEntities.lastOrNull())
		}
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/memory/MemoryScene.tscn"
	}
}
