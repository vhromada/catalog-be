package com.github.vhromada.catalog.entity

import com.github.vhromada.catalog.common.Time

/**
 * A class represents music.
 *
 * @author Vladimir Hromada
 */
data class Music(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    val name: String,

    /**
     * URL to english Wikipedia page about music
     */
    val wikiEn: String?,

    /**
     * URL to czech Wikipedia page about music
     */
    val wikiCz: String?,

    /**
     * Count of media
     */
    val mediaCount: Int,

    /**
     * Note
     */
    val note: String?,

    /**
     * Count of songs
     */
    val songsCount: Int,

    /**
     * Length
     */
    val length: Int

) {

    /**
     * Returns formatted length.
     *
     * @return formatted length
     */
    @Suppress("unused")
    fun getFormattedLength(): String {
        return Time(length = length).toString()
    }

}

