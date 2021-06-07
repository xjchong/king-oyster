package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.WeaponAttackEvent
import com.helloworldramen.kingoyster.extensions.freeChildren
import com.helloworldramen.kingoyster.extensions.positionsForEach
import com.helloworldramen.kingoyster.parts.ItemSlotPart
import com.helloworldramen.kingoyster.parts.WeaponSlotPart
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.telegraphedPositions
import com.helloworldramen.kingoyster.parts.visiblePositions
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.floor.FloorScene
import com.helloworldramen.kingoyster.scenes.memory.MemoryScene
import com.helloworldramen.kingoyster.scenes.tileoverlay.TileOverlayScene
import com.helloworldramen.kingoyster.utilities.WeightedCollection
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

	private val floorBucket: YSort by lazy { getNodeAs("FloorRect/FloorBucket")!! }
	private val tileBucket: YSort by lazy { getNodeAs("FloorRect/TileBucket")!! }
	private val telegraphBucket: YSort by lazy { getNodeAs("TelegraphBucket")!! }
	private val flashBucket: YSort by lazy { getNodeAs("FlashBucket")!! }
	private val blackoutRect: ColorRect by lazy { getNodeAs("BlackoutRect")!! }
	private val animationPlayer: AnimationPlayer by lazy { getNodeAs("AnimationPlayer")!! }

	private val packedFloorScene = GD.load<PackedScene>(FloorScene.PATH)
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

		floorBucket.freeChildren()
		tileBucket.freeChildren()
		telegraphBucket.freeChildren()
		flashBucket.freeChildren()
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
				entity.find<WeaponSlotPart>()?.weapon?.let { weapon ->
					packedEntityScene?.instanceAs<EntityScene>()?.let {
						tileBucket.addChild(it)
						it.bind(context, weapon)
						sceneForEntity[weapon] = it
					}
				}
				entity.find<ItemSlotPart>()?.item?.let { item ->
					packedEntityScene?.instanceAs<EntityScene>()?.let {
						tileBucket.addChild(it)
						it.bind(context, item)
						sceneForEntity[item] = it
					}
				}
			}

			// Setup the floor for this position.
			packedFloorScene?.instanceAs<FloorScene>()?.let { floorScene ->
				floorBucket.addChild(floorScene)
				floorScene.bind("dry_grass_floor", WeightedCollection(
					500 to 0, 30 to 1, 30 to 2, 30 to 3,
					25 to 4, 25 to 5, 25 to 6, 25 to 7,
					25 to 8, 25 to 9, 25 to 10, 25 to 11,
					25 to 12, 25 to 13, 25 to 14, 25 to 15,
					25 to 16, 25 to 17, 25 to 18, 25 to 19
				))
				floorScene.position = calculateNodePosition(position)
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
