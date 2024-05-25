package com.github.vhromada.catalog.entity.filter

/**
 * A class represents filter for names.
 *
 * @author Vladimir Hromada
 */
data class NameFilter(

    /**
     * Name
     */
    val name: String? = null

) : PagingFilter()
