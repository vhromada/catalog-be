package com.github.vhromada.catalog.entity.paging

/**
 * A class represents paging info.
 *
 * @author Vladimir Hromada
 */
data class PagingInfo(

    /**
     * Number of page
     */
    val pageNumber: Int,

    /**
     * Count of pages
     */
    val pagesCount: Int

) {

    /**
     * Creates a new instance of [PagingInfo].
     *
     * @param page page of data
     */
    constructor(page: org.springframework.data.domain.Page<*>) : this(
        pageNumber = page.number + 1,
        pagesCount = if (page.totalPages == 0) 1 else page.totalPages
    )

}
