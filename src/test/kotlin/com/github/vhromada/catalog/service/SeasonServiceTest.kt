package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.domain.Season
import com.github.vhromada.catalog.repository.SeasonRepository
import com.github.vhromada.catalog.repository.ShowRepository
import com.github.vhromada.catalog.service.impl.SeasonServiceImpl
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
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [SeasonService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class SeasonServiceTest {

    /**
     * Instance of [SeasonRepository]
     */
    @Mock
    private lateinit var seasonRepository: SeasonRepository

    /**
     * Instance of [ShowRepository]
     */
    @Mock
    private lateinit var showRepository: ShowRepository

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [SeasonService]
     */
    private lateinit var service: SeasonService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = SeasonServiceImpl(seasonRepository = seasonRepository, showRepository = showRepository, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [SeasonService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(ShowUtils.getDomainShow(index = 1).seasons)
        whenever(seasonRepository.findAllByShowId(id = any(), pageable = any())).thenReturn(page)

        val result = service.search(show = 1, pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(seasonRepository).findAllByShowId(id = 1, pageable = pageable)
        verifyNoMoreInteractions(seasonRepository)
        verifyNoInteractions(showRepository, uuidProvider)
    }

    /**
     * Test method for [SeasonService.get] with existing season.
     */
    @Test
    fun getExisting() {
        val season = ShowUtils.getDomainShow(index = 1).seasons.first()
        whenever(seasonRepository.findByUuid(uuid = any())).thenReturn(Optional.of(season))

        val result = service.get(uuid = season.uuid)

        assertThat(result).isEqualTo(season)
        verify(seasonRepository).findByUuid(uuid = season.uuid)
        verifyNoMoreInteractions(seasonRepository)
        verifyNoInteractions(showRepository, uuidProvider)
    }

    /**
     * Test method for [SeasonService.get] with not existing season.
     */
    @Test
    fun getNotExisting() {
        whenever(seasonRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(seasonRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(seasonRepository)
        verifyNoInteractions(showRepository, uuidProvider)
    }

    /**
     * Test method for [SeasonService.store].
     */
    @Test
    fun update() {
        val season = ShowUtils.getDomainShow(index = 1).seasons.first()
        whenever(seasonRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(season = season)

        assertThat(result).isSameAs(season)
        verify(seasonRepository).save(season)
        verifyNoMoreInteractions(seasonRepository)
        verifyNoInteractions(showRepository, uuidProvider)
    }

    /**
     * Test method for [SeasonService.remove].
     */
    @Test
    fun remove() {
        val show = ShowUtils.getDomainShow(index = 1)
        val season = show.seasons.first()

        service.remove(season = season)

        verify(showRepository).save(show)
        verifyNoMoreInteractions(showRepository)
        verifyNoInteractions(seasonRepository, uuidProvider)
    }

    /**
     * Test method for [SeasonService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedShow = ShowUtils.getDomainShow(index = 1)
        val expectedSeason = expectedShow.seasons.first()
            .copy(id = 0, uuid = TestConstants.UUID)
        expectedShow.seasons.add(expectedSeason)
        val expectedEpisodes = expectedSeason.episodes.map { it.copy(id = null, uuid = TestConstants.UUID, season = expectedSeason) }
        expectedSeason.episodes.clear()
        expectedSeason.episodes.addAll(expectedEpisodes)
        val copyArgumentCaptor = argumentCaptor<Season>()
        whenever(seasonRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Season
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(season = ShowUtils.getDomainShow(index = 1).seasons.first())

        SeasonUtils.assertSeasonDeepEquals(expected = expectedSeason, actual = result)
        verify(seasonRepository).save(copyArgumentCaptor.capture())
        verify(uuidProvider, times(EpisodeUtils.EPISODES_PER_SEASON_COUNT + 1)).getUuid()
        verifyNoMoreInteractions(seasonRepository, uuidProvider)
        verifyNoInteractions(showRepository)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Returns any mock for domain season.
     *
     * @return any mock for domain season
     */
    private fun anyDomain(): Season {
        return any()
    }

}
