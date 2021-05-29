package com.helloworldramen.kingoyster.scenes.listmenu

import com.helloworldramen.kingoyster.architecture.Entity
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.annotation.RegisterSignal
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD
import godot.signals.signal

@RegisterClass
class ListMenuScene : Control() {

	private val vBox: VBoxContainer by lazy { getNodeAs("CenterContainer/PanelContainer/VBox")!! }

	private val packedTitleListMenuItem = GD.load<PackedScene>(TitleListMenuItem.PATH)

	private var index: Int = 0

	@RegisterSignal
	val signalListItemSelected by signal<Int>("selectedIndex")

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (!visible) return

		val itemCount = vBox.getChildCount().toInt()

		acceptEvent()

		when {
			itemCount <= 0 -> return
			event.isActionPressed("ui_up", true) || event.isActionPressed("ui_left", true) -> {
				index = (index + itemCount - 1) % itemCount
			}
			event.isActionPressed("ui_down", true) || event.isActionPressed("ui_right", true) -> {
				index = (index + itemCount + 1) % itemCount
			}
			event.isActionPressed("ui_cancel") -> signalListItemSelected.emit(-1)
			event.isActionPressed("ui_accept") -> signalListItemSelected.emit(index)
		}

		updateItems()
	}

	fun bindTitles(titles: List<String>) {
		index = 0

		vBox.getChildren().forEach {
			vBox.removeChild(it as Node)
			it.queueFree()
		}

		for (title in titles) {
			val item = packedTitleListMenuItem?.instanceAs<TitleListMenuItem>() ?: continue

			item.bind(title)
			vBox.addChild(item)
		}

		updateItems()
	}

	private fun updateItems() {
		vBox.getChildren().forEachIndexed { i, child ->
			when(child) {
				is TitleListMenuItem -> if (index == i) child.select() else child.deselect()
			}
		}
	}
}
