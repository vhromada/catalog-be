package com.github.vhromada.catalog.domain.io

/**
 * A class represents statistics for songs.
 *
 * @author Vladimir Hromada
 */
data class SongStatistics(

    /**
     * Count of songs
     */
    val count: Long,

    /**
     * Length
     */
    val length: Long?

)
