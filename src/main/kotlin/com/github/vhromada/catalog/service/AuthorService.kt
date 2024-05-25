package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Author
import com.github.vhromada.catalog.domain.filter.AuthorFilter
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for authors.
 *
 * @author Vladimir Hromada
 */
interface AuthorService {

    /**
     * Returns page of authors by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of authors by filter
     */
    fun search(filter: AuthorFilter, pageable: Pageable): Page<Author>

    /**
     * Returns author.
     *
     * @param uuid UUID
     * @return author
     * @throws InputException if author doesn't exist in data storage
     */
    fun get(uuid: String): Author

    /**
     * Stores author.
     *
     * @param author author
     * @return stored author
     */
    fun store(author: Author): Author

    /**
     * Removes author.
     *
     * @param author author
     */
    fun remove(author: Author)

    /**
     * Duplicates author.
     *
     * @param author author
     * @return duplicated author
     */
    fun duplicate(author: Author): Author

    /**
     * Returns count of authors.
     *
     * @return count of authors
     */
    fun getCount(): Long

}
