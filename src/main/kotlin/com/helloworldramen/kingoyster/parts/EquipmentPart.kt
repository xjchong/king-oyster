package com.helloworldramen.kingoyster.parts

import com.helloworldramen.kingoyster.architecture.Entity
import com.helloworldramen.kingoyster.architecture.Part

class EquipmentPart(
    var weapon: Entity
) : Part {

    override fun copy(): Part {
        return EquipmentPart(weapon = weapon.copy())
    }
}

fun Entity.equippedWeaponPart(): WeaponPart? {
    return find<EquipmentPart>()?.weapon?.find()
}