package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing movie.
 *
 * @author Vladimir Hromada
 */
data class ChangeMovieRequest(

    /**
     * Czech name
     */
    val czechName: String?,

    /**
     * Original name
     */
    val originalName: String?,

    /**
     * Year
     */
    val year: Int?,

    /**
     * Languages
     */
    val languages: List<String?>?,

    /**
     * Subtitles
     */
    val subtitles: List<String?>?,

    /**
     * Media
     */
    val media: List<Int?>?,

    /**
     * URL to ČSFD page about movie
     */
    val csfd: String?,

    /**
     * IMDB code
     */
    val imdbCode: Int?,

    /**
     * URL to english Wikipedia page about movie
     */
    val wikiEn: String?,

    /**
     * URL to czech Wikipedia page about movie
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
