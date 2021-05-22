package com.helloworldramen.kingoyster.scenes.fog

import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity
import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.parts.SensoryPart
import godot.ColorRect
import godot.Node2D
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class FogScene : Node2D() {

	private val colorRect: ColorRect by lazy { getNodeAs("ColorRect")!! }

	private var context: Context = Context.UNKNOWN()
	private var worldPosition: Position = Position(0, 0)

	@RegisterFunction
	override fun _process(delta: Double) {
		visible = context.player?.find(SensoryPart::class)?.visiblePositions?.contains(worldPosition) != true
	}

	fun bind(context: Context, worldPosition: Position) {
		zIndex = 1000
		this.context = context
		this.worldPosition = worldPosition
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/fog/FogScene.tscn"
	}
}
