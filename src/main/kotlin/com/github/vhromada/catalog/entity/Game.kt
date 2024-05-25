package com.github.vhromada.catalog.entity

/**
 * A class represents game.
 *
 * @author Vladimir Hromada
 */
data class Game(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    val name: String,

    /**
     * URL to english Wikipedia page about game
     */
    val wikiEn: String?,

    /**
     * URL to czech Wikipedia page about game
     */
    val wikiCz: String?,

    /**
     * Count of media
     */
    val mediaCount: Int,

    /**
     * Format
     */
    val format: String,

    /**
     * True if there is crack
     */
    val crack: Boolean,

    /**
     * True if there is serial key
     */
    val serialKey: Boolean,

    /**
     * True if there is patch
     */
    val patch: Boolean,

    /**
     * True if there is trainer
     */
    val trainer: Boolean,

    /**
     * True if there is data for trainer
     */
    val trainerData: Boolean,

    /**
     * True if there is editor
     */
    val editor: Boolean,

    /**
     * True if there are saves
     */
    val saves: Boolean,

    /**
     * Other data
     */
    val otherData: String?,

    /**
     * Note
     */
    val note: String?,

    /**
     * True if there is cheat
     */
    val cheat: Boolean

)
