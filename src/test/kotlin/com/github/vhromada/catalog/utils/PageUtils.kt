package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.entity.paging.PagingInfo

/**
 * A class represents utility class for paging.
 *
 * @author Vladimir Hromada
 */
object PageUtils {

    /**
     * Page
     */
    const val PAGE = 2

    /**
     * Limit
     */
    const val LIMIT = 3

    /**
     * Returns page.
     *
     * @param data       data
     * @param pageNumber number of page
     * @param pagesCount count of pages
     * @param T         type of data
     */
    fun <T> getPage(data: List<T>, pageNumber: Int = 1, pagesCount: Int = 1): Page<T> {
        return Page(data = data, pagingInfo = PagingInfo(pageNumber = pageNumber, pagesCount = pagesCount))
    }

}
