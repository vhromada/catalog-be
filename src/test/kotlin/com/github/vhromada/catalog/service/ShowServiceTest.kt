package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Show
import com.github.vhromada.catalog.domain.filter.ShowFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.ShowMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.EpisodeRepository
import com.github.vhromada.catalog.repository.SeasonRepository
import com.github.vhromada.catalog.repository.ShowRepository
import com.github.vhromada.catalog.service.impl.ShowServiceImpl
import com.github.vhromada.catalog.utils.EpisodeUtils
import com.github.vhromada.catalog.utils.SeasonUtils
import com.github.vhromada.catalog.utils.ShowUtils
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
 * A class represents test for class [ShowService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ShowServiceTest {

    /**
     * Instance of [ShowRepository]
     */
    @Mock
    private lateinit var showRepository: ShowRepository

    /**
     * Instance of [SeasonRepository]
     */
    @Mock
    private lateinit var seasonRepository: SeasonRepository

    /**
     * Instance of [EpisodeRepository]
     */
    @Mock
    private lateinit var episodeRepository: EpisodeRepository

    /**
     * Instance of [ShowMapper]
     */
    @Mock
    private lateinit var mapper: ShowMapper

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [ShowService]
     */
    private lateinit var service: ShowService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = ShowServiceImpl(showRepository = showRepository, seasonRepository = seasonRepository, episodeRepository = episodeRepository, mapper = mapper, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [ShowService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(ShowUtils.getDomainShow(index = 1), ShowUtils.getDomainShow(index = 2)))
        whenever(showRepository.findAll(any<Specification<Show>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = ShowFilter(czechName = "czech", originalName = "original"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(showRepository).findAll(any<Specification<Show>>(), eq(pageable))
        verifyNoMoreInteractions(showRepository)
        verifyNoInteractions(seasonRepository, episodeRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [ShowService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(ShowUtils.getDomainShow(index = 1), ShowUtils.getDomainShow(index = 2)))
        whenever(showRepository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = ShowFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(showRepository).findAll(pageable)
        verifyNoMoreInteractions(showRepository)
        verifyNoInteractions(seasonRepository, episodeRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [ShowService.get] with existing show.
     */
    @Test
    fun getExisting() {
        val show = ShowUtils.getDomainShow(index = 1)
        whenever(showRepository.findByUuid(uuid = any())).thenReturn(Optional.of(show))

        val result = service.get(uuid = show.uuid)

        assertThat(result).isEqualTo(show)
        verify(showRepository).findByUuid(uuid = show.uuid)
        verifyNoMoreInteractions(showRepository)
        verifyNoInteractions(seasonRepository, episodeRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [ShowService.get] with not existing show.
     */
    @Test
    fun getNotExisting() {
        whenever(showRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(showRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(showRepository)
        verifyNoInteractions(seasonRepository, episodeRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [ShowService.store].
     */
    @Test
    fun store() {
        val show = ShowUtils.getDomainShow(index = 1)
        whenever(showRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(show = show)

        assertThat(result).isSameAs(show)
        verify(showRepository).save(show)
        verifyNoMoreInteractions(showRepository)
        verifyNoInteractions(seasonRepository, episodeRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [ShowService.remove].
     */
    @Test
    fun remove() {
        val show = ShowUtils.getDomainShow(index = 1)

        service.remove(show = show)

        verify(showRepository).delete(show)
        verifyNoMoreInteractions(showRepository)
        verifyNoInteractions(seasonRepository, episodeRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [ShowService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedShow = ShowUtils.getDomainShow(index = 1)
            .copy(id = 0, uuid = TestConstants.UUID)
        val copyArgumentCaptor = argumentCaptor<Show>()
        val expectedSeasons = expectedShow.seasons.map {
            val expectedSeason = it.copy(id = null, uuid = TestConstants.UUID, show = expectedShow)
            val expectedEpisodes = expectedSeason.episodes.map { episode -> episode.copy(id = null, uuid = TestConstants.UUID, season = expectedSeason) }
            expectedSeason.episodes.clear()
            expectedSeason.episodes.addAll(expectedEpisodes)
            expectedSeason
        }
        expectedShow.seasons.clear()
        expectedShow.seasons.addAll(expectedSeasons)
        whenever(showRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Show
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(show = ShowUtils.getDomainShow(index = 1))

        ShowUtils.assertShowDeepEquals(expected = expectedShow, actual = result)
        verify(showRepository).save(copyArgumentCaptor.capture())
        verify(uuidProvider, times(SeasonUtils.SEASONS_PER_SHOW_COUNT + EpisodeUtils.EPISODES_PER_SHOW_COUNT + 1)).getUuid()
        verifyNoMoreInteractions(showRepository, uuidProvider)
        verifyNoInteractions(seasonRepository, episodeRepository, mapper)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [ShowService.getStatistics].
     */
    @Test
    fun getStatistics() {
        val episodeStatistics = EpisodeUtils.getStatistics()
        val showStatistics = ShowUtils.getStatistics()
        whenever(showRepository.count()).thenReturn(ShowUtils.SHOWS_COUNT.toLong())
        whenever(seasonRepository.count()).thenReturn(SeasonUtils.SEASONS_COUNT.toLong())
        whenever(episodeRepository.getStatistics()).thenReturn(episodeStatistics)
        whenever(mapper.mapStatistics(showsCount = any(), seasonCount = any(), episodeStatistics = any())).thenReturn(showStatistics)

        val result = service.getStatistics()

        assertThat(result).isEqualTo(showStatistics)
        verify(showRepository).count()
        verify(seasonRepository).count()
        verify(episodeRepository).getStatistics()
        verify(mapper).mapStatistics(showsCount = ShowUtils.SHOWS_COUNT.toLong(), seasonCount = SeasonUtils.SEASONS_COUNT.toLong(), episodeStatistics = episodeStatistics)
        verifyNoMoreInteractions(showRepository, seasonRepository, episodeRepository, mapper)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain show.
     *
     * @return any mock for domain show
     */
    private fun anyDomain(): Show {
        return any()
    }

}
