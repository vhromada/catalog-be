package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.ShowFilter
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.EpisodeUtils
import com.github.vhromada.catalog.utils.GenreUtils
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
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [ShowRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class ShowRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [ShowRepository]
     */
    @Autowired
    private lateinit var repository: ShowRepository

    /**
     * Test method for get shows.
     */
    @Test
    fun getShows() {
        val shows = repository.findAll()

        ShowUtils.assertDomainShowsDeepEquals(expected = ShowUtils.getDomainShows(), actual = shows)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for get show.
     */
    @Test
    fun getShow() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = repository.findById(i).orElse(null)

            ShowUtils.assertShowDeepEquals(expected = ShowUtils.getDomainShow(index = i), actual = show)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for add show.
     */
    @Test
    @DirtiesContext
    fun add() {
        val show = ShowUtils.newDomainShow(id = null)
            .copy(genres = mutableListOf(GenreUtils.getDomainGenre(entityManager = entityManager, id = 1)!!))
        val expectedShow = ShowUtils.newDomainShow(id = ShowUtils.SHOWS_COUNT + 1)
            .copy(picture = null, genres = mutableListOf(GenreUtils.getDomainGenre(index = 1)))
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(show)

        assertSoftly {
            it.assertThat(show.id).isEqualTo(ShowUtils.SHOWS_COUNT + 1)
            it.assertThat(show.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(show.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(show.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(show.updatedTime).isEqualTo(TestConstants.TIME)
        }
        ShowUtils.assertShowDeepEquals(expected = expectedShow, actual = ShowUtils.getDomainShow(entityManager = entityManager, id = ShowUtils.SHOWS_COUNT + 1))

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT + 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for update show.
     */
    @Test
    fun update() {
        val show = ShowUtils.getDomainShow(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedShow = ShowUtils.getDomainShow(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(show)

        ShowUtils.assertShowDeepEquals(expected = expectedShow, actual = ShowUtils.getDomainShow(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for remove show.
     */
    @Test
    fun remove() {
        repository.delete(ShowUtils.getDomainShow(entityManager = entityManager, id = 1)!!)

        assertThat(ShowUtils.getDomainShow(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT - 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT - SeasonUtils.SEASONS_PER_SHOW_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT - EpisodeUtils.EPISODES_PER_SHOW_COUNT)
        }
    }

    /**
     * Test method for remove all shows.
     */
    @Test
    fun removeAll() {
        repository.deleteAll()

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(0)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(0)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(0)
        }
    }

    /**
     * Test method for count of shows.
     */
    @Test
    fun count() {
        val result = repository.count()

        assertThat(result).isEqualTo(ShowUtils.SHOWS_COUNT.toLong())

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for search shows by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            val filter = ShowFilter(czechName = show.czechName, originalName = show.originalName)

            val result = repository.findAll(filter.toSpecification())

            ShowUtils.assertDomainShowsDeepEquals(expected = listOf(show), actual = result.toList())
        }

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for find show by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)

            val result = repository.findByUuid(uuid = show.uuid).orElse(null)

            ShowUtils.assertShowDeepEquals(expected = ShowUtils.getDomainShow(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

}
