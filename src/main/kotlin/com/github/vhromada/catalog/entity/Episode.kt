package com.github.vhromada.catalog.entity

import com.github.vhromada.catalog.common.Time

/**
 * A class represents episode.
 *
 * @author Vladimir Hromada
 */
data class Episode(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Number of episode
     */
    val number: Int,

    /**
     * Name
     */
    val name: String,

    /**
     * Length
     */
    val length: Int,

    /**
     * Note
     */
    val note: String?

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
