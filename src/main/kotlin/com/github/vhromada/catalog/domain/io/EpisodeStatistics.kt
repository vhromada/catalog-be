package com.github.vhromada.catalog.domain.io

/**
 * A class represents statistics for episodes.
 *
 * @author Vladimir Hromada
 */
data class EpisodeStatistics(

    /**
     * Count of episodes
     */
    val count: Long,

    /**
     * Length
     */
    val length: Long?

)
