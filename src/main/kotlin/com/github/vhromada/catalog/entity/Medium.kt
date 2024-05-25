package com.github.vhromada.catalog.entity

import com.github.vhromada.catalog.common.Time

/**
 * A class represents medium.
 *
 * @author Vladimir Hromada
 */
data class Medium(

    /**
     * Number
     */
    val number: Int,

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
