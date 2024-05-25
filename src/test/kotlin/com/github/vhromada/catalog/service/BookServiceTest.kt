package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Book
import com.github.vhromada.catalog.domain.filter.BookFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.BookMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.BookItemRepository
import com.github.vhromada.catalog.repository.BookRepository
import com.github.vhromada.catalog.service.impl.BookServiceImpl
import com.github.vhromada.catalog.utils.BookItemUtils
import com.github.vhromada.catalog.utils.BookUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [BookService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class BookServiceTest {

    /**
     * Instance of [BookRepository]
     */
    @Mock
    private lateinit var bookRepository: BookRepository

    /**
     * Instance of [BookItemRepository]
     */
    @Mock
    private lateinit var bookItemRepository: BookItemRepository

    /**
     * Instance of [BookMapper]
     */
    @Mock
    private lateinit var mapper: BookMapper

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [BookService]
     */
    private lateinit var service: BookService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = BookServiceImpl(bookRepository = bookRepository, bookItemRepository = bookItemRepository, mapper = mapper, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [BookService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(BookUtils.getDomainBook(index = 1), BookUtils.getDomainBook(index = 2)))
        whenever(bookRepository.findAll(any<Specification<Book>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = BookFilter(czechName = "czech", originalName = "original"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(bookRepository).findAll(any<Specification<Book>>(), eq(pageable))
        verifyNoMoreInteractions(bookRepository)
        verifyNoInteractions(bookItemRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [BookService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(BookUtils.getDomainBook(index = 1), BookUtils.getDomainBook(index = 2)))
        whenever(bookRepository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = BookFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(bookRepository).findAll(pageable)
        verifyNoMoreInteractions(bookRepository)
        verifyNoInteractions(bookItemRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [BookService.get] with existing book.
     */
    @Test
    fun getExisting() {
        val book = BookUtils.getDomainBook(index = 1)
        whenever(bookRepository.findByUuid(uuid = any())).thenReturn(Optional.of(book))

        val result = service.get(uuid = book.uuid)

        assertThat(result).isEqualTo(book)
        verify(bookRepository).findByUuid(uuid = book.uuid)
        verifyNoMoreInteractions(bookRepository)
        verifyNoInteractions(bookItemRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [BookService.get] with not existing book.
     */
    @Test
    fun getNotExisting() {
        whenever(bookRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_NOT_EXIST")
            .hasMessageContaining("Book doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(bookRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(bookRepository)
        verifyNoInteractions(bookItemRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [BookService.store].
     */
    @Test
    fun store() {
        val book = BookUtils.getDomainBook(index = 1)
        whenever(bookRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(book = book)

        assertThat(result).isSameAs(book)
        verify(bookRepository).save(book)
        verifyNoMoreInteractions(bookRepository)
        verifyNoInteractions(bookItemRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [BookService.remove].
     */
    @Test
    fun remove() {
        val book = BookUtils.getDomainBook(index = 1)

        service.remove(book = book)

        verify(bookRepository).delete(book)
        verifyNoMoreInteractions(bookRepository)
        verifyNoInteractions(bookItemRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [BookService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedBook = BookUtils.getDomainBook(index = 1)
            .copy(id = 0, uuid = TestConstants.UUID)
        val copyArgumentCaptor = argumentCaptor<Book>()
        val expectedBookItems = expectedBook.items.map {
            it.copy(id = null, uuid = TestConstants.UUID, book = expectedBook)
        }
        expectedBook.items.clear()
        expectedBook.items.addAll(expectedBookItems)
        whenever(bookRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Book
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(book = BookUtils.getDomainBook(index = 1))

        BookUtils.assertBookDeepEquals(expected = expectedBook, actual = result)
        verify(bookRepository).save(copyArgumentCaptor.capture())
        verify(uuidProvider, times(BookItemUtils.BOOK_ITEMS_PER_BOOK_COUNT + 1)).getUuid()
        verifyNoMoreInteractions(bookRepository, uuidProvider)
        verifyNoInteractions(bookItemRepository, mapper)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [BookService.getStatistics].
     */
    @Test
    fun getStatistics() {
        val bookStatistics = BookUtils.getStatistics()
        whenever(bookRepository.count()).thenReturn(BookUtils.BOOKS_COUNT.toLong())
        whenever(bookItemRepository.count()).thenReturn(BookItemUtils.BOOK_ITEMS_COUNT.toLong())
        whenever(mapper.mapStatistics(booksCount = any(), bookItemsCount = any())).thenReturn(bookStatistics)

        val result = service.getStatistics()

        assertThat(result).isEqualTo(bookStatistics)
        verify(bookRepository).count()
        verify(bookItemRepository).count()
        verify(mapper).mapStatistics(booksCount = BookUtils.BOOKS_COUNT.toLong(), bookItemsCount = BookItemUtils.BOOK_ITEMS_COUNT.toLong())
        verifyNoMoreInteractions(bookRepository, bookItemRepository, mapper)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain book.
     *
     * @return any mock for domain book
     */
    private fun anyDomain(): Book {
        return any()
    }

}
