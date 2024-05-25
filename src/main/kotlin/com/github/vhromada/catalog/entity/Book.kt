package com.github.vhromada.catalog.entity

/**
 * A class represents book.
 *
 * @author Vladimir Hromada
 */
data class Book(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Czech name
     */
    val czechName: String,

    /**
     * Original name
     */
    val originalName: String,

    /**
     * Description
     */
    val description: String,

    /**
     * Note
     */
    val note: String?,

    /**
     * Authors
     */
    val authors: List<Author>,

    /**
     * Count of book items
     */
    val itemsCount: Int

)
