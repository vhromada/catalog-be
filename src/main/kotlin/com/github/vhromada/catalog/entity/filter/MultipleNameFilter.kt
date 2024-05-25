package com.github.vhromada.catalog.entity.filter

/**
 * A class represents filter for multiple names.
 *
 * @author Vladimir Hromada
 */
data class MultipleNameFilter(

    /**
     * Czech name
     */
    val czechName: String? = null,

    /**
     * Original name
     */
    val originalName: String? = null

) : PagingFilter()
