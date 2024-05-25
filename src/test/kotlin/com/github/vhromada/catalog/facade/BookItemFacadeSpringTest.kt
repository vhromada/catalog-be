package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.BookItemUtils
import com.github.vhromada.catalog.utils.BookUtils
import com.github.vhromada.catalog.utils.CheatDataUtils
import com.github.vhromada.catalog.utils.CheatUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [BookItemFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class BookItemFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [BookItemFacade]
     */
    @Autowired
    private lateinit var facade: BookItemFacade

    /**
     * Test method for [BookItemFacade.findAll].
     */
    @Test
    fun findAll() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getDomainBook(index = i)
            val filter = PagingFilter()
            filter.page = 1
            filter.limit = BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT

            val result = facade.findAll(book = book.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            BookItemUtils.assertBookItemListDeepEquals(expected = BookItemUtils.getBookItems(book = i), actual = result.data)
        }

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.findAll] with not existing book.
     */
    @Test
    fun findAllNotExistingBook() {
        assertThatThrownBy { facade.findAll(book = TestConstants.UUID, filter = PagingFilter()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.findAll] with paging.
     */
    @Test
    fun findAllPaging() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getDomainBook(index = i)
            val filter = PagingFilter()
            filter.page = 2
            filter.limit = 1

            val result = facade.findAll(book = book.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT)
            }
            BookItemUtils.assertBookItemsDeepEquals(expected = listOf(book.items[1]), actual = result.data)
        }

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.findAll] with invalid paging.
     */
    @Test
    fun findAllInvalidPaging() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getDomainBook(index = i)
            val filter = PagingFilter()
            filter.page = 2
            filter.limit = BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT

            val result = facade.findAll(book = book.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.data).isEmpty()
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
        }

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getDomainBook(index = i)
            for (bookItem in book.items) {
                val result = facade.get(book = book.uuid, uuid = bookItem.uuid)

                BookItemUtils.assertBookItemDeepEquals(expected = bookItem, actual = result)
            }
        }

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.get] with not existing book.
     */
    @Test
    fun getNotExistingBook() {
        assertThatThrownBy { facade.get(book = TestConstants.UUID, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(book = BookUtils.getDomainBook(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_NOT_EXIST")
            .hasMessageContaining("Book item doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val book = BookUtils.getDomainBook(index = 1)
        val expectedBookItem = BookItemUtils.newBookItem()
        val expectedDomainBookItem = BookItemUtils.newDomainBookItem(id = BookItemUtils.BOOK_ITEMS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedDomainBookItem.book = book

        val result = facade.add(book = book.uuid, request = BookItemUtils.newRequest())
        entityManager.flush()

        BookItemUtils.assertBookItemDeepEquals(expected = expectedBookItem, actual = result, ignoreUuid = true)
        BookItemUtils.assertBookItemDeepEquals(
            expected = expectedDomainBookItem,
            actual = BookItemUtils.getDomainBookItem(entityManager = entityManager, id = BookItemUtils.BOOK_ITEMS_COUNT + 1),
            ignoreUuid = true
        )

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT + 1)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.add] with request with null languages.
     */
    @Test
    fun addNullLanguages() {
        val request = BookItemUtils.newRequest()
            .copy(languages = null)

        assertThatThrownBy { facade.add(book = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_LANGUAGES_NULL")
            .hasMessageContaining("Languages mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.add] with request with languages with null value.
     */
    @Test
    fun addEmptyBadLanguages() {
        val request = BookItemUtils.newRequest()
            .copy(languages = listOf(null))

        assertThatThrownBy { facade.add(book = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_LANGUAGES_CONTAIN_NULL")
            .hasMessageContaining("Languages mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.add] with bookItem with null format.
     */
    @Test
    fun addNullLength() {
        val request = BookItemUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { facade.add(book = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with not existing format.
     */
    @Test
    fun addNotExistingFormat() {
        val request = BookItemUtils.newRequest()
            .copy(format = TestConstants.UUID)

        assertThatThrownBy { facade.add(book = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.add] with not existing book.
     */
    @Test
    fun addNotExistingBook() {
        assertThatThrownBy { facade.add(book = TestConstants.UUID, request = BookItemUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val book = BookUtils.getDomainBook(index = 1)
        val request = BookItemUtils.newRequest()
        val expectedBookItem = BookItemUtils.getBookItem(index = 1)
            .updated()
        val expectedDomainBookItem = book.items.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(book = book.uuid, uuid = book.items.first().uuid, request = request)
        entityManager.flush()

        BookItemUtils.assertBookItemDeepEquals(expected = expectedBookItem, actual = result)
        BookItemUtils.assertBookItemDeepEquals(expected = expectedDomainBookItem, actual = BookItemUtils.getDomainBookItem(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.update] with request with null languages.
     */
    @Test
    fun updateNullLanguages() {
        val request = BookItemUtils.newRequest()
            .copy(languages = null)

        assertThatThrownBy { facade.update(book = BookUtils.getDomainBook(index = 1).uuid, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_LANGUAGES_NULL")
            .hasMessageContaining("Languages mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.update] with request with languages with null value.
     */
    @Test
    fun updateBadLanguages() {
        val request = BookItemUtils.newRequest()
            .copy(languages = listOf(null))

        assertThatThrownBy { facade.update(book = BookUtils.getDomainBook(index = 1).uuid, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_LANGUAGES_CONTAIN_NULL")
            .hasMessageContaining("Languages mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookItemFacade.update] with bookItem with null format.
     */
    @Test
    fun updateNullLength() {
        val request = BookItemUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { facade.update(book = BookUtils.getDomainBook(index = 1).uuid, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.update] with not existing book.
     */
    @Test
    fun updateNotExistingBook() {
        assertThatThrownBy { facade.update(book = TestConstants.UUID, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid, request = BookItemUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with not existing format.
     */
    @Test
    fun updateNotExistingFormat() {
        val request = BookItemUtils.newRequest()
            .copy(format = TestConstants.UUID)

        assertThatThrownBy { facade.update(book = BookUtils.getDomainBook(index = 1).uuid, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(book = BookUtils.getDomainBook(index = 1).uuid, uuid = TestConstants.UUID, request = BookItemUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_NOT_EXIST")
            .hasMessageContaining("Book item doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.remove].
     */
    @Test
    fun remove() {
        val book = BookUtils.getDomainBook(index = 1)

        facade.remove(book = book.uuid, uuid = book.items.first().uuid)
        entityManager.flush()

        assertThat(BookItemUtils.getDomainBookItem(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT - 1)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.remove] with not existing book.
     */
    @Test
    fun removeNotExistingBook() {
        assertThatThrownBy { facade.remove(book = TestConstants.UUID, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(book = BookUtils.getDomainBook(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_NOT_EXIST")
            .hasMessageContaining("Book item doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val book = BookUtils.getDomainBook(index = 1)
        val expectedBookItem = BookItemUtils.getBookItem(index = 1)
        val expectedDomainBookItem = book.items.first()
            .copy(id = BookItemUtils.BOOK_ITEMS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        book.items.add(expectedDomainBookItem)

        val result = facade.duplicate(book = book.uuid, uuid = book.items.first().uuid)
        entityManager.flush()

        BookItemUtils.assertBookItemDeepEquals(expected = expectedBookItem, actual = result, ignoreUuid = true)
        BookItemUtils.assertBookItemDeepEquals(
            expected = expectedDomainBookItem,
            actual = BookItemUtils.getDomainBookItem(entityManager = entityManager, id = BookItemUtils.BOOK_ITEMS_COUNT + 1),
            ignoreUuid = true
        )

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT + 1)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.duplicate] with not existing book.
     */
    @Test
    fun duplicateNotExistingBook() {
        assertThatThrownBy { facade.duplicate(book = TestConstants.UUID, uuid = BookUtils.getDomainBook(index = 1).items.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

    /**
     * Test method for [BookItemFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(book = BookUtils.getDomainBook(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_NOT_EXIST")
            .hasMessageContaining("Book item doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
        }
    }

}
