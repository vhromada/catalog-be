package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.BookItem
import com.github.vhromada.catalog.entity.io.ChangeBookItemRequest
import com.github.vhromada.catalog.mapper.BookItemMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for book items.
 *
 * @author Vladimir Hromada
 */
@Component("bookItemMapper")
class BookItemMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : BookItemMapper {

    override fun mapBookItem(source: BookItem): com.github.vhromada.catalog.entity.BookItem {
        return com.github.vhromada.catalog.entity.BookItem(
            uuid = source.uuid,
            languages = source.languages,
            format = source.format,
            note = source.note
        )
    }

    override fun mapBookItems(source: List<BookItem>): List<com.github.vhromada.catalog.entity.BookItem> {
        return source.map { mapBookItem(source = it) }
    }

    override fun mapRequest(source: ChangeBookItemRequest): BookItem {
        return BookItem(
            id = null,
            uuid = uuidProvider.getUuid(),
            languages = source.languages!!.filterNotNull().toMutableList(),
            format = source.format!!,
            note = source.note
        )
    }

}
