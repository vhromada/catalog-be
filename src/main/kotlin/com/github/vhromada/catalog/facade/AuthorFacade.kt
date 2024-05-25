package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Author
import com.github.vhromada.catalog.entity.AuthorStatistics
import com.github.vhromada.catalog.entity.filter.AuthorFilter
import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for authors.
 *
 * @author Vladimir Hromada
 */
interface AuthorFacade {

    /**
     * Returns page of authors for filter.
     *
     * @param filter filter
     * @return page of authors for filter
     */
    fun search(filter: AuthorFilter): Page<Author>

    /**
     * Returns author.
     *
     * @param uuid UUID
     * @return author
     * @throws InputException if author doesn't exist in data storage
     */
    fun get(uuid: String): Author

    /**
     * Adds author.
     * <br></br>
     * Validation errors:
     *
     *  * First name is null
     *  * First name is empty string
     *  * Last name is null
     *  * Last name is empty string
     *
     * @param request request for changing author
     * @return created author
     * @throws InputException if request for changing author isn't valid
     */
    fun add(request: ChangeAuthorRequest): Author

    /**
     * Updates author.
     * <br></br>
     * Validation errors:
     *
     *  * First name is null
     *  * First name is empty string
     *  * Last name is null
     *  * Last name is empty string
     *  * Author doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing author
     * @return updated author
     * @throws InputException if request for changing author isn't valid
     */
    fun update(uuid: String, request: ChangeAuthorRequest): Author

    /**
     * Removes author.
     *
     * @param uuid UUID
     * @throws InputException if author doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated author
     * @throws InputException if author doesn't exist in data storage
     */
    fun duplicate(uuid: String): Author

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): AuthorStatistics

}
