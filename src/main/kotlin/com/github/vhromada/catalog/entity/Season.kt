package com.github.vhromada.catalog.entity

import com.github.vhromada.catalog.common.Time

/**
 * A class represents season.
 *
 * @author Vladimir Hromada
 */
data class Season(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Number of season
     */
    val number: Int,

    /**
     * Starting year
     */
    val startYear: Int,

    /**
     * Ending year
     */
    val endYear: Int,

    /**
     * Language
     */
    val language: String,

    /**
     * Subtitles
     */
    val subtitles: List<String>,

    /**
     * Note
     */
    val note: String?,

    /**
     * Count of episodes
     */
    val episodesCount: Int,

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
