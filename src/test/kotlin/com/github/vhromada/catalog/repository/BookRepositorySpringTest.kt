package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.BookFilter
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.AuthorUtils
import com.github.vhromada.catalog.utils.BookItemUtils
import com.github.vhromada.catalog.utils.BookUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [BookRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class BookRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [BookRepository]
     */
    @Autowired
    private lateinit var repository: BookRepository

    /**
     * Test method for get books.
     */
    @Test
    fun getBooks() {
        val books = repository.findAll()

        BookUtils.assertDomainBooksDeepEquals(expected = BookUtils.getDomainBooks(), actual = books)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
    }

    /**
     * Test method for get book.
     */
    @Test
    fun getBook() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = repository.findById(i).orElse(null)

            BookUtils.assertBookDeepEquals(expected = BookUtils.getDomainBook(index = i), actual = book)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
    }

    /**
     * Test method for add book.
     */
    @Test
    @DirtiesContext
    fun add() {
        val book = BookUtils.newDomainBook(id = null)
            .copy(authors = mutableListOf(AuthorUtils.getDomainAuthor(entityManager = entityManager, id = 1)!!))
        val expectedBook = BookUtils.newDomainBook(id = BookUtils.BOOKS_COUNT + 1)
            .copy(authors = mutableListOf(AuthorUtils.getDomainAuthor(index = 1)))
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(book)

        assertSoftly {
            it.assertThat(book.id).isEqualTo(BookUtils.BOOKS_COUNT + 1)
            it.assertThat(book.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(book.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(book.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(book.updatedTime).isEqualTo(TestConstants.TIME)
        }
        BookUtils.assertBookDeepEquals(expected = expectedBook, actual = BookUtils.getDomainBook(entityManager = entityManager, id = BookUtils.BOOKS_COUNT + 1))

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT + 1)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
    }

    /**
     * Test method for update book.
     */
    @Test
    fun update() {
        val book = BookUtils.getDomainBook(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedBook = BookUtils.getDomainBook(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(book)

        BookUtils.assertBookDeepEquals(expected = expectedBook, actual = BookUtils.getDomainBook(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
    }

    /**
     * Test method for remove book.
     */
    @Test
    fun remove() {
        repository.delete(BookUtils.getDomainBook(entityManager = entityManager, id = 1)!!)

        assertThat(BookUtils.getDomainBook(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT - 1)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT - BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT)
        }
    }

    /**
     * Test method for remove all books.
     */
    @Test
    fun removeAll() {
        repository.deleteAll()

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(0)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(0)
        }
    }

    /**
     * Test method for count of books.
     */
    @Test
    fun count() {
        val result = repository.count()

        assertThat(result).isEqualTo(BookUtils.BOOKS_COUNT.toLong())

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
    }

    /**
     * Test method for search books by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getDomainBook(index = i)
            val filter = BookFilter(czechName = book.czechName, originalName = book.originalName)

            val result = repository.findAll(filter.toSpecification())

            BookUtils.assertDomainBooksDeepEquals(expected = listOf(book), actual = result.toList())
        }

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
    }

    /**
     * Test method for find book by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getDomainBook(index = i)

            val result = repository.findByUuid(uuid = book.uuid).orElse(null)

            BookUtils.assertBookDeepEquals(expected = BookUtils.getDomainBook(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
    }

}
