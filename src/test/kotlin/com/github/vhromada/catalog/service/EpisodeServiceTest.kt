package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Episode
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.EpisodeRepository
import com.github.vhromada.catalog.repository.SeasonRepository
import com.github.vhromada.catalog.service.impl.EpisodeServiceImpl
import com.github.vhromada.catalog.utils.EpisodeUtils
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [EpisodeService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class EpisodeServiceTest {

    /**
     * Instance of [EpisodeRepository]
     */
    @Mock
    private lateinit var episodeRepository: EpisodeRepository

    /**
     * Instance of [SeasonRepository]
     */
    @Mock
    private lateinit var seasonRepository: SeasonRepository

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [EpisodeService]
     */
    private lateinit var service: EpisodeService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = EpisodeServiceImpl(episodeRepository = episodeRepository, seasonRepository = seasonRepository, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [EpisodeService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(ShowUtils.getDomainShow(index = 1).seasons.first().episodes)
        whenever(episodeRepository.findAllBySeasonId(id = any(), pageable = any())).thenReturn(page)

        val result = service.search(season = 1, pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(episodeRepository).findAllBySeasonId(id = 1, pageable = pageable)
        verifyNoMoreInteractions(episodeRepository)
        verifyNoInteractions(seasonRepository, uuidProvider)
    }

    /**
     * Test method for [EpisodeService.get] with existing episode.
     */
    @Test
    fun getExisting() {
        val episode = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first()
        whenever(episodeRepository.findByUuid(uuid = any())).thenReturn(Optional.of(episode))

        val result = service.get(uuid = episode.uuid)

        assertThat(result).isEqualTo(episode)
        verify(episodeRepository).findByUuid(uuid = episode.uuid)
        verifyNoMoreInteractions(episodeRepository)
        verifyNoInteractions(seasonRepository, uuidProvider)
    }

    /**
     * Test method for [EpisodeService.get] with not existing episode.
     */
    @Test
    fun getNotExisting() {
        whenever(episodeRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NOT_EXIST")
            .hasMessageContaining("Episode doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(episodeRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(episodeRepository)
        verifyNoInteractions(seasonRepository, uuidProvider)
    }

    /**
     * Test method for [EpisodeService.store].
     */
    @Test
    fun store() {
        val episode = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first()
        whenever(episodeRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(episode = episode)

        assertThat(result).isSameAs(episode)
        verify(episodeRepository).save(episode)
        verifyNoMoreInteractions(episodeRepository)
        verifyNoInteractions(seasonRepository, uuidProvider)
    }

    /**
     * Test method for [EpisodeService.remove].
     */
    @Test
    fun remove() {
        val season = ShowUtils.getDomainShow(index = 1).seasons.first()
        val episode = season.episodes.first()

        service.remove(episode = episode)

        verify(seasonRepository).save(season)
        verifyNoMoreInteractions(seasonRepository)
        verifyNoInteractions(episodeRepository, uuidProvider)
    }

    /**
     * Test method for [EpisodeService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedSeason = ShowUtils.getDomainShow(index = 1).seasons.first()
        val expectedEpisode = expectedSeason.episodes.first()
            .copy(id = 0, uuid = TestConstants.UUID)
        expectedSeason.episodes.add(expectedEpisode)
        val copyArgumentCaptor = argumentCaptor<Episode>()
        whenever(episodeRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Episode
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(episode = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first())

        EpisodeUtils.assertEpisodeDeepEquals(expected = expectedEpisode, actual = result)
        verify(episodeRepository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(episodeRepository, uuidProvider)
        verifyNoInteractions(seasonRepository)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Returns any mock for domain episode.
     *
     * @return any mock for domain episode
     */
    private fun anyDomain(): Episode {
        return any()
    }

}
