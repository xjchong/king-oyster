package com.helloworldramen.kingoyster.scenes.world

import com.helloworldramen.kingoyster.architecture.*
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.BreedEvent
import com.helloworldramen.kingoyster.eventbus.events.DamagePositionEvent
import com.helloworldramen.kingoyster.extensions.freeChildren
import com.helloworldramen.kingoyster.extensions.isVisibleToPlayer
import com.helloworldramen.kingoyster.extensions.positionsForEach
import com.helloworldramen.kingoyster.parts.ItemSlotPart
import com.helloworldramen.kingoyster.parts.WeaponSlotPart
import com.helloworldramen.kingoyster.parts.combat.DamageType
import com.helloworldramen.kingoyster.parts.combat.ElementType
import com.helloworldramen.kingoyster.parts.combat.health
import com.helloworldramen.kingoyster.parts.telegraphedPositions
import com.helloworldramen.kingoyster.parts.visiblePositions
import com.helloworldramen.kingoyster.scenes.entity.EntityScene
import com.helloworldramen.kingoyster.scenes.floor.FloorScene
import com.helloworldramen.kingoyster.scenes.memory.MemoryScene
import com.helloworldramen.kingoyster.scenes.tileoverlay.TileOverlayScene
import com.helloworldramen.kingoyster.worldgen.metadata.WorldFlavor
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
	private val entityBucket: YSort by lazy { getNodeAs("FloorRect/YSort/EntityBucket")!! }
	private val memoryBucket: YSort by lazy { getNodeAs("FloorRect/YSort/MemoryBucket")!! }
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
	private var flavor: WorldFlavor = WorldFlavor.DEFAULT

	val isAnimating: Boolean
		get() = animationPlayer.isPlaying()

	override fun receiveEvent(event: Event) {
		when (event) {
			is BreedEvent -> {
				addEntityScene(event.child)
			}
			is DamagePositionEvent -> {
				if (event.source?.isPlayer == true) {
					animateDamage(event.position, event.damageType, event.elementType)
				}
			}
		}
	}

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this, BreedEvent::class, DamagePositionEvent::class)

		blackoutRect.color = Color.html(flavor.backgroundColor)
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

	fun bind(context: Context, flavor: WorldFlavor): EntityScene? {
		this.context = context
		this.flavor = flavor
		val world = context.world

		blackoutRect.color = Color.html(flavor.backgroundColor)
		floorBucket.freeChildren()
		entityBucket.freeChildren()
		memoryBucket.freeChildren()
		telegraphBucket.freeChildren()
		flashBucket.freeChildren()
		sceneForEntity.clear()
		telegraphOverlayForPosition.clear()
		flashOverlayForPosition.clear()

		world.positionsForEach { position ->
			// Add the entities for this position.
			world[position]?.forEach { entity ->
				addEntityScene(entity)

				// Also add scenes for entities that are not explicitly visible.
				entity.find<WeaponSlotPart>()?.weapon?.let { weapon ->
					addEntityScene(weapon)
				}
				entity.find<ItemSlotPart>()?.item?.let { item ->
					addEntityScene(item)
				}
			}

			// Setup the floor for this position.
			packedFloorScene?.instanceAs<FloorScene>()?.let { floorScene ->
				floorBucket.addChild(floorScene)
				floorScene.bind(flavor.floorFlavor.sprite, flavor.floorFlavor.weightedFrameIndices)
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
				memoryBucket.addChild(memoryScene)
				memoryScene.bind(context.player, position, flavor)
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

	private fun addEntityScene(entity: Entity) {
		packedEntityScene?.instanceAs<EntityScene>()?.let {
			entityBucket.addChild(it)
			it.bind(context, entity)
			sceneForEntity[entity] = it
		}
	}

	private fun animateDamage(position: Position, damageType: DamageType, elementType: ElementType) {
		if (!position.isVisibleToPlayer(context)) return

		flashOverlayForPosition[position]?.showFlash(Color(1, 1, 1, 0.8))
	}

	private fun calculateNodePosition(worldPosition: Position): Vector2 {
		return Vector2(worldPosition.x * TILE_SIZE, worldPosition.y * TILE_SIZE)
	}

	companion object {
		const val PATH = "res://src/main/kotlin/com/helloworldramen/kingoyster/scenes/world/WorldScene.tscn"
		const val TILE_SIZE = 32
	}
}
