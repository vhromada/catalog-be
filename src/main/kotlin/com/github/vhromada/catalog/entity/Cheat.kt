package com.github.vhromada.catalog.entity

/**
 * A class represents cheat.
 *
 * @author Vladimir Hromada
 */
data class Cheat(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Setting for game
     */
    val gameSetting: String?,

    /**
     * Setting for cheat
     */
    val cheatSetting: String?,

    /**
     * Data
     */
    val data: List<CheatData>

)
