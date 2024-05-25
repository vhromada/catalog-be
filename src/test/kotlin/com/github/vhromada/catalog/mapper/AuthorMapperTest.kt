package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.AuthorMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.AuthorUtils
import com.github.vhromada.catalog.utils.TestConstants
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
 * A class represents test for class [AuthorMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class AuthorMapperTest {

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
     * Instance of [AuthorMapper]
     */
    private lateinit var mapper: AuthorMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = AuthorMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [AuthorMapper.mapAuthor].
     */
    @Test
    fun mapAuthor() {
        val author = AuthorUtils.getDomainAuthor(index = 1)

        val result = mapper.mapAuthor(source = author)

        AuthorUtils.assertAuthorDeepEquals(expected = author, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [AuthorMapper.mapAuthors].
     */
    @Test
    fun mapAuthors() {
        val author = AuthorUtils.getDomainAuthor(index = 1)

        val result = mapper.mapAuthors(source = listOf(author))

        AuthorUtils.assertAuthorsDeepEquals(expected = listOf(author), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [AuthorMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = AuthorUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        AuthorUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.firstName!!)
        verify(normalizerService).normalize(request.middleName!!)
        verify(normalizerService).normalize(request.lastName!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [AuthorMapper.mapRequest] with null middle name.
     */
    @Test
    fun mapRequestNullMiddleName() {
        val request = AuthorUtils.newRequest()
            .copy(middleName = null)
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        AuthorUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.firstName!!)
        verify(normalizerService).normalize(request.lastName!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [AuthorMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = AuthorUtils.newFilter()

        val result = mapper.mapFilter(source = source)

        AuthorUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
