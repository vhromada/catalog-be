package com.github.vhromada.catalog.entity.io

/**
 * A class represents request for changing book.
 *
 * @author Vladimir Hromada
 */
data class ChangeBookRequest(

    /**
     * Czech name
     */
    val czechName: String?,

    /**
     * Original name
     */
    val originalName: String?,

    /**
     * Description
     */
    val description: String?,

    /**
     * Note
     */
    val note: String?,

    /**
     * Authors
     */
    val authors: List<String?>?

)
