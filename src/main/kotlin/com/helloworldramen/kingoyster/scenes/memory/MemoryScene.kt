package com.helloworldramen.kingoyster.scenes.memory

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.parts.*
import com.helloworldramen.kingoyster.scenes.entity.EntitySprite
import godot.ColorRect
import godot.Label
import godot.Node2D
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.NodePath
import godot.extensions.getNodeAs

@RegisterClass
class MemoryScene : Node2D() {

	private val memoryRect: ColorRect by lazy { getNodeAs("MemoryRect")!! }
	private val entitySprite: EntitySprite by lazy { getNodeAs("EntitySprite")!! }
	private val durabilityLabel: Label by lazy { getNodeAs("EntitySprite/DurabilityLabel")!! }
	private val tween: Tween by lazy { getNodeAs("Tween")!! }

	private var entity: Entity = Entity.UNKNOWN
	private var worldPosition: Position = Position(0, 0)

	private var lastState: MemoryState = MemoryState.Unknown

	@RegisterFunction
	override fun _process(delta: Double) {
		val memoryState = nextMemoryState()
		
		if (lastState != memoryState) {
			lastState = memoryState
			updateAppearance()
		}
	}

	fun bind(entity: Entity, worldPosition: Position) {
		this.entity = entity
		this.worldPosition = worldPosition
		updateAppearance()
	}

	private fun updateAppearance() {
		when (lastState) {
			MemoryState.Visible -> animateVisible()
			MemoryState.Memory -> animateMemory()
			MemoryState.Unknown -> animateUnknown()
		}
	}

	private fun nextMemoryState(): MemoryState {
		val visiblePositions = entity.visiblePositions()
		val rememberedEntities = entity.find<MemoryPart>()?.get(worldPosition)

		return when {
			visiblePositions.contains(worldPosition) -> MemoryState.Visible
			rememberedEntities == null -> MemoryState.Unknown
			else -> MemoryState.Memory
		}
	}

	private fun animateVisible() {
		entitySprite.hide()
		tweenModulate(0.0)
	}

	private fun animateMemory() {
		val topEntity = entity.find<MemoryPart>()?.get(worldPosition)?.lastOrNull()

		entitySprite.bind(topEntity)
		entitySprite.show()

		when {
			topEntity == null -> {
				durabilityLabel.visible = false
				durabilityLabel.text = ""
			}
			topEntity.has<WeaponPart>() -> {
				durabilityLabel.visible = true
				durabilityLabel.text = topEntity.durability().toString()
			}
			topEntity.has<ItemPart>() -> {
				durabilityLabel.visible = true
				durabilityLabel.text = topEntity.uses().toString()
			}
			else -> {
				durabilityLabel.visible = false
				durabilityLabel.text = ""
			}
		}

		tweenModulate(0.5)
	}

	private fun animateUnknown() {
		entitySprite.hide()
		tweenModulate(1.0)
	}

	private fun tweenModulate(alpha: Double) {
		tween.interpolateProperty(memoryRect, NodePath("color"),
			initialVal = memoryRect.color, finalVal = Color(0.0, 0.0, 0.0, alpha),
			duration = 0.18, Tween.TRANS_QUAD, Tween.EASE_IN
		)
		tween.start()
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/memory/MemoryScene.tscn"
	}

	sealed class MemoryState {
		object Unknown : MemoryState()
		object Memory : MemoryState()
		object Visible : MemoryState()
	}
}
