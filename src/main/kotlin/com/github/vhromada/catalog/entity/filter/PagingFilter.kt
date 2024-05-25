package com.github.vhromada.catalog.entity.filter

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

/**
 * A class represents filter for paging.
 *
 * @author Vladimir Hromada
 */
open class PagingFilter {

    /**
     * Page
     */
    var page: Int? = null

    /**
     * Limit
     */
    var limit: Int? = null

    /**
     * Returns paging information.
     *
     * @param sort sort
     * @return paging information
     */
    fun toPageable(sort: Sort? = null): Pageable {
        return if (page == null) {
            if (sort == null) {
                Pageable.ofSize(getItemsPerPage())
            } else {
                PageRequest.of(0, getItemsPerPage(), sort)
            }
        } else if (sort == null) {
            PageRequest.of(page!! - 1, getItemsPerPage())
        } else {
            PageRequest.of(page!! - 1, getItemsPerPage(), sort)
        }
    }

    /**
     * Returns count of items per page.
     *
     * @return count of items per page
     */
    private fun getItemsPerPage() = limit ?: 50

}
