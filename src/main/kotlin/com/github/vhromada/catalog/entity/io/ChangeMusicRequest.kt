package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing music.
 *
 * @author Vladimir Hromada
 */
data class ChangeMusicRequest(

    /**
     * Name
     */
    val name: String?,

    /**
     * URL to english Wikipedia page about music
     */
    val wikiEn: String?,

    /**
     * URL to czech Wikipedia page about music
     */
    val wikiCz: String?,

    /**
     * Count of media
     */
    val mediaCount: Int?,

    /**
     * Note
     */
    val note: String?

)
