package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Book
import com.github.vhromada.catalog.domain.filter.BookFilter
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import com.github.vhromada.catalog.mapper.AuthorMapper
import com.github.vhromada.catalog.mapper.BookMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for books.
 *
 * @author Vladimir Hromada
 */
@Component("bookMapper")
class BookMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Mapper for authors
     */
    private val authorMapper: AuthorMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : BookMapper {

    override fun mapBook(source: Book): com.github.vhromada.catalog.entity.Book {
        return com.github.vhromada.catalog.entity.Book(
            uuid = source.uuid,
            czechName = source.czechName,
            originalName = source.originalName,
            description = source.description,
            note = source.note,
            authors = authorMapper.mapAuthors(source = source.authors),
            itemsCount = source.items.size
        )
    }

    override fun mapBooks(source: List<Book>): List<com.github.vhromada.catalog.entity.Book> {
        return source.map { mapBook(source = it) }
    }

    override fun mapRequest(source: ChangeBookRequest): Book {
        return Book(
            id = null,
            uuid = uuidProvider.getUuid(),
            czechName = source.czechName!!,
            normalizedCzechName = normalizerService.normalize(source = source.czechName),
            originalName = source.originalName!!,
            normalizedOriginalName = normalizerService.normalize(source = source.originalName),
            description = source.description!!,
            note = source.note,
            authors = mutableListOf(),
            items = mutableListOf()
        )
    }

    override fun mapFilter(source: MultipleNameFilter): BookFilter {
        return BookFilter(
            czechName = source.czechName,
            originalName = source.originalName
        )
    }

    override fun mapStatistics(booksCount: Long, bookItemsCount: Long): BookStatistics {
        return BookStatistics(
            count = booksCount.toInt(),
            itemsCount = bookItemsCount.toInt()
        )
    }

}
