package com.helloworldramen.kingoyster.ai.considerations

import com.helloworldramen.kingoyster.ai.GameAiOptionContext
import com.helloworldramen.kingoyster.parts.weapon

class DoesTargetHaveWeaponConsideration(whenTrue: Double, whenFalse: Double) : BooleanConsideration(whenTrue, whenFalse) {

    override fun isConditionTrue(optionContext: GameAiOptionContext): Boolean {
        return optionContext.target?.weapon() != null
    }
}