package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.WeaponAttackEvent
import com.helloworldramen.kingoyster.extensions.freeChildren
import com.helloworldramen.kingoyster.extensions.positionsForEach
import com.helloworldramen.kingoyster.parts.EquipmentPart
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.telegraphedPositions
import com.helloworldramen.kingoyster.parts.visiblePositions
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.memory.MemoryScene
import com.helloworldramen.kingoyster.scenes.tileoverlay.TileOverlayScene
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.Color
import godot.core.Vector2
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD

@RegisterClass
class WorldScene : Node2D(), EventBusSubscriber {

	private val telegraphBucket: YSort by lazy { getNodeAs("TelegraphBucket")!! }
	private val flashBucket: YSort by lazy { getNodeAs("FlashBucket")!! }
	private val tileBucket: YSort by lazy { getNodeAs("FloorRect/TileBucket")!! }
	private val blackoutRect: ColorRect by lazy { getNodeAs("BlackoutRect")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }

	private val packedTileOverlayScene = GD.load<PackedScene>(TileOverlayScene.PATH)
	private val packedMemoryScene = GD.load<PackedScene>(MemoryScene.PATH)
	private val packedEntityScene = GD.load<PackedScene>(EntityScene.PATH)

	private val sceneForEntity: MutableMap<Entity, EntityScene> = mutableMapOf()
	private var telegraphOverlayForPosition: MutableMap<Position, TileOverlayScene> = mutableMapOf()
	private var flashOverlayForPosition: MutableMap<Position, TileOverlayScene> = mutableMapOf()

	private var context: Context = Context.UNKNOWN

	val isAnimating: Boolean
		get() = animationPlayer.isPlaying()

	override fun receiveEvent(event: Event) {
		when (event) {
			is WeaponAttackEvent -> {
				if (event.attacker.isPlayer) {
					animateWeaponAttack(event.positions)
				}
			}
		}
	}

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this, WeaponAttackEvent::class)
	}

	@RegisterFunction
	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	@RegisterFunction
	override fun _process(delta: Double) {
		val playerVisiblePositions = context.player.visiblePositions()
		val telegraphedPositions = sceneForEntity.keys.filter { entity ->
			entity.health() > 0
		}.flatMap { entity ->
			entity.telegraphedPositions().filter { playerVisiblePositions.contains(it) }
		}.toSet()

		context.world.positionsForEach { position ->
			telegraphOverlayForPosition[position]?.visible = telegraphedPositions.contains(position)
		}
	}

	fun bind(context: Context): EntityScene? {
		this.context = context
		val world = context.world

		telegraphBucket.freeChildren()
		flashBucket.freeChildren()
		tileBucket.freeChildren()
		sceneForEntity.clear()
		telegraphOverlayForPosition.clear()
		flashOverlayForPosition.clear()

		world.positionsForEach { position ->
			// Add the entities for this position.
			world[position]?.forEach { entity ->
				packedEntityScene?.instanceAs<EntityScene>()?.let {
					tileBucket.addChild(it)
					it.bind(context, entity)
					sceneForEntity[entity] = it
				}

				// Also add scenes for entities that are not explicitly visible.
				entity.find<EquipmentPart>()?.weapon?.let { weapon ->
					packedEntityScene?.instanceAs<EntityScene>()?.let {
						tileBucket.addChild(it)
						it.bind(context, weapon)
						sceneForEntity[weapon] = it
					}
				}
			}

			// Setup the telegraph for this position.
			packedTileOverlayScene?.instanceAs<TileOverlayScene>()?.let { overlayScene ->
				telegraphBucket.addChild(overlayScene)
				telegraphOverlayForPosition[position] = overlayScene
				overlayScene.position = calculateNodePosition(position)
				overlayScene.showTelegraph()
				overlayScene.hide()
			}

			// Setup the flash for this position.
			packedTileOverlayScene?.instanceAs<TileOverlayScene>()?.let { overlayScene ->
				flashBucket.addChild(overlayScene)
				flashOverlayForPosition[position] = overlayScene
				overlayScene.position = calculateNodePosition(position)
				overlayScene.hide()
			}

			// Setup the memory for this position.
			packedMemoryScene?.instanceAs<MemoryScene>()?.let { memoryScene ->
				tileBucket.addChild(memoryScene)
				memoryScene.bind(context.player, position)
				memoryScene.position = calculateNodePosition(position)
			}
		}

		return sceneForEntity[context.player]
	}

	fun animateBump(entity: Entity, direction: Direction) {
		sceneForEntity[entity]?.animateBump(direction)
	}

	fun fadeOut() {
		animationPlayer.play("fade_out")
	}

	fun fadeIn() {
		animationPlayer.play("fade_in")
	}

	private fun animateWeaponAttack(positions: Set<Position>) {
		positions.forEach {
			flashOverlayForPosition[it]?.showFlash(Color.white)
		}
	}

	private fun calculateNodePosition(worldPosition: Position): Vector2 {
		return Vector2(worldPosition.x * TILE_SIZE, worldPosition.y * TILE_SIZE)
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/world/WorldScene.tscn"
		const val TILE_SIZE = 32
	}
}
