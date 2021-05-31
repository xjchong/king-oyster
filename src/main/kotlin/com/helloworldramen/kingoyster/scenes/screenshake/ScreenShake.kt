package com.helloworldramen.kingoyster.scenes.screenshake

import godot.Camera2D
import godot.Node
import godot.Timer
import godot.Tween
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.core.NodePath
import godot.core.Vector2
import godot.extensions.getNodeAs
import kotlin.random.Random

@RegisterClass
class ScreenShake : Node() {

	private val tween: Tween by lazy { getNodeAs("Tween")!! }
	private val parentCamera: Camera2D by lazy { getParent() as Camera2D }
	private val frequencyTimer: Timer by lazy { getNodeAs("FrequencyTimer")!! }
	private val durationTimer: Timer by lazy { getNodeAs("DurationTimer")!! }

	private var amplitude: Double = 0.0
	private var priority = 0

	@RegisterFunction
	override fun _ready() {
		frequencyTimer.timeout.connect(this, ::onFrequencyTimeout)
		durationTimer.timeout.connect(this, ::onDurationTimeout)
	}

	@RegisterFunction
	fun onFrequencyTimeout() {
		shakeCamera()
	}

	@RegisterFunction
	fun onDurationTimeout() {
		resetCamera()
		frequencyTimer.stop()
	}

	fun startSmallShake() {
		start(0.06, 20.0, 1.8)
	}

	fun startMediumShake() {
		start(0.08, 24.0, 4.0)
	}

	fun start(duration: Double, frequency: Double, amplitude: Double, priority: Int = 0) {
		if (priority < this.priority) return

		this.priority = priority
		this.amplitude = amplitude

		durationTimer.waitTime = duration
		durationTimer.start()

		frequencyTimer.waitTime = 1 / frequency
		frequencyTimer.start()

		shakeCamera()
	}

	private fun shakeCamera() {
		val randomOffset = Vector2(
			Random.nextDouble(-amplitude, amplitude),
			Random.nextDouble(-amplitude, amplitude)
		)

		tween.interpolateProperty(parentCamera, NodePath("offset"),
			initialVal = parentCamera.offset,
			finalVal = randomOffset,
			duration = frequencyTimer.waitTime,
			transType = transitionType, easeType = easeType
		)
		tween.start()
	}

	private fun resetCamera() {
		tween.interpolateProperty(parentCamera, NodePath("offset"),
			initialVal = parentCamera.offset, Vector2.ZERO,
			duration = frequencyTimer.waitTime,
			transType = transitionType, easeType =easeType
		)
		tween.start()

		priority = 0
	}

	companion object {
		private const val transitionType = Tween.TRANS_SINE
		private const val easeType = Tween.EASE_IN_OUT
	}
}
