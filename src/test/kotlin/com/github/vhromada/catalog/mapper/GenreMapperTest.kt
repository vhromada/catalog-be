package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.GenreMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.GenreUtils
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
 * A class represents test for class [GenreMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class GenreMapperTest {

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
     * Instance of [GenreMapper]
     */
    private lateinit var mapper: GenreMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = GenreMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [GenreMapper.mapGenre].
     */
    @Test
    fun mapGenre() {
        val genre = GenreUtils.getDomainGenre(index = 1)

        val result = mapper.mapGenre(source = genre)

        GenreUtils.assertGenreDeepEquals(expected = genre, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GenreMapper.mapGenres].
     */
    @Test
    fun mapGenres() {
        val genre = GenreUtils.getDomainGenre(index = 1)

        val result = mapper.mapGenres(source = listOf(genre))

        GenreUtils.assertGenresDeepEquals(expected = listOf(genre), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GenreMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = GenreUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        GenreUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.name!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GenreMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = TestConstants.NAME_FILTER

        val result = mapper.mapFilter(source = source)

        GenreUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
