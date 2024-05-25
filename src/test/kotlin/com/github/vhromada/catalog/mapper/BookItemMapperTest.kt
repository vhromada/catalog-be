package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.BookItemMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.BookItemUtils
import com.github.vhromada.catalog.utils.BookUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

/**
 * A class represents test for class [BookItemMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class BookItemMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [BookItemMapper]
     */
    private lateinit var mapper: BookItemMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = BookItemMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [BookItemMapper.mapBookItem].
     */
    @Test
    fun mapBookItem() {
        val bookItem = BookUtils.getDomainBook(index = 1).items.first()

        val result = mapper.mapBookItem(source = bookItem)

        BookItemUtils.assertBookItemDeepEquals(expected = bookItem, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [BookItemMapper.mapBookItems].
     */
    @Test
    fun mapBookItems() {
        val bookItem = BookUtils.getDomainBook(index = 1).items.first()

        val result = mapper.mapBookItems(source = listOf(bookItem))

        BookItemUtils.assertBookItemsDeepEquals(expected = listOf(bookItem), actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [BookItemMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = BookItemUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        BookItemUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
