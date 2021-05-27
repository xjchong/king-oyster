package com.helloworldramen.kingoyster.scenes.tileselection

import com.helloworldramen.kingoyster.oyster.Position
import com.helloworldramen.kingoyster.scenes.tileoverlay.TileOverlayScene
import com.helloworldramen.kingoyster.scenes.world.WorldScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD
import godot.signals.signal

@RegisterClass
class TileSelectionScene : Node2D() {

	@RegisterSignal
	val signalTilesSelected by signal<Int>("selectedGroupIndex")

	private val tileOverlayScenesBucket: Node2D by lazy { getNodeAs("TileOverlayScenesBucket")!! }

	private val packedTileOverlayScene = GD.load<PackedScene>(TileOverlayScene.PATH)

	private var tileOverlaySceneForPosition: Map<Position, TileOverlayScene> = mapOf()

	private var currentGroupIndex: Int = 0

	var positionGroups: List<List<Position>> = listOf()
		private set

	private var selectablePositions: Set<Position> = setOf()
	private var isSelecting = false

	private var width: Int = 0
	private var height: Int = 0

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (!isSelecting) return

		when {
			event.isActionPressed("ui_up", true) -> selectGroup(currentGroupIndex - 1)
			event.isActionPressed("ui_right", true) -> selectGroup(currentGroupIndex + 1)
			event.isActionPressed("ui_down", true) -> selectGroup(currentGroupIndex + 1)
			event.isActionPressed("ui_left", true) -> selectGroup(currentGroupIndex - 1)
			event.isActionPressed("ui_accept") -> confirmCurrentSelection()
			event.isActionPressed("ui_cancel") -> cancelSelection()
		}
	}

	fun bind(width: Int, height: Int) {
		this.width = width
		this.height = height

		val nextTileOverlaySceneForPosition = mutableMapOf<Position, TileOverlayScene>()
		tileOverlayScenesBucket.getChildren().forEach {
			removeChild(it as Node)
			it.queueFree()
		}

		Position(width, height).forEach { position ->
			packedTileOverlayScene?.instanceAs<TileOverlayScene>()?.let { tileOverlayScene ->
				tileOverlayScenesBucket.addChild(tileOverlayScene)
				tileOverlayScene.position = Vector2(
					position.x * WorldScene.TILE_SIZE,
					position.y * WorldScene.TILE_SIZE)
				tileOverlayScene.hide()

				nextTileOverlaySceneForPosition[position] = tileOverlayScene
			}
		}

		tileOverlaySceneForPosition = nextTileOverlaySceneForPosition
	}

	fun startTileSelection(positionGroups: List<List<Position>>) {
		if (positionGroups.isEmpty()) return

		this.positionGroups = positionGroups
		selectablePositions = positionGroups.flatten().toSet()
		isSelecting = true

		selectGroup(0)
	}

	private fun confirmCurrentSelection() {
		isSelecting = false
		tileOverlaySceneForPosition.values.forEach {
			it.hide()
		}

		signalTilesSelected.emit(currentGroupIndex)
	}

	private fun cancelSelection() {
		isSelecting = false
		tileOverlaySceneForPosition.values.forEach {
			it.hide()
		}

		signalTilesSelected.emit(-1)
	}

	private fun selectGroup(nextIndex: Int) {
		currentGroupIndex = (nextIndex + positionGroups.size) % positionGroups.size

		val selectedPositions = positionGroups[currentGroupIndex]

		Position(width, height).forEach { position ->
			when {
				selectedPositions.contains(position) -> tileOverlaySceneForPosition[position]?.showSelected()
				selectablePositions.contains(position) -> tileOverlaySceneForPosition[position]?.showCanSelect()
				else -> tileOverlaySceneForPosition[position]?.showNonInteractive()
			}
		}
	}
}
