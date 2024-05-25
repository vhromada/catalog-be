package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.exception.InputException
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
 * A class represents test for class [BookFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class BookFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [BookFacade]
     */
    @Autowired
    private lateinit var facade: BookFacade

    /**
     * Test method for [BookFacade.search].
     */
    @Test
    fun search() {
        val filter = MultipleNameFilter()
        filter.page = 1
        filter.limit = BookUtils.BOOKS_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        BookUtils.assertBookListDeepEquals(expected = BookUtils.getBooks(), actual = result.data)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = MultipleNameFilter()
        filter.page = 2
        filter.limit = 1
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(BookUtils.BOOKS_COUNT)
        }
        BookUtils.assertBookListDeepEquals(expected = listOf(BookUtils.getBook(index = 2)), actual = result.data)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = MultipleNameFilter()
        filter.page = 2
        filter.limit = BookUtils.BOOKS_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getBook(index = i)
            val filter = MultipleNameFilter(czechName = book.czechName, originalName = book.originalName)
            filter.page = 1
            filter.limit = BookUtils.BOOKS_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            BookUtils.assertBookListDeepEquals(expected = listOf(book), actual = result.data)
        }

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..BookUtils.BOOKS_COUNT) {
            val book = BookUtils.getBook(index = i)

            val result = facade.get(uuid = book.uuid)

            BookUtils.assertBookDeepEquals(expected = BookUtils.getBook(index = i), actual = result)
        }

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedBook = BookUtils.newBook()
        val expectedDomainBook = BookUtils.newDomainBook(id = BookUtils.BOOKS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = BookUtils.newRequest())
        entityManager.flush()

        BookUtils.assertBookDeepEquals(expected = expectedBook, actual = result, ignoreUuid = true)
        BookUtils.assertBookDeepEquals(expected = expectedDomainBook, actual = BookUtils.getDomainBook(entityManager = entityManager, id = BookUtils.BOOKS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT + 1)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with null czech name.
     */
    @Test
    fun addNullCzechName() {
        val request = BookUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with empty string as czech name.
     */
    @Test
    fun addEmptyCzechName() {
        val request = BookUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with null original name.
     */
    @Test
    fun addNullOriginalName() {
        val request = BookUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with empty original name.
     */
    @Test
    fun addEmptyOriginalName() {
        val request = BookUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with null description.
     */
    @Test
    fun addNullDescription() {
        val request = BookUtils.newRequest()
            .copy(description = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_DESCRIPTION_NULL")
            .hasMessageContaining("Description mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with empty string as description.
     */
    @Test
    fun addEmptyDescription() {
        val request = BookUtils.newRequest()
            .copy(description = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_DESCRIPTION_EMPTY")
            .hasMessageContaining("Description mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with null authors.
     */
    @Test
    fun addNullAuthors() {
        val request = BookUtils.newRequest()
            .copy(authors = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHORS_NULL")
            .hasMessageContaining("Authors mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with authors with null value.
     */
    @Test
    fun addBadAuthors() {
        val request = BookUtils.newRequest()
            .copy(authors = listOf(null))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHORS_CONTAIN_NULL")
            .hasMessageContaining("Authors mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with request with authors with empty author.
     */
    @Test
    fun addAuthorsBadAuthor() {
        val request = BookUtils.newRequest()
            .copy(authors = listOf(""))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHOR_EMPTY")
            .hasMessageContaining("Author mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.add] with not existing author.
     */
    @Test
    fun addNotExistingAuthor() {
        val request = BookUtils.newRequest()
            .copy(authors = mutableListOf(TestConstants.UUID))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_NOT_EXIST")
            .hasMessageContaining("Author doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = BookUtils.newRequest()
        val expectedBook = BookUtils.getBook(index = 1)
            .updated()
        val expectedDomainBook = BookUtils.getDomainBook(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedDomainBook.items.forEach { it.book = expectedDomainBook }

        val result = facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request)
        entityManager.flush()

        BookUtils.assertBookDeepEquals(expected = expectedBook, actual = result)
        BookUtils.assertBookDeepEquals(expected = expectedDomainBook, actual = BookUtils.getDomainBook(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with null czech name.
     */
    @Test
    fun updateNullCzechName() {
        val request = BookUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with empty string as czech name.
     */
    @Test
    fun updateEmptyCzechName() {
        val request = BookUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with null original name.
     */
    @Test
    fun updateNullOriginalName() {
        val request = BookUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with empty original name.
     */
    @Test
    fun updateEmptyOriginalName() {
        val request = BookUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with null description.
     */
    @Test
    fun updateNullDescription() {
        val request = BookUtils.newRequest()
            .copy(description = null)

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_DESCRIPTION_NULL")
            .hasMessageContaining("Description mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with empty string as description.
     */
    @Test
    fun updateEmptyDescription() {
        val request = BookUtils.newRequest()
            .copy(description = "")

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_DESCRIPTION_EMPTY")
            .hasMessageContaining("Description mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with null authors.
     */
    @Test
    fun updateNullAuthors() {
        val request = BookUtils.newRequest()
            .copy(authors = null)

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHORS_NULL")
            .hasMessageContaining("Authors mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with authors with null value.
     */
    @Test
    fun updateBadAuthors() {
        val request = BookUtils.newRequest()
            .copy(authors = listOf(null))

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHORS_CONTAIN_NULL")
            .hasMessageContaining("Authors mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with request with authors with empty author.
     */
    @Test
    fun updateAuthorsBadAuthor() {
        val request = BookUtils.newRequest()
            .copy(authors = listOf(""))

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHOR_EMPTY")
            .hasMessageContaining("Author mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with not existing author.
     */
    @Test
    fun updateNotExistingAuthor() {
        val request = BookUtils.newRequest()
            .copy(authors = mutableListOf(TestConstants.UUID))

        assertThatThrownBy { facade.update(uuid = BookUtils.getDomainBook(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_NOT_EXIST")
            .hasMessageContaining("Author doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = BookUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.remove].
     */
    @Test
    fun remove() {
        facade.remove(uuid = BookUtils.getBook(index = 1).uuid)
        entityManager.flush()

        assertThat(BookUtils.getDomainBook(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT - 1)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT - BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedBook = BookUtils.getBook(index = 1)
        val expectedDomainBook = BookUtils.getDomainBook(index = 1)
            .copy(id = BookUtils.BOOKS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        val expectedDomainBookItems = expectedDomainBook.items.mapIndexed { index, bookItem ->
            bookItem.copy(id = BookItemUtils.BOOK_ITEMS_COUNT + index + 1, book = expectedDomainBook)
                .fillAudit(audit = AuditUtils.newAudit())
        }
        expectedDomainBook.items.clear()
        expectedDomainBook.items.addAll(expectedDomainBookItems)

        val result = facade.duplicate(uuid = BookUtils.getBook(index = 1).uuid)
        entityManager.flush()

        BookUtils.assertBookDeepEquals(expected = expectedBook, actual = result, ignoreUuid = true)
        BookUtils.assertBookDeepEquals(expected = expectedDomainBook, actual = BookUtils.getDomainBook(entityManager = entityManager, id = BookUtils.BOOKS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT + 1)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT + BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

    /**
     * Test method for [BookFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        val result = facade.getStatistics()

        BookUtils.assertStatisticsDeepEquals(expected = BookUtils.getStatistics(), actual = result)

        assertSoftly {
            it.assertThat(BookUtils.getBooksCount(entityManager = entityManager)).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(BookItemUtils.getBookItemsCount(entityManager = entityManager)).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
            it.assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
    }

}
