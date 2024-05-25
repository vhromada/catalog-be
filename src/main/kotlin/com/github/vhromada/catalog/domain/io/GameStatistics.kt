package com.github.vhromada.catalog.domain.io

/**
 * A class represents statistics for games.
 *
 * @author Vladimir Hromada
 */
data class GameStatistics(

    /**
     * Count of programs
     */
    val count: Long,

    /**
     * Count of media
     */
    val mediaCount: Long?

)
