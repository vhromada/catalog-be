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
 * A class represents test for class [SeasonRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class SeasonRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [SeasonRepository]
     */
    @Autowired
    private lateinit var repository: SeasonRepository

    /**
     * Test method for get season.
     */
    @Test
    fun getSeason() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            for (j in 1..SeasonUtils.SEASONS_PER_SHOW_COUNT) {
                val id = (i - 1) * SeasonUtils.SEASONS_PER_SHOW_COUNT + j

                val season = repository.findById(id).orElse(null)

                SeasonUtils.assertSeasonDeepEquals(expected = ShowUtils.getDomainShow(index = i).seasons[j - 1], actual = season)
            }
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for add season.
     */
    @Test
    @DirtiesContext
    fun add() {
        val season = SeasonUtils.newDomainSeason(id = null)
        season.show = ShowUtils.getDomainShow(entityManager = entityManager, id = 1)
        val expectedSeason = SeasonUtils.newDomainSeason(id = SeasonUtils.SEASONS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedSeason.show = ShowUtils.getDomainShow(entityManager = entityManager, id = 1)

        repository.saveAndFlush(season)

        assertSoftly {
            it.assertThat(season.id).isEqualTo(SeasonUtils.SEASONS_COUNT + 1)
            it.assertThat(season.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(season.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(season.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(season.updatedTime).isEqualTo(TestConstants.TIME)
        }
        SeasonUtils.assertSeasonDeepEquals(expected = expectedSeason, actual = SeasonUtils.getDomainSeason(entityManager = entityManager, id = SeasonUtils.SEASONS_COUNT + 1))

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT + 1)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for update season.
     */
    @Test
    fun update() {
        val season = SeasonUtils.getDomainSeason(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedSeason = ShowUtils.getDomainShow(index = 1).seasons.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(season)

        SeasonUtils.assertSeasonDeepEquals(expected = expectedSeason, actual = SeasonUtils.getDomainSeason(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for count of seasons.
     */
    @Test
    fun count() {
        val result = repository.count()

        assertThat(result).isEqualTo(SeasonUtils.SEASONS_COUNT.toLong())

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for get seasons by show ID.
     */
    @Test
    fun findAllByShowId() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val seasons = repository.findAllByShowId(id = i, pageable = Pageable.ofSize(SeasonUtils.SEASONS_PER_SHOW_COUNT))

            assertSoftly {
                it.assertThat(seasons.number).isEqualTo(0)
                it.assertThat(seasons.totalPages).isEqualTo(1)
                it.assertThat(seasons.totalElements).isEqualTo(SeasonUtils.SEASONS_PER_SHOW_COUNT.toLong())
            }
            SeasonUtils.assertDomainSeasonsDeepEquals(expected = ShowUtils.getDomainShow(index = i).seasons, actual = seasons.content)
        }

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for get seasons by show ID with invalid paging.
     */
    @Test
    fun findAllByShowIdInvalidPaging() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val seasons = repository.findAllByShowId(id = i, pageable = PageRequest.of(2, SeasonUtils.SEASONS_PER_SHOW_COUNT))

            assertSoftly {
                it.assertThat(seasons.content).isEmpty()
                it.assertThat(seasons.number).isEqualTo(2)
                it.assertThat(seasons.totalPages).isEqualTo(1)
                it.assertThat(seasons.totalElements).isEqualTo(SeasonUtils.SEASONS_PER_SHOW_COUNT.toLong())
            }
        }

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for get seasons by show ID with not existing show ID.
     */
    @Test
    fun findAllByShowIdNotExistingShowId() {
        val seasons = repository.findAllByShowId(id = Int.MAX_VALUE, pageable = Pageable.ofSize(SeasonUtils.SEASONS_PER_SHOW_COUNT))

        assertSoftly {
            it.assertThat(seasons.content).isEmpty()
            it.assertThat(seasons.number).isEqualTo(0)
            it.assertThat(seasons.totalPages).isEqualTo(0)
            it.assertThat(seasons.totalElements).isEqualTo(0L)
        }

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }


    /**
     * Test method for find season by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                val result = repository.findByUuid(uuid = season.uuid).orElse(null)

                SeasonUtils.assertSeasonDeepEquals(expected = season, actual = result)
            }
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

}
