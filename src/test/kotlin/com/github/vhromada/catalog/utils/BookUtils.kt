package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.filter.BookFilter
import com.github.vhromada.catalog.entity.Author
import com.github.vhromada.catalog.entity.Book
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates book fields.
 *
 * @return updated book
 */
fun com.github.vhromada.catalog.domain.Book.updated(): com.github.vhromada.catalog.domain.Book {
    czechName = "czName"
    normalizedCzechName = "czName"
    originalName = "origName"
    normalizedOriginalName = "origName"
    description = "description"
    note = "Note"
    return this
}

/**
 * Updates book fields.
 *
 * @return updated book
 */
fun Book.updated(): Book {
    return copy(
        czechName = "czName",
        originalName = "origName",
        description = "description",
        note = "Note"
    )
}

/**
 * A class represents utility class for books.
 *
 * @author Vladimir Hromada
 */
object BookUtils {

    /**
     * Count of books
     */
    const val BOOKS_COUNT = 3

    /**
     * Returns books.
     *
     * @return books
     */
    fun getDomainBooks(): List<com.github.vhromada.catalog.domain.Book> {
        val books = mutableListOf<com.github.vhromada.catalog.domain.Book>()
        for (i in 1..BOOKS_COUNT) {
            books.add(getDomainBook(index = i))
        }

        return books
    }

    /**
     * Returns books.
     *
     * @return books
     */
    fun getBooks(): List<Book> {
        val books = mutableListOf<Book>()
        for (i in 1..BOOKS_COUNT) {
            books.add(getBook(index = i))
        }

        return books
    }

    /**
     * Returns book for index.
     *
     * @param index index
     * @return book for index
     */
    fun getDomainBook(index: Int): com.github.vhromada.catalog.domain.Book {
        val czechName = "Book $index czech name"
        val originalName = "Book $index original name"
        val book = com.github.vhromada.catalog.domain.Book(
            id = index,
            uuid = getUuid(index = index),
            czechName = czechName,
            normalizedCzechName = czechName,
            originalName = originalName,
            normalizedOriginalName = originalName,
            description = "Book $index description",
            note = if (index != 2) "Book $index note" else null,
            authors = getDomainAuthors(index = index),
            items = BookItemUtils.getDomainBookItems(book = index)
        ).fillAudit(audit = AuditUtils.getAudit())
        book.items.forEach { it.book = book }
        return book
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "bf532dac-4996-45b0-80e0-d6dc32c7118c"
            2 -> "22b8589a-77c9-4910-b824-4672c568b159"
            3 -> "1720d1a1-294b-4f3c-9475-0204842b85af"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns authors for index.
     *
     * @param index index
     * @return authors for index
     */
    private fun getDomainAuthors(index: Int): MutableList<com.github.vhromada.catalog.domain.Author> {
        val authors = mutableListOf<com.github.vhromada.catalog.domain.Author>()
        authors.add(AuthorUtils.getDomainAuthor(index = index))
        if (index == 2) {
            authors.add(AuthorUtils.getDomainAuthor(index = 3))
        }
        return authors
    }

    /**
     * Returns book.
     *
     * @param entityManager entity manager
     * @param id            book ID
     * @return book
     */
    fun getDomainBook(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Book? {
        return entityManager.find(com.github.vhromada.catalog.domain.Book::class.java, id)
    }

    /**
     * Returns book for index.
     *
     * @param index index
     * @return book for index
     */
    fun getBook(index: Int): Book {
        return Book(
            uuid = getUuid(index = index),
            czechName = "Book $index czech name",
            originalName = "Book $index original name",
            description = "Book $index description",
            note = if (index != 2) "Book $index note" else null,
            authors = getAuthors(index = index),
            itemsCount = BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT
        )
    }

    /**
     * Returns authors for index.
     *
     * @param index index
     * @return authors for index
     */
    private fun getAuthors(index: Int): MutableList<Author> {
        val authors = mutableListOf<Author>()
        authors.add(AuthorUtils.getAuthor(index = index))
        if (index == 2) {
            authors.add(AuthorUtils.getAuthor(index = 3))
        }
        return authors
    }

    /**
     * Returns statistics for books.
     *
     * @return statistics for books
     */
    fun getStatistics(): BookStatistics {
        return BookStatistics(count = BOOKS_COUNT, itemsCount = BookItemUtils.BOOK_ITEMS_COUNT)
    }

    /**
     * Returns count of books.
     *
     * @param entityManager entity manager
     * @return count of books
     */
    fun getBooksCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(b.id) FROM Book b", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns book.
     *
     * @param id ID
     * @return book
     */
    fun newDomainBook(id: Int?): com.github.vhromada.catalog.domain.Book {
        return com.github.vhromada.catalog.domain.Book(
            id = id,
            uuid = TestConstants.UUID,
            czechName = "",
            normalizedCzechName = "",
            originalName = "",
            normalizedOriginalName = "",
            description = "",
            note = null,
            authors = mutableListOf(AuthorUtils.getDomainAuthor(index = 1)),
            items = mutableListOf()
        ).updated()
    }

    /**
     * Returns book.
     *
     * @return book
     */
    fun newBook(): Book {
        return Book(
            uuid = TestConstants.UUID,
            czechName = "",
            originalName = "",
            description = "",
            note = null,
            authors = listOf(AuthorUtils.getAuthor(index = 1)),
            itemsCount = 0
        ).updated()
    }

    /**
     * Returns request for changing book.
     *
     * @return request for changing book
     */
    fun newRequest(): ChangeBookRequest {
        return ChangeBookRequest(
            czechName = "czName",
            originalName = "origName",
            description = "description",
            note = "Note",
            authors = listOf(AuthorUtils.getAuthor(index = 1).uuid)
        )
    }

    /**
     * Asserts list of books deep equals.
     *
     * @param expected expected list of books
     * @param actual   actual list of books
     */
    fun assertDomainBooksDeepEquals(expected: List<com.github.vhromada.catalog.domain.Book>, actual: List<com.github.vhromada.catalog.domain.Book>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertBookDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts book deep equals.
     *
     * @param expected       expected book
     * @param actual         actual book
     * @param checkBookItems true if book items should be checked
     * @param ignoreUuid     true if UUID should be ignored
     */
    fun assertBookDeepEquals(expected: com.github.vhromada.catalog.domain.Book?, actual: com.github.vhromada.catalog.domain.Book?, checkBookItems: Boolean = true, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.czechName).isEqualTo(expected.czechName)
                it.assertThat(actual.normalizedCzechName).isEqualTo(expected.normalizedCzechName)
                it.assertThat(actual.originalName).isEqualTo(expected.originalName)
                it.assertThat(actual.normalizedOriginalName).isEqualTo(expected.normalizedOriginalName)
                it.assertThat(actual.description).isEqualTo(expected.description)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            AuthorUtils.assertDomainAuthorsDeepEquals(expected = expected.authors, actual = actual.authors)
            if (checkBookItems) {
                BookItemUtils.assertDomainBookItemsDeepEquals(expected = expected.items, actual = actual.items, ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts list of books deep equals.
     *
     * @param expected expected list of books
     * @param actual   actual list of books
     */
    fun assertBooksDeepEquals(expected: List<com.github.vhromada.catalog.domain.Book>, actual: List<Book>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertBookDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts book deep equals.
     *
     * @param expected   expected book
     * @param actual     actual book
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertBookDeepEquals(expected: com.github.vhromada.catalog.domain.Book, actual: Book, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.description).isEqualTo(expected.description)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
        AuthorUtils.assertAuthorsDeepEquals(expected = expected.authors, actual = actual.authors)
    }

    /**
     * Asserts list of books deep equals.
     *
     * @param expected expected list of books
     * @param actual   actual list of books
     */
    fun assertBookListDeepEquals(expected: List<Book>, actual: List<Book>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertBookDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts book deep equals.
     *
     * @param expected   expected book
     * @param actual     actual book
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertBookDeepEquals(expected: Book, actual: Book, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.description).isEqualTo(expected.description)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
        AuthorUtils.assertAuthorListDeepEquals(expected = expected.authors, actual = actual.authors)
    }

    /**
     * Asserts request and book deep equals.
     *
     * @param expected expected request for changing book
     * @param actual   actual book
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeBookRequest, actual: com.github.vhromada.catalog.domain.Book, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.normalizedCzechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.normalizedOriginalName).isEqualTo(expected.originalName)
            it.assertThat(actual.description).isEqualTo(expected.description)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.authors).isEmpty()
            it.assertThat(actual.items).isEmpty()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

    /**
     * Asserts filter deep equals.
     *
     * @param expected expected filter
     * @param actual   actual filter
     */
    fun assertFilterDeepEquals(expected: MultipleNameFilter, actual: BookFilter) {
        assertSoftly {
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
        }
    }

    /**
     * Asserts statistics for games deep equals.
     *
     * @param expected expected statistics for games
     * @param actual   actual statistics for games
     */
    fun assertStatisticsDeepEquals(expected: BookStatistics, actual: BookStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.itemsCount).isEqualTo(expected.itemsCount)
        }
    }

}
