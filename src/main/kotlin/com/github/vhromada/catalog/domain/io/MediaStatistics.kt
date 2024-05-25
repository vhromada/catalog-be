package com.github.vhromada.catalog.domain.io

/**
 * A class represents statistics for media.
 *
 * @author Vladimir Hromada
 */
data class MediaStatistics(

    /**
     * Count of media
     */
    val count: Long,

    /**
     * Length
     */
    val length: Long?

)
