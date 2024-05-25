package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [BookItemRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class BookItemRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [BookItemRepository]
     */
    @Autowired
    private lateinit var repository: BookItemRepository

    /**
     * Test method for get book item.
     */
    @Test
    fun getBookItem() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            for (j in 1..BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT) {
                val id = (i - 1) * BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT + j

                val bookItem = repository.findById(id).orElse(null)

                BookItemUtils.assertBookItemDeepEquals(expected = BookUtils.getDomainBook(index = i).items[j - 1], actual = bookItem)
            }
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for add book item.
     */
    @Test
    @DirtiesContext
    fun add() {
        val bookItem = BookItemUtils.newDomainBookItem(id = null)
        bookItem.book = BookUtils.getDomainBook(entityManager = entityManager, id = 1)
        val expectedBookItem = BookItemUtils.newDomainBookItem(id = BookItemUtils.BOOK_ITEMS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedBookItem.book = BookUtils.getDomainBook(entityManager = entityManager, id = 1)

        repository.saveAndFlush(bookItem)

        assertSoftly {
            it.assertThat(bookItem.id).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT + 1)
            it.assertThat(bookItem.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(bookItem.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(bookItem.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(bookItem.updatedTime).isEqualTo(TestConstants.TIME)
        }
        BookItemUtils.assertBookItemDeepEquals(expected = expectedBookItem, actual = BookItemUtils.getDomainBookItem(entityManager = entityManager, id = BookItemUtils.BOOK_ITEMS_COUNT + 1))

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT + 1)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for update book item.
     */
    @Test
    fun update() {
        val bookItem = BookItemUtils.getDomainBookItem(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedBookItem = BookUtils.getDomainBook(index = 1).items.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(bookItem)

        BookItemUtils.assertBookItemDeepEquals(expected = expectedBookItem, actual = BookItemUtils.getDomainBookItem(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for get book items by book ID.
     */
    @Test
    fun findAllByBookId() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val bookItems = repository.findAllByBookId(id = i, pageable = Pageable.ofSize(BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT))

            assertSoftly {
                it.assertThat(bookItems.number).isEqualTo(0)
                it.assertThat(bookItems.totalPages).isEqualTo(1)
                it.assertThat(bookItems.totalElements).isEqualTo(BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT.toLong())
            }
            BookItemUtils.assertDomainBookItemsDeepEquals(expected = BookUtils.getDomainBook(index = i).items, actual = bookItems.content)
        }

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for get book items by book ID with invalid paging.
     */
    @Test
    fun findAllByBookIdInvalidPaging() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val bookItems = repository.findAllByBookId(id = i, pageable = PageRequest.of(2, BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT))

            assertSoftly {
                it.assertThat(bookItems.content).isEmpty()
                it.assertThat(bookItems.number).isEqualTo(2)
                it.assertThat(bookItems.totalPages).isEqualTo(1)
                it.assertThat(bookItems.totalElements).isEqualTo(BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT.toLong())
            }
        }

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for get book items by book ID with not existing book ID.
     */
    @Test
    fun findAllByBookIdNotExistingBookId() {
        val bookItems = repository.findAllByBookId(id = Int.MAX_VALUE, pageable = Pageable.ofSize(BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT))

        assertSoftly {
            it.assertThat(bookItems.content).isEmpty()
            it.assertThat(bookItems.number).isEqualTo(0)
            it.assertThat(bookItems.totalPages).isEqualTo(0)
            it.assertThat(bookItems.totalElements).isEqualTo(0L)
        }

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for find book item by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getDomainBook(index = i)
            for (bookItem in book.items) {
                val result = repository.findByUuid(uuid = bookItem.uuid).orElse(null)

                BookItemUtils.assertBookItemDeepEquals(expected = bookItem, actual = result)
            }
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

}
