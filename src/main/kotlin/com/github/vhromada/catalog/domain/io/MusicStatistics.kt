package com.github.vhromada.catalog.domain.io

/**
 * A class represents statistics for music.
 *
 * @author Vladimir Hromada
 */
data class MusicStatistics(

    /**
     * Count of music
     */
    val count: Long,

    /**
     * Count of media
     */
    val mediaCount: Long?

)
