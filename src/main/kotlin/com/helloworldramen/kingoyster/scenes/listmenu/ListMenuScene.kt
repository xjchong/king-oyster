package com.helloworldramen.kingoyster.scenes.listmenu

import com.helloworldramen.kingoyster.scenes.autoload.audio.AudioAutoload
import com.helloworldramen.kingoyster.directories.SFX
import godot.*
import godot.annotation.RegisterClass
import godot.annotation.RegisterFunction
import godot.extensions.getNodeAs
import godot.extensions.instanceAs
import godot.global.GD

@RegisterClass
class ListMenuScene : Control() {

	private val audio: AudioAutoload by lazy { getNodeAs(AudioAutoload.TREE_PATH)!! }
	private val menuTitleLabel: Label by lazy { getNodeAs("$VBOX_PREFIX/MenuTitleLabel")!! }
	private val itemsVBox: VBoxContainer by lazy { getNodeAs("$VBOX_PREFIX/ItemsVBox")!! }
	private val packedTitleListMenuItem = GD.load<PackedScene>(TitleListMenuItem.PATH)

	private var index: Int = 0
	private var callback: ((Int) -> Unit)? = null

	@RegisterFunction
	override fun _input(event: InputEvent) {
		if (!visible) return

		val itemCount = itemsVBox.getChildCount().toInt()

		acceptEvent()

		when {
			itemCount <= 0 -> return
			event.isActionPressed("ui_up", true) || event.isActionPressed("ui_left", true) -> {
				audio.play(SFX.MENU_MOVE_ALT)
				index = (index + itemCount - 1) % itemCount
			}
			event.isActionPressed("ui_down", true) || event.isActionPressed("ui_right", true) -> {
				audio.play(SFX.MENU_MOVE_ALT)
				index = (index + itemCount + 1) % itemCount
			}
			event.isActionPressed("ui_cancel") -> {
				audio.play(SFX.MENU_BACK)
				callback?.invoke(-1)
			}
			event.isActionPressed("ui_accept") -> {
				audio.play(SFX.MENU_SELECT_ALT)
				callback?.invoke(index)
			}
		}

		updateItems()
	}

	fun bind(menuTitle: String, itemTitles: List<String>, callback: (index: Int) -> Unit) {
		this.callback = callback
		index = 0

		menuTitleLabel.text = menuTitle

		itemsVBox.getChildren().forEach {
			itemsVBox.removeChild(it as Node)
			it.queueFree()
		}

		for (itemTitle in itemTitles) {
			val item = packedTitleListMenuItem?.instanceAs<TitleListMenuItem>() ?: continue

			item.bind(itemTitle)
			itemsVBox.addChild(item)
		}

		updateItems()
	}

	private fun updateItems() {
		itemsVBox.getChildren().forEachIndexed { i, child ->
			when(child) {
				is TitleListMenuItem -> if (index == i) child.select() else child.deselect()
			}
		}
	}

	companion object {
		private const val VBOX_PREFIX = "CenterContainer/PanelContainer/VBoxContainer"
	}
}
