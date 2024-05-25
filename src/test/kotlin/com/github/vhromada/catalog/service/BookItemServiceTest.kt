package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.BookItem
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.BookItemRepository
import com.github.vhromada.catalog.repository.BookRepository
import com.github.vhromada.catalog.service.impl.BookItemServiceImpl
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [BookItemService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class BookItemServiceTest {

    /**
     * Instance of [BookItemRepository]
     */
    @Mock
    private lateinit var bookItemRepository: BookItemRepository

    /**
     * Instance of [BookRepository]
     */
    @Mock
    private lateinit var bookRepository: BookRepository

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [BookItemService]
     */
    private lateinit var service: BookItemService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = BookItemServiceImpl(bookItemRepository = bookItemRepository, bookRepository = bookRepository, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [BookItemService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(BookUtils.getDomainBook(index = 1).items)
        whenever(bookItemRepository.findAllByBookId(id = any(), pageable = any())).thenReturn(page)

        val result = service.search(book = 1, pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(bookItemRepository).findAllByBookId(id = 1, pageable = pageable)
        verifyNoMoreInteractions(bookItemRepository)
        verifyNoInteractions(bookRepository, uuidProvider)
    }

    /**
     * Test method for [BookItemService.get] with existing bookItem.
     */
    @Test
    fun getExisting() {
        val bookItem = BookUtils.getDomainBook(index = 1).items.first()
        whenever(bookItemRepository.findByUuid(uuid = any())).thenReturn(Optional.of(bookItem))

        val result = service.get(uuid = bookItem.uuid)

        assertThat(result).isEqualTo(bookItem)
        verify(bookItemRepository).findByUuid(uuid = bookItem.uuid)
        verifyNoMoreInteractions(bookItemRepository)
        verifyNoInteractions(bookRepository, uuidProvider)
    }

    /**
     * Test method for [BookItemService.get] with not existing bookItem.
     */
    @Test
    fun getNotExisting() {
        whenever(bookItemRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_NOT_EXIST")
            .hasMessageContaining("Book item doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(bookItemRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(bookItemRepository)
        verifyNoInteractions(bookRepository, uuidProvider)
    }

    /**
     * Test method for [BookItemService.store].
     */
    @Test
    fun store() {
        val bookItem = BookUtils.getDomainBook(index = 1).items.first()
        whenever(bookItemRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(bookItem = bookItem)

        assertThat(result).isSameAs(bookItem)
        verify(bookItemRepository).save(bookItem)
        verifyNoMoreInteractions(bookItemRepository)
        verifyNoInteractions(bookRepository, uuidProvider)
    }

    /**
     * Test method for [BookItemService.remove].
     */
    @Test
    fun remove() {
        val book = BookUtils.getDomainBook(index = 1)
        val bookItem = book.items.first()

        service.remove(bookItem = bookItem)

        verify(bookRepository).save(book)
        verifyNoMoreInteractions(bookRepository)
        verifyNoInteractions(bookItemRepository, uuidProvider)
    }

    /**
     * Test method for [BookItemService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedBook = BookUtils.getDomainBook(index = 1)
        val expectedBookItem = expectedBook.items.first()
            .copy(id = 0, uuid = TestConstants.UUID)
        expectedBook.items.add(expectedBookItem)
        val copyArgumentCaptor = argumentCaptor<BookItem>()
        whenever(bookItemRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as BookItem
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(bookItem = BookUtils.getDomainBook(index = 1).items.first())

        BookItemUtils.assertBookItemDeepEquals(expected = expectedBookItem, actual = result)
        verify(bookItemRepository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(bookItemRepository, uuidProvider)
        verifyNoInteractions(bookRepository)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Returns any mock for domain bookItem.
     *
     * @return any mock for domain bookItem
     */
    private fun anyDomain(): BookItem {
        return any()
    }

}
