package com.helloworldramen.kingoyster.scenes.eventaudio

import com.helloworldramen.kingoyster.actions.MoveType
import com.helloworldramen.kingoyster.architecture.Context
import com.helloworldramen.kingoyster.extensions.isVisibleToPlayer
import com.helloworldramen.kingoyster.eventbus.Event
import com.helloworldramen.kingoyster.eventbus.EventBus
import com.helloworldramen.kingoyster.eventbus.EventBusSubscriber
import com.helloworldramen.kingoyster.eventbus.events.*
import com.helloworldramen.kingoyster.parts.weapon
import com.helloworldramen.kingoyster.scenes.autoload.audio.AudioAutoload
import com.helloworldramen.kingoyster.utilities.SFX
import godot.Node
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs

@RegisterClass
class EventAudio : Node(), EventBusSubscriber {

	private val audio: AudioAutoload by lazy { getNodeAs(AudioAutoload.TREE_PATH)!! }

	private var context: Context = Context.UNKNOWN

	@RegisterFunction
	override fun _ready() {
		EventBus.register(this,
			AscendEvent::class,
			WeaponAttackEvent::class,
			DamageEntityEvent::class,
			DamageWeaponEvent::class,
			DeathEvent::class,
			OpenEvent::class,
			HealEvent::class,
			GameOverEvent::class,
			MoveEvent::class,
			TakeItemEvent::class,
			TakeWeaponEvent::class,
			ThrowWeaponEvent::class,
			UseItemEvent::class
		)
	}

	@RegisterFunction
	override fun _onDestroy() {
		EventBus.unregister(this)
	}

	override fun receiveEvent(event: Event) {
		when (event) {
			is AscendEvent -> onAscend(event)
			is WeaponAttackEvent -> onAttack(event)
			is DamageEntityEvent -> onDamageEntity(event)
			is DamageWeaponEvent -> onDamageWeapon(event)
			is DeathEvent -> onDeath(event)
			is OpenEvent -> onDoor(event)
			is HealEvent -> onHeal(event)
			is GameOverEvent -> onGameOver(event)
			is MoveEvent -> onMove(event)
			is TakeItemEvent -> onTakeItem(event)
			is TakeWeaponEvent -> onTakeWeapon(event)
			is ThrowWeaponEvent -> onThrowWeapon(event)
			is UseItemEvent -> onUseItem(event)
		}
	}

	fun bind(context: Context) {
		this.context = context
	}

	private fun onAscend(event: AscendEvent) {
		audio.play(SFX.STAIRS)
	}

	private fun onAttack(event: WeaponAttackEvent) {
	}

	private fun onDamageEntity(event: DamageEntityEvent) {
		if (!event.target.isVisibleToPlayer(context)) return

		audio.play(SFX.HIT_BASH)
	}

	private fun onDamageWeapon(event: DamageWeaponEvent) {
		val isWeaponOwned = event.owner?.weapon() == event.weapon

		if ((!isWeaponOwned && !event.weapon.isVisibleToPlayer(context))
			|| isWeaponOwned && event.owner?.isVisibleToPlayer(context) == false) {
			return
		}

		if (event.isBroken) {
			audio.play(SFX.WEAPON_BREAK)
		}
	}

	private fun onDeath(event: DeathEvent) {
		if (!event.entity.isVisibleToPlayer(context)) return

		audio.play(SFX.DEATH)
	}

	private fun onDoor(event: OpenEvent) {
		if (!event.actor.isVisibleToPlayer(context)) return

		audio.play(SFX.DOOR_OPEN)
	}

	private fun onHeal(event: HealEvent) {
		if (!event.target.isVisibleToPlayer(context)) return

		audio.play(SFX.HEAL)
	}

	private fun onTakeItem(event: TakeItemEvent) {
		if (!event.taker.isVisibleToPlayer(context)) return

		audio.play(SFX.TAKE)
	}

	private fun onTakeWeapon(event: TakeWeaponEvent) {
		if (!event.taker.isVisibleToPlayer(context)) return

		audio.play(SFX.TAKE)
	}

	private fun onGameOver(event: GameOverEvent) {
	}

	private fun onMove(event: MoveEvent) {
		if (!event.entity.isPlayer) return

		when (event.type) {
			MoveType.Charge -> audio.play(SFX.HIT_SWOOSH_CUT)
			MoveType.Default -> audio.play(SFX.MOVE_STEP)
		}
	}

	private fun onThrowWeapon(event: ThrowWeaponEvent) {
		if (!event.thrower.isVisibleToPlayer(context)) return

		audio.play(SFX.HIT_CUT_CRIT)
	}

	private fun onUseItem(event: UseItemEvent) {
		if (!event.user.isVisibleToPlayer(context)) return

		audio.play(SFX.ITEM_BREAK)
	}
}
