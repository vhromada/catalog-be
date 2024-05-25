package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.mapper.impl.GenreMapperImpl
import com.github.vhromada.catalog.mapper.impl.ShowMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.EpisodeUtils
import com.github.vhromada.catalog.utils.SeasonUtils
import com.github.vhromada.catalog.utils.ShowUtils
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
 * A class represents test for class [ShowMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ShowMapperTest {

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
     * Instance of [ShowMapper]
     */
    private lateinit var mapper: ShowMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = ShowMapperImpl(normalizerService = normalizerService, genreMapper = GenreMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider), uuidProvider = uuidProvider)
    }

    /**
     * Test method for [ShowMapper.mapShow].
     */
    @Test
    fun mapShow() {
        val show = ShowUtils.getDomainShow(index = 1)

        val result = mapper.mapShow(source = show)

        ShowUtils.assertShowDeepEquals(expected = show, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ShowMapper.mapShows].
     */
    @Test
    fun mapShows() {
        val show = ShowUtils.getDomainShow(index = 1)

        val result = mapper.mapShows(source = listOf(show))

        ShowUtils.assertShowsDeepEquals(expected = listOf(show), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ShowMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = ShowUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        ShowUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.czechName!!)
        verify(normalizerService).normalize(request.originalName!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ShowMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = TestConstants.MULTIPLE_NAMES_FILTER

        val result = mapper.mapFilter(source = source)

        ShowUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapStatistics].
     */
    @Test
    fun mapStatistics() {
        val episodeStatistics = EpisodeUtils.getStatistics()

        val result = mapper.mapStatistics(showsCount = ShowUtils.SHOWS_COUNT.toLong(), seasonCount = SeasonUtils.SEASONS_COUNT.toLong(), episodeStatistics = episodeStatistics)

        assertSoftly {
            it.assertThat(result.count).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(result.seasonsCount).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(result.episodesCount).isEqualTo(episodeStatistics.count.toInt())
            it.assertThat(result.length).isEqualTo(Time(length = episodeStatistics.length!!.toInt()).toString())
        }
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MovieMapper.mapStatistics] with null episodes' length.
     */
    @Test
    fun mapStatisticsNullEpisodesLength() {
        val episodeStatistics = EpisodeUtils.getStatistics()
            .copy(length = null)

        val result = mapper.mapStatistics(showsCount = ShowUtils.SHOWS_COUNT.toLong(), seasonCount = SeasonUtils.SEASONS_COUNT.toLong(), episodeStatistics = episodeStatistics)

        assertSoftly {
            it.assertThat(result.count).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(result.seasonsCount).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(result.episodesCount).isEqualTo(episodeStatistics.count.toInt())
            it.assertThat(result.length).isEqualTo(Time(0).toString())
        }
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
