package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.EpisodeUtils
import com.github.vhromada.catalog.utils.SeasonUtils
import com.github.vhromada.catalog.utils.ShowUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [EpisodeRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class EpisodeRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [EpisodeRepository]
     */
    @Autowired
    private lateinit var repository: EpisodeRepository

    /**
     * Test method for get episode.
     */
    @Test
    fun getEpisode() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            for (j in 1..SeasonUtils.SEASONS_PER_SHOW_COUNT) {
                for (k in 1..EpisodeUtils.EPISODES_PER_SEASON_COUNT) {
                    val id = (i - 1) * EpisodeUtils.EPISODES_PER_SHOW_COUNT + (j - 1) * EpisodeUtils.EPISODES_PER_SEASON_COUNT + k

                    val episode = repository.findById(id).orElse(null)

                    EpisodeUtils.assertEpisodeDeepEquals(expected = ShowUtils.getDomainShow(index = i).seasons[j - 1].episodes[k - 1], actual = episode)
                }
            }
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for add episode.
     */
    @Test
    @DirtiesContext
    fun add() {
        val episode = EpisodeUtils.newDomainEpisode(id = null)
        episode.season = SeasonUtils.getDomainSeason(entityManager = entityManager, id = 1)
        val expectedEpisode = EpisodeUtils.newDomainEpisode(id = EpisodeUtils.EPISODES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedEpisode.season = SeasonUtils.getDomainSeason(entityManager = entityManager, id = 1)

        repository.saveAndFlush(episode)

        assertSoftly {
            it.assertThat(episode.id).isEqualTo(EpisodeUtils.EPISODES_COUNT + 1)
            it.assertThat(episode.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(episode.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(episode.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(episode.updatedTime).isEqualTo(TestConstants.TIME)
        }
        EpisodeUtils.assertEpisodeDeepEquals(expected = expectedEpisode, actual = EpisodeUtils.getDomainEpisode(entityManager = entityManager, id = EpisodeUtils.EPISODES_COUNT + 1))

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT + 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for update episode.
     */
    @Test
    fun update() {
        val episode = EpisodeUtils.getDomainEpisode(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedEpisode = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(episode)

        EpisodeUtils.assertEpisodeDeepEquals(expected = expectedEpisode, actual = EpisodeUtils.getDomainEpisode(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for get episodes by season ID.
     */
    @Test
    fun findAllBySeasonId() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                val episodes = repository.findAllBySeasonId(id = season.id!!, pageable = Pageable.ofSize(EpisodeUtils.EPISODES_PER_SEASON_COUNT))

                assertSoftly {
                    it.assertThat(episodes.number).isEqualTo(0)
                    it.assertThat(episodes.totalPages).isEqualTo(1)
                    it.assertThat(episodes.totalElements).isEqualTo(EpisodeUtils.EPISODES_PER_SEASON_COUNT.toLong())
                }
                EpisodeUtils.assertDomainEpisodesDeepEquals(expected = season.episodes, actual = episodes.content)
            }
        }

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for get episodes by season ID with invalid paging.
     */
    @Test
    fun findAllBySeasonIdInvalidPaging() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                val episodes = repository.findAllBySeasonId(id = season.id!!, pageable = PageRequest.of(2, EpisodeUtils.EPISODES_PER_SEASON_COUNT))

                assertSoftly {
                    it.assertThat(episodes.content).isEmpty()
                    it.assertThat(episodes.number).isEqualTo(2)
                    it.assertThat(episodes.totalPages).isEqualTo(1)
                    it.assertThat(episodes.totalElements).isEqualTo(EpisodeUtils.EPISODES_PER_SEASON_COUNT.toLong())
                }
            }
        }

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for get episodes by season ID with not existing season ID.
     */
    @Test
    fun findAllBySeasonIdNotExistingSeasonId() {
        val episodes = repository.findAllBySeasonId(id = Int.MAX_VALUE, pageable = Pageable.ofSize(EpisodeUtils.EPISODES_PER_SEASON_COUNT))

        assertSoftly {
            it.assertThat(episodes.content).isEmpty()
            it.assertThat(episodes.number).isEqualTo(0)
            it.assertThat(episodes.totalPages).isEqualTo(0)
            it.assertThat(episodes.totalElements).isEqualTo(0L)
        }

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for find episode by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                for (episode in season.episodes) {
                    val result = repository.findByUuid(uuid = episode.uuid).orElse(null)

                    EpisodeUtils.assertEpisodeDeepEquals(expected = episode, actual = result)
                }
            }
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for get statistics.
     */
    @Test
    fun getStatistics() {
        val result = repository.getStatistics()

        EpisodeUtils.assertStatisticsDeepEquals(expected = EpisodeUtils.getStatistics(), actual = result)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

}
