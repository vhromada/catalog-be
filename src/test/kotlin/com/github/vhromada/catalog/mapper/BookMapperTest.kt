package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.AuthorMapperImpl
import com.github.vhromada.catalog.mapper.impl.BookMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.BookItemUtils
import com.github.vhromada.catalog.utils.BookUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

/**
 * A class represents test for class [BookMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class BookMapperTest {

    /**
     * Instance of [NormalizerService]
     */
    @Mock
    private lateinit var normalizerService: NormalizerService

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [BookMapper]
     */
    private lateinit var mapper: BookMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = BookMapperImpl(normalizerService = normalizerService, authorMapper = AuthorMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider), uuidProvider = uuidProvider)
    }

    /**
     * Test method for [BookMapper.mapBook].
     */
    @Test
    fun mapBook() {
        val book = BookUtils.getDomainBook(index = 1)

        val result = mapper.mapBook(source = book)

        BookUtils.assertBookDeepEquals(expected = book, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [BookMapper.mapBooks].
     */
    @Test
    fun mapBooks() {
        val book = BookUtils.getDomainBook(index = 1)

        val result = mapper.mapBooks(source = listOf(book))

        BookUtils.assertBooksDeepEquals(expected = listOf(book), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [BookMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = BookUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        BookUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.czechName!!)
        verify(normalizerService).normalize(request.originalName!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [BookMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = TestConstants.MULTIPLE_NAMES_FILTER

        val result = mapper.mapFilter(source = source)

        BookUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapStatistics].
     */
    @Test
    fun mapStatistics() {
        val result = mapper.mapStatistics(booksCount = BookUtils.BOOKS_COUNT.toLong(), bookItemsCount = BookItemUtils.BOOK_ITEMS_COUNT.toLong())

        assertSoftly {
            it.assertThat(result.count).isEqualTo(BookUtils.BOOKS_COUNT)
            it.assertThat(result.itemsCount).isEqualTo(BookItemUtils.BOOK_ITEMS_COUNT)
        }
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
