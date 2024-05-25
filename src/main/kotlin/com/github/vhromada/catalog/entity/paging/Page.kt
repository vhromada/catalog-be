package com.github.vhromada.catalog.entity.paging

/**
 * A class represents page.
 *
 * @param T type of data
 * @author Vladimir Hromada
 */
data class Page<T>(

    /**
     * Data
     */
    val data: List<T>,

    /**
     * Paging info
     */
    val pagingInfo: PagingInfo

) {

    /**
     * Creates a new instance of [Page].
     *
     * @param data data
     * @param page page of data
     */
    constructor(data: List<T>, page: org.springframework.data.domain.Page<*>) : this(
        data = data,
        pagingInfo = PagingInfo(page = page)
    )

}
