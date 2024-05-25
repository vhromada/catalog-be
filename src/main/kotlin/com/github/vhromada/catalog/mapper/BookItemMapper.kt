package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.BookItem
import com.github.vhromada.catalog.entity.io.ChangeBookItemRequest

/**
 * An interface represents mapper for book items.
 *
 * @author Vladimir Hromada
 */
interface BookItemMapper {

    /**
     * Maps book item.
     *
     * @param source book item
     * @return mapped book item
     */
    fun mapBookItem(source: BookItem): com.github.vhromada.catalog.entity.BookItem

    /**
     * Maps list of book items.
     *
     * @param source list of book items
     * @return mapped list of book items
     */
    fun mapBookItems(source: List<BookItem>): List<com.github.vhromada.catalog.entity.BookItem>

    /**
     * Maps request for changing book item.
     *
     * @param source request for changing book item
     * @return mapped book item
     */
    fun mapRequest(source: ChangeBookItemRequest): BookItem

}
