package com.github.vhromada.catalog.entity

/**
 * A class represents program.
 *
 * @author Vladimir Hromada
 */
data class Program(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    val name: String,

    /**
     * URL to english Wikipedia page about program
     */
    val wikiEn: String?,

    /**
     * URL to czech Wikipedia page about program
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
     * Other data
     */
    val otherData: String?,

    /**
     * Note
     */
    val note: String?

)
