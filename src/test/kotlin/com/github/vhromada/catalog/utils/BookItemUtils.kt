package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.BookItem
import com.github.vhromada.catalog.entity.io.ChangeBookItemRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates bookItem fields.
 *
 * @return updated bookItem
 */
fun com.github.vhromada.catalog.domain.BookItem.updated(): com.github.vhromada.catalog.domain.BookItem {
    languages.clear()
    languages.add("CZ")
    format = "PDF"
    note = "Note"
    return this
}

/**
 * Updates bookItem fields.
 *
 * @return updated bookItem
 */
fun BookItem.updated(): BookItem {
    return copy(
        languages = listOf("CZ"),
        format = "PDF",
        note = "Note"
    )
}

/**
 * A class represents utility class for book items.
 *
 * @author Vladimir Hromada
 */
object BookItemUtils {

    /**
     * Count of book items
     */
    const val BOOK_ITEMS_COUNT = 9

    /**
     * Count of book items in book
     */
    const val BOOK_ITEMS_PER_BOOK_COUNT = 3

    /**
     * Returns book items.
     *
     * @param book book ID
     * @return bookItems
     */
    fun getDomainBookItems(book: Int): MutableList<com.github.vhromada.catalog.domain.BookItem> {
        val bookItems = mutableListOf<com.github.vhromada.catalog.domain.BookItem>()
        for (i in 1..BOOK_ITEMS_PER_BOOK_COUNT) {
            bookItems.add(getDomainBookItem(bookIndex = book, bookItemIndex = i))
        }

        return bookItems
    }

    /**
     * Returns book items.
     *
     * @param book book ID
     * @return book items
     */
    fun getBookItems(book: Int): List<BookItem> {
        val bookItems = mutableListOf<BookItem>()
        for (i in 1..BOOK_ITEMS_PER_BOOK_COUNT) {
            bookItems.add(getBookItem(bookIndex = book, bookItemIndex = i))
        }

        return bookItems
    }

    /**
     * Returns book item for indexes.
     *
     * @param bookIndex     book index
     * @param bookItemIndex book item index
     * @return book item for indexes
     */
    private fun getDomainBookItem(bookIndex: Int, bookItemIndex: Int): com.github.vhromada.catalog.domain.BookItem {
        return com.github.vhromada.catalog.domain.BookItem(
            id = (bookIndex - 1) * BOOK_ITEMS_PER_BOOK_COUNT + bookItemIndex,
            uuid = getUuid(index = (bookIndex - 1) * BOOK_ITEMS_PER_BOOK_COUNT + bookItemIndex),
            languages = getLanguages(bookItemIndex = bookItemIndex),
            format = getFormat(bookItemIndex = bookItemIndex),
            note = if (bookItemIndex != 1) "Book $bookIndex Book item $bookItemIndex note" else null
        ).fillAudit(audit = AuditUtils.getAudit())
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "e3441446-a530-456f-9eb9-b881b584fd02"
            2 -> "d5f9c1a4-756e-4677-bc67-e43140c9e6c5"
            3 -> "d6374376-fb43-4e07-981c-b53abaee3cd4"
            4 -> "6c7d1be7-2840-4bc9-85aa-03e6dc080fff"
            5 -> "c690da86-0577-4d2e-854b-4b921aad5a7f"
            6 -> "6e783bf8-00d4-4cd0-ab97-6b51547be5de"
            7 -> "64fc061e-0d25-4b88-b73c-c161db408815"
            8 -> "f1435a87-cdd0-4558-86fd-fdc015e7c41d"
            9 -> "6673617c-3c39-422b-8084-ea68f1dd42fa"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns languages for book item index.
     *
     * @param bookItemIndex book item index
     * @return languages for book item index
     */
    private fun getLanguages(bookItemIndex: Int): MutableList<String> {
        val languages = mutableListOf<String>()
        when (bookItemIndex) {
            1 -> {
                languages.add("CZ")
            }

            2 -> {
                languages.add("EN")
            }

            3 -> {
                languages.add("CZ")
                languages.add("EN")
            }

            else -> throw IllegalArgumentException("Bad book item index")
        }
        return languages
    }

    /**
     * Returns format for book item index.
     *
     * @param bookItemIndex book item index
     * @return format for book item index
     */
    private fun getFormat(bookItemIndex: Int): String {
        return when (bookItemIndex) {
            1 -> "PAPER"
            2 -> "TXT"
            3 -> "PDF"
            else -> throw IllegalArgumentException("Bad book item index")
        }
    }

    /**
     * Returns book item.
     *
     * @param entityManager entity manager
     * @param id            book item ID
     * @return bookItem
     */
    fun getDomainBookItem(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.BookItem? {
        return entityManager.find(com.github.vhromada.catalog.domain.BookItem::class.java, id)
    }

    /**
     * Returns book item for index.
     *
     * @param index bookItem index
     * @return bookItem for index
     */
    fun getBookItem(index: Int): BookItem {
        val bookNumber = (index - 1) / BOOK_ITEMS_PER_BOOK_COUNT + 1
        val bookItemNumber = (index - 1) % BOOK_ITEMS_PER_BOOK_COUNT + 1

        return getBookItem(bookIndex = bookNumber, bookItemIndex = bookItemNumber)
    }

    /**
     * Returns book item for indexes.
     *
     * @param bookIndex     book index
     * @param bookItemIndex book item index
     * @return bookItem for indexes
     */
    private fun getBookItem(bookIndex: Int, bookItemIndex: Int): BookItem {
        return BookItem(
            uuid = getUuid(index = (bookIndex - 1) * BOOK_ITEMS_PER_BOOK_COUNT + bookItemIndex),
            languages = getLanguages(bookItemIndex = bookItemIndex),
            format = getFormat(bookItemIndex = bookItemIndex),
            note = if (bookItemIndex != 1) "Book $bookIndex Book item $bookItemIndex note" else null
        )
    }

    /**
     * Returns count of book items.
     *
     * @param entityManager entity manager
     * @return count of book items
     */
    fun getBookItemsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(b.id) FROM BookItem b", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns book item.
     *
     * @param id ID
     * @return book item
     */
    fun newDomainBookItem(id: Int?): com.github.vhromada.catalog.domain.BookItem {
        return com.github.vhromada.catalog.domain.BookItem(
            id = id,
            uuid = TestConstants.UUID,
            languages = mutableListOf(),
            format = "PDF",
            note = null
        ).updated()
    }

    /**
     * Returns book item.
     *
     * @return book item
     */
    fun newBookItem(): BookItem {
        return BookItem(
            uuid = TestConstants.UUID,
            languages = emptyList(),
            format = "",
            note = null
        ).updated()
    }

    /**
     * Returns request for changing book item.
     *
     * @return request for changing book item
     */
    fun newRequest(): ChangeBookItemRequest {
        return ChangeBookItemRequest(
            languages = listOf("CZ"),
            format = "PDF",
            note = "Note"
        )
    }

    /**
     * Asserts list of book items deep equals.
     *
     * @param expected   expected list of book items
     * @param actual     actual list of book items
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertDomainBookItemsDeepEquals(expected: List<com.github.vhromada.catalog.domain.BookItem>, actual: List<com.github.vhromada.catalog.domain.BookItem>, ignoreUuid: Boolean = false) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertBookItemDeepEquals(expected = expected[i], actual = actual[i], ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts book item deep equals.
     *
     * @param expected   expected book item
     * @param actual     actual book item
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertBookItemDeepEquals(expected: com.github.vhromada.catalog.domain.BookItem?, actual: com.github.vhromada.catalog.domain.BookItem?, ignoreUuid: Boolean = false) {
        if (expected == null) {
            assertThat(actual).isNull()
        } else {
            assertThat(actual).isNotNull
            assertSoftly {
                it.assertThat(actual!!.id).isEqualTo(expected.id)
                if (ignoreUuid) {
                    it.assertThat(actual.uuid).isNotEmpty
                } else {
                    it.assertThat(actual.uuid).isEqualTo(expected.uuid)
                }
                it.assertThat(actual.languages)
                    .hasSameSizeAs(expected.languages)
                    .hasSameElementsAs(expected.languages)
                it.assertThat(actual.format).isEqualTo(expected.format)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            if (expected.book != null) {
                assertThat(actual.book).isNotNull
                assertThat(actual.book!!.items).hasSameSizeAs(expected.book!!.items)
                BookUtils.assertBookDeepEquals(expected = expected.book!!, actual = actual.book!!, checkBookItems = false, ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts list of book items deep equals.
     *
     * @param expected expected list of book items
     * @param actual   actual list of book items
     */
    fun assertBookItemsDeepEquals(expected: List<com.github.vhromada.catalog.domain.BookItem>, actual: List<BookItem>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertBookItemDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts book item deep equals.
     *
     * @param expected   expected book item
     * @param actual     actual book item
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertBookItemDeepEquals(expected: com.github.vhromada.catalog.domain.BookItem, actual: BookItem, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.languages)
                .hasSameSizeAs(expected.languages)
                .hasSameElementsAs(expected.languages)
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts list of book items deep equals.
     *
     * @param expected expected list of book items
     * @param actual   actual list of book items
     */
    fun assertBookItemListDeepEquals(expected: List<BookItem>, actual: List<BookItem>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertBookItemDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts book item deep equals.
     *
     * @param expected   expected book item
     * @param actual     actual book item
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertBookItemDeepEquals(expected: BookItem, actual: BookItem, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.languages)
                .hasSameSizeAs(expected.languages)
                .hasSameElementsAs(expected.languages)
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts request and book item deep equals.
     *
     * @param expected expected request for changing book item
     * @param actual   actual book item
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeBookItemRequest, actual: com.github.vhromada.catalog.domain.BookItem, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.languages)
                .hasSameSizeAs(expected.languages)
                .hasSameElementsAs(expected.languages)
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.book).isNull()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

}
