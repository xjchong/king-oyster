package com.helloworldramen.kingoyster.scenes.directedselection

import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.architecture.Direction
import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Position
import com.helloworldramen.kingoyster.scenes.tileoverlay.TileOverlayScene
import com.helloworldramen.kingoyster.scenes.world.WorldScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD

@RegisterClass
class DirectedSelectionScene : Node2D() {

	private val tileOverlayScenesBucket: Node2D by lazy { getNodeAs("TileOverlayScenesBucket")!! }
	private val packedTileOverlayScene = GD.load<PackedScene>(TileOverlayScene.PATH)

	private var overlayForPosition: Map<Position, TileOverlayScene> = mapOf()
	var pathForDirection: Map<Direction, List<Position>> = mapOf()
		private set
	private var destinationPattern: (Position, Direction) -> List<Position> = { _,_ -> listOf() }
	private var context: Context = Context.UNKNOWN

	var selectedDirection: Direction? = null
		private set

	@RegisterFunction
	override fun _process(delta: Double) {
		if (selectedDirection != null && !visible) {
			selectedDirection = null
			highlightSelectedDirection()
		}
	}

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (!visible) return

		val pressedDirection = when {
			event.isActionPressed("ui_up") -> Direction.North
			event.isActionPressed("ui_right") -> Direction.East
			event.isActionPressed("ui_down") -> Direction.South
			event.isActionPressed("ui_left") -> Direction.West
			else -> null
		}

		if (pressedDirection == null || selectedDirection == pressedDirection) {
			return
		}

		selectedDirection = pressedDirection
		highlightSelectedDirection()
	}

	fun bindPathUntil(context: Context, position: Position,
					  predicate: (Position) -> Boolean,
					  destinationPattern: (Position, Direction) -> List<Position>) {
		bindPathsWhere(true, context, position, predicate, destinationPattern)
	}

	fun bindPathWhile(context: Context, position: Position,
					  predicate: (Position) -> Boolean,
					  destinationPattern: (Position, Direction) -> List<Position>) {
		bindPathsWhere(false, context, position, predicate, destinationPattern)
	}

	fun bindNone(context: Context) {
		bindPathWhile(context, Position(0, 0), predicate = { false }, { _, _ -> listOf() })
	}

	private fun bindPathsWhere(isInclusive: Boolean, context: Context, position: Position,
							   predicate: (Position) -> Boolean,
							   destinationPattern: (Position, Direction) -> List<Position>) {
		// Update the context if needed.
		if (context != this.context) {
			updateContext(context)
		}

		this.destinationPattern = destinationPattern

		// Set the paths for direction.
		pathForDirection = listOf(Direction.North, Direction.South, Direction.East, Direction.West).associateWith {
			if (isInclusive) context.straightPathUntil(position, it, predicate)
			else context.straightPathWhile(position, it, predicate)
		}

		selectedDirection = null
		highlightSelectedDirection()
	}

	fun updateContext(context: Context) {
		val world = context.world
		val nextOverlayForPosition = mutableMapOf<Position, TileOverlayScene>()

		this.context = context

		tileOverlayScenesBucket.getChildren().forEach {
			tileOverlayScenesBucket.removeChild(it as Node)
			it.queueFree()
		}

		Position(world.width - 1, world.height - 1).forEach { position ->
			packedTileOverlayScene?.instanceAs<TileOverlayScene>()?.let { tileOverlayScene ->
				tileOverlayScenesBucket.addChild(tileOverlayScene)
				tileOverlayScene.position = Vector2(
					position.x * WorldScene.TILE_SIZE,
					position.y * WorldScene.TILE_SIZE)
				tileOverlayScene.showNonInteractive()

				nextOverlayForPosition[position] = tileOverlayScene
			}
		}

		overlayForPosition = nextOverlayForPosition
	}

	private fun highlightSelectedDirection() {
		overlayForPosition.values.forEach {
			it.showNonInteractive()
		}

		selectedDirection?.let { direction ->
			val path = pathForDirection[direction] ?: listOf()
			val destination = path.lastOrNull() ?: return
			val pattern = destinationPattern(destination, direction)

			path.forEach { overlayForPosition[it]?.showCanSelect() }
			pattern.forEach { overlayForPosition[it]?.showSelected() }
		}
	}
}
