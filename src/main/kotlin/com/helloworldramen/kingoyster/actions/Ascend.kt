package com.helloworldramen.kingoyster.actions

import com.helloworldramen.kingoyster.oyster.Action
import com.helloworldramen.kingoyster.oyster.Context
import com.helloworldramen.kingoyster.oyster.Entity

data class Ascend(
    val context: Context,
    val user: Entity
) : Action
