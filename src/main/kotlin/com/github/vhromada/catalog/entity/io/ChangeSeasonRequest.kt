package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing season.
 *
 * @author Vladimir Hromada
 */
data class ChangeSeasonRequest(

    /**
     * Number of season
     */
    val number: Int?,

    /**
     * Starting year
     */
    val startYear: Int?,

    /**
     * Ending year
     */
    val endYear: Int?,

    /**
     * Language
     */
    val language: String?,

    /**
     * Subtitles
     */
    val subtitles: List<String?>?,

    /**
     * Note
     */
    val note: String?

)
