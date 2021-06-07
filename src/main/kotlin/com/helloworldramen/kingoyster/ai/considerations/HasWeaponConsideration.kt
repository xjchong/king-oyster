package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.weapon

class HasWeaponConsideration(whenTrue: Double, whenFalse: Double) : BooleanConsideration(whenTrue, whenFalse) {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        return optionContext.entity.weapon() != null
    }
}