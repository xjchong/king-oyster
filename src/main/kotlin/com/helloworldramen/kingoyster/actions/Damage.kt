package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Context

data class Damage(
    override val context: Context,
    val value: Int
) : Action