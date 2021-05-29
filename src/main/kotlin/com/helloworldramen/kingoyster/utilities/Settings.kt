package com.helloworldramen.kingoyster.utilities

import godot.ConfigFile
import godot.core.GodotError
import godot.core.memory.GodotStatic

object Settings : GodotStatic {

    private const val CONFIG_PATH = "user://settings.cfg"
    private val configFile = ConfigFile()

    override fun collect() {
        configFile.free()
    }

    fun save(section: String, key: String, value: Any?): Boolean {
        configFile.setValue(section, key, value)

        return configFile.save(CONFIG_PATH) == GodotError.OK
    }


    fun load(section: String, key: String, defaultValue: Any): Any? {
        return configFile.getValue(section, key, defaultValue)
    }
}