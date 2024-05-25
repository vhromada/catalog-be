package com.github.vhromada.catalog.entity

/**
 * A class represents book item.
 *
 * @author Vladimir Hromada
 */
data class BookItem(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Languages
     */
    val languages: List<String>,

    /**
     * Format
     */
    val format: String,

    /**
     * Note
     */
    val note: String?

)
