package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.mapper.impl.GenreMapperImpl
import com.github.vhromada.catalog.mapper.impl.MediumMapperImpl
import com.github.vhromada.catalog.mapper.impl.MovieMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.MediumUtils
import com.github.vhromada.catalog.utils.MovieUtils
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
 * A class represents test for class [MovieMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class MovieMapperTest {

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
     * Instance of [MovieMapper]
     */
    private lateinit var mapper: MovieMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = MovieMapperImpl(
            normalizerService = normalizerService,
            mediumMapper = MediumMapperImpl(),
            genreMapper = GenreMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider),
            uuidProvider = uuidProvider
        )
    }

    /**
     * Test method for [MovieMapper.mapMovie].
     */
    @Test
    fun mapMovie() {
        val movie = MovieUtils.getDomainMovie(index = 1)

        val result = mapper.mapMovie(source = movie)

        MovieUtils.assertMovieDeepEquals(expected = movie, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapMovies].
     */
    @Test
    fun mapMovies() {
        val movie = MovieUtils.getDomainMovie(index = 1)

        val result = mapper.mapMovies(source = listOf(movie))

        MovieUtils.assertMoviesDeepEquals(expected = listOf(movie), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = MovieUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        MovieUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.czechName!!)
        verify(normalizerService).normalize(request.originalName!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = TestConstants.MULTIPLE_NAMES_FILTER

        val result = mapper.mapFilter(source = source)

        MovieUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapStatistics].
     */
    @Test
    fun mapStatistics() {
        val mediaStatistics = MediumUtils.getStatistics()

        val result = mapper.mapStatistics(count = MovieUtils.MOVIES_COUNT.toLong(), mediaStatistics = mediaStatistics)

        assertSoftly {
            it.assertThat(result.count).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(result.mediaCount).isEqualTo(mediaStatistics.count.toInt())
            it.assertThat(result.length).isEqualTo(Time(length = mediaStatistics.length!!.toInt()).toString())
        }
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapStatistics] with null media's length.
     */
    @Test
    fun mapStatisticsNullMediaLength() {
        val mediaStatistics = MediumUtils.getStatistics()
            .copy(length = null)

        val result = mapper.mapStatistics(count = MovieUtils.MOVIES_COUNT.toLong(), mediaStatistics = mediaStatistics)

        assertSoftly {
            it.assertThat(result.count).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(result.mediaCount).isEqualTo(mediaStatistics.count.toInt())
            it.assertThat(result.length).isEqualTo(Time(0).toString())
        }
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
