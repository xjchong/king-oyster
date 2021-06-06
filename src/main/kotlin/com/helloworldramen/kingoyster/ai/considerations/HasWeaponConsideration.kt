package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.weapon

class HasWeaponConsideration(preferTrue: Boolean = true) : BooleanConsideration(preferTrue) {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        return optionContext.entity.weapon() != null
    }
}