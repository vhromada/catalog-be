package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing show.
 *
 * @author Vladimir Hromada
 */
data class ChangeShowRequest(

    /**
     * Czech name
     */
    val czechName: String?,

    /**
     * Original name
     */
    val originalName: String?,

    /**
     * URL to ČSFD page about show
     */
    val csfd: String?,

    /**
     * IMDB code
     */
    val imdbCode: Int?,

    /**
     * URL to english Wikipedia page about show
     */
    val wikiEn: String?,

    /**
     * URL to czech Wikipedia page about show
     */
    val wikiCz: String?,

    /**
     * Picture
     */
    val picture: String?,

    /**
     * Note
     */
    val note: String?,

    /**
     * Genres
     */
    val genres: List<String?>?

)
