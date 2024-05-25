package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing episode.
 *
 * @author Vladimir Hromada
 */
data class ChangeEpisodeRequest(

    /**
     * Number of episode
     */
    val number: Int?,

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
