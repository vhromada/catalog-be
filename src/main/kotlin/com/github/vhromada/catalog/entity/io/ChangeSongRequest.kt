package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing song.
 *
 * @author Vladimir Hromada
 */
data class ChangeSongRequest(

    /**
     * Name
     */
    val name: String?,

    /**
     * Length
     */
    val length: Int?,

    /**
     * Note
     */
    val note: String?

)
