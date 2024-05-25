package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Author
import com.github.vhromada.catalog.domain.filter.AuthorFilter
import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest

/**
 * An interface represents mapper for authors.
 *
 * @author Vladimir Hromada
 */
interface AuthorMapper {

    /**
     * Maps author.
     *
     * @param source author
     * @return mapped author
     */
    fun mapAuthor(source: Author): com.github.vhromada.catalog.entity.Author

    /**
     * Maps list of authors.
     *
     * @param source list of authors
     * @return mapped list of authors
     */
    fun mapAuthors(source: List<Author>): List<com.github.vhromada.catalog.entity.Author>

    /**
     * Maps request for changing author.
     *
     * @param source request for changing author
     * @return mapped author
     */
    fun mapRequest(source: ChangeAuthorRequest): Author

    /**
     * Maps filter for authors.
     *
     * @param source filter for authors
     * @return mapped filter for authors
     */
    fun mapFilter(source: com.github.vhromada.catalog.entity.filter.AuthorFilter): AuthorFilter

}
