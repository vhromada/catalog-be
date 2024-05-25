package com.github.vhromada.catalog.domain.io

/**
 * A class represents statistics for programs.
 *
 * @author Vladimir Hromada
 */
data class ProgramStatistics(

    /**
     * Count of programs
     */
    val count: Long,

    /**
     * Count of media
     */
    val mediaCount: Long?

)
