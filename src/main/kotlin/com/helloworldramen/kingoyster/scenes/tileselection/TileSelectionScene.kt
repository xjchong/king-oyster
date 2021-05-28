package com.helloworldramen.kingoyster.scenes.tileselection

import com.helloworldramen.kingoyster.architecture.Position
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
import java.util.Timer
import kotlin.concurrent.timerTask
import kotlin.math.roundToInt

@RegisterClass
class TileSelectionScene : Control() {

	@RegisterSignal
	val signalTilesSelected by signal<String>("selectionReason")

	var selection: List<Position> = listOf()
		private set

	private val tileOverlayScenesBucket: Node2D by lazy { getNodeAs("TileOverlayScenesBucket")!! }

	private val packedTileOverlayScene = GD.load<PackedScene>(TileOverlayScene.PATH)

	private var tileOverlaySceneForPosition: Map<Position, TileOverlayScene> = mapOf()

	private var currentGroupIndex: Int = 0

	private var positionGroups: List<List<Position>> = listOf()
	private var selectionGroups: List<TileSelectionGroup> = listOf()

	private var selectablePositions: Set<Position> = setOf()
	private var isSelecting = false
	private var selectionReason: String? = null

	private var width: Int = 0
	private var height: Int = 0

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (!isSelecting) return

        // It is important to call this to consume the event.
		// Without this, upon unpausing the game, the game scene
		// may still pick up the last key pressed and immediately parse it.
		acceptEvent()

		val currentSelectionGroup = selectionGroups[currentGroupIndex]

		when {
			event.isActionPressed("ui_up", true) -> {
				selectGroup(currentSelectionGroup.northNeighborIndex)
			}
			event.isActionPressed("ui_right", true) -> {
				selectGroup(currentSelectionGroup.eastNeighborIndex)
			}
			event.isActionPressed("ui_down", true) -> {
				selectGroup(currentSelectionGroup.southNeighborIndex)
			}
			event.isActionPressed("ui_left", true) -> {
				selectGroup(currentSelectionGroup.westNeighborIndex)
			}
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

	fun startTileSelection(reason: String, positionGroups: List<List<Position>>) {
		if (positionGroups.isEmpty()) return

		this.selectionReason = reason
		this.positionGroups = positionGroups
		this.selection = listOf()
		selectablePositions = positionGroups.flatten().toSet()
		selectionGroups = calculateSelectionGroups(positionGroups)
		currentGroupIndex = 0
		isSelecting = true

		selectGroup(0)
	}

	private fun confirmCurrentSelection() {
		selection = positionGroups[currentGroupIndex]
		isSelecting = false
		tileOverlaySceneForPosition.values.forEach {
			it.hide()
		}

		signalTilesSelected.emit(selectionReason ?: "")
	}

	private fun cancelSelection() {
		selection = listOf()
		isSelecting = false
		tileOverlaySceneForPosition.values.forEach {
			it.hide()
		}

		signalTilesSelected.emit(selectionReason ?: "")
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

	private fun calculateSelectionGroups(positionGroups: List<List<Position>>): List<TileSelectionGroup> {
		val selectionGroups = mutableListOf<TileSelectionGroup>()
		val averagePositions = positionGroups.map { it.averagePosition() }

		for ((index, averagePosition) in averagePositions.withIndex()) {
			selectionGroups.add(
				TileSelectionGroup(index,
					northNeighborIndex = averagePositions.closestNeighborIndex(averagePosition, index) {
						it.y < averagePosition.y
					} ?: index,
					eastNeighborIndex = averagePositions.closestNeighborIndex(averagePosition, index) {
						it.x > averagePosition.x
					} ?: index,
					southNeighborIndex = averagePositions.closestNeighborIndex(averagePosition, index) {
						it.y > averagePosition.y
					} ?: index,
					westNeighborIndex = averagePositions.closestNeighborIndex(averagePosition, index) {
						it.x < averagePosition.x
					} ?: index,
				)
			)
		}

		return selectionGroups
	}

	private fun List<Position>.closestNeighborIndex(position: Position, index: Int, predicate: (Position) -> Boolean): Int? {
		// For example, only positions west of a certain position.
		val filtered = filterIndexed { i, p ->
			i != index && predicate(p)
		}

		// Get the closest neighbor to the parameter position.
		val closestNeighbor = filtered.minByOrNull { position.distanceFrom(it) } ?: return null

		// Get the index of that neighbor from the original list.
		val closestNeighborIndex = indexOf(closestNeighbor)

		return if (closestNeighborIndex < 0) null else closestNeighborIndex
	}

	private fun List<Position>.averagePosition(): Position {
		var totalX = 0
		var totalY = 0

		forEach {
			totalX += it.x
			totalY += it.y
		}

		return Position(
			(totalX / size.toDouble().roundToInt()),
			(totalY / size.toDouble().roundToInt())
		)
	}
}
