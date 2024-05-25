package com.github.vhromada.catalog.entity

/**
 * A class represents statistics for shows.
 *
 * @author Vladimir Hromada
 */
class ShowStatistics(

    /**
     * Count of shows
     */
    val count: Int,

    /**
     * Count of seasons
     */
    val seasonsCount: Int,

    /**
     * Count of episodes
     */
    val episodesCount: Int,

    /**
     * Length
     */
    val length: String

)
