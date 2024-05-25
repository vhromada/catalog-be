package com.github.vhromada.catalog.entity

import com.github.vhromada.catalog.common.Time

/**
 * A class represents song.
 *
 * @author Vladimir Hromada
 */
data class Song(

    /**
     * UUID
     */
    val uuid: String,

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

