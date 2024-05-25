package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.exception.InputException
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
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [EpisodeFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class EpisodeFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [EpisodeFacade]
     */
    @Autowired
    private lateinit var facade: EpisodeFacade

    /**
     * Test method for [EpisodeFacade.findAll].
     */
    @Test
    fun findAll() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (j in 1..SeasonUtils.SEASONS_PER_SHOW_COUNT) {
                val season = show.seasons[j - 1]
                val filter = PagingFilter()
                filter.page = 1
                filter.limit = EpisodeUtils.EPISODES_PER_SEASON_COUNT

                val result = facade.findAll(show = show.uuid, season = season.uuid, filter = filter)

                assertSoftly {
                    it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                    it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
                }
                EpisodeUtils.assertEpisodeListDeepEquals(expected = EpisodeUtils.getEpisodes(show = i, season = j), actual = result.data)
            }
        }

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.findAll] with not existing show.
     */
    @Test
    fun findAllNotExistingShow() {
        assertThatThrownBy { facade.findAll(show = TestConstants.UUID, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, filter = PagingFilter()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.findAll] with not existing season.
     */
    @Test
    fun findAllNotExistingSeason() {
        assertThatThrownBy { facade.findAll(show = ShowUtils.getDomainShow(index = 1).uuid, season = TestConstants.UUID, filter = PagingFilter()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.findAll] with paging.
     */
    @Test
    fun findAllPaging() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                val filter = PagingFilter()
                filter.page = 2
                filter.limit = 1

                val result = facade.findAll(show = show.uuid, season = season.uuid, filter = filter)

                assertSoftly {
                    it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                    it.assertThat(result.pagingInfo.pagesCount).isEqualTo(EpisodeUtils.EPISODES_PER_SEASON_COUNT)
                }
                EpisodeUtils.assertEpisodesDeepEquals(expected = listOf(season.episodes[1]), actual = result.data)
            }
        }

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.findAll] with invalid paging.
     */
    @Test
    fun findAllInvalidPaging() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                val filter = PagingFilter()
                filter.page = 2
                filter.limit = EpisodeUtils.EPISODES_PER_SEASON_COUNT

                val result = facade.findAll(show = show.uuid, season = season.uuid, filter = filter)

                assertSoftly {
                    it.assertThat(result.data).isEmpty()
                    it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                    it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
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
     * Test method for [EpisodeFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                for (episode in season.episodes) {
                    val result = facade.get(show = show.uuid, season = season.uuid, uuid = episode.uuid)

                    EpisodeUtils.assertEpisodeDeepEquals(expected = episode, actual = result)
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
     * Test method for [EpisodeFacade.get] with not existing show.
     */
    @Test
    fun getNotExistingShow() {
        assertThatThrownBy {
            facade.get(
                show = TestConstants.UUID,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.get] with not existing season.
     */
    @Test
    fun getNotExistingSeason() {
        assertThatThrownBy { facade.get(show = ShowUtils.getDomainShow(index = 1).uuid, season = TestConstants.UUID, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NOT_EXIST")
            .hasMessageContaining("Episode doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val show = ShowUtils.getDomainShow(index = 1)
        val season = show.seasons.first()
        val expectedEpisode = EpisodeUtils.newEpisode()
        val expectedDomainEpisode = EpisodeUtils.newDomainEpisode(id = EpisodeUtils.EPISODES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedDomainEpisode.season = season

        val result = facade.add(show = show.uuid, season = season.uuid, request = EpisodeUtils.newRequest())
        entityManager.flush()

        EpisodeUtils.assertEpisodeDeepEquals(expected = expectedEpisode, actual = result, ignoreUuid = true)
        EpisodeUtils.assertEpisodeDeepEquals(
            expected = expectedDomainEpisode,
            actual = EpisodeUtils.getDomainEpisode(entityManager = entityManager, id = EpisodeUtils.EPISODES_COUNT + 1),
            ignoreUuid = true
        )

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT + 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with null number of episode.
     */
    @Test
    fun addNullNumber() {
        val request = EpisodeUtils.newRequest()
            .copy(number = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NUMBER_NULL")
            .hasMessageContaining("Number of episode mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with not positive number of episode.
     */
    @Test
    fun addNotPositiveNumber() {
        val request = EpisodeUtils.newRequest()
            .copy(number = -1)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NUMBER_NOT_POSITIVE")
            .hasMessageContaining("Number of episode must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.add] with request with null name.
     */
    @Test
    fun addNullName() {
        val request = EpisodeUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.add] with request with empty string as name.
     */
    @Test
    fun addEmptyName() {
        val request = EpisodeUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.add] with episode with null length of episode.
     */
    @Test
    fun addNullLength() {
        val request = EpisodeUtils.newRequest()
            .copy(length = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_LENGTH_NULL")
            .hasMessageContaining("Length of episode mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.add] with episode with negative length of episode.
     */
    @Test
    fun addNegativeLength() {
        val request = EpisodeUtils.newRequest()
            .copy(length = -1)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_LENGTH_NEGATIVE")
            .hasMessageContaining("Length of episode mustn't be negative number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.add] with not existing show.
     */
    @Test
    fun addNotExistingShow() {
        assertThatThrownBy { facade.add(show = TestConstants.UUID, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = EpisodeUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.add] with not existing season.
     */
    @Test
    fun addNotExistingSeason() {
        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, season = TestConstants.UUID, request = EpisodeUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val show = ShowUtils.getDomainShow(index = 1)
        val season = show.seasons.first()
        val request = EpisodeUtils.newRequest()
        val expectedEpisode = EpisodeUtils.getEpisode(index = 1)
            .updated()
        val expectedDomainEpisode = season.episodes.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(show = show.uuid, season = season.uuid, uuid = season.episodes.first().uuid, request = request)
        entityManager.flush()

        EpisodeUtils.assertEpisodeDeepEquals(expected = expectedEpisode, actual = result)
        EpisodeUtils.assertEpisodeDeepEquals(expected = expectedDomainEpisode, actual = EpisodeUtils.getDomainEpisode(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with request with null number of episode.
     */
    @Test
    fun updateNullNumber() {
        val request = EpisodeUtils.newRequest()
            .copy(number = null)

        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = request
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NUMBER_NULL")
            .hasMessageContaining("Number of episode mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with request with not positive number of episode.
     */
    @Test
    fun updateNotPositiveNumber() {
        val request = EpisodeUtils.newRequest()
            .copy(number = -1)

        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = request
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NUMBER_NOT_POSITIVE")
            .hasMessageContaining("Number of episode must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with request with null name.
     */
    @Test
    fun updateNullName() {
        val request = EpisodeUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = request
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with request with empty string as name.
     */
    @Test
    fun updateEmptyName() {
        val request = EpisodeUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = request
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [EpisodeFacade.update] with episode with null length of episode.
     */
    @Test
    fun updateNullLength() {
        val request = EpisodeUtils.newRequest()
            .copy(length = null)

        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = request
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_LENGTH_NULL")
            .hasMessageContaining("Length of episode mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with episode with negative length of episode.
     */
    @Test
    fun updateNegativeLength() {
        val request = EpisodeUtils.newRequest()
            .copy(length = -1)

        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = request
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_LENGTH_NEGATIVE")
            .hasMessageContaining("Length of episode mustn't be negative number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with not existing show.
     */
    @Test
    fun updateNotExistingShow() {
        assertThatThrownBy {
            facade.update(
                show = TestConstants.UUID,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = EpisodeUtils.newRequest()
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with not existing season.
     */
    @Test
    fun updateNotExistingSeason() {
        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = TestConstants.UUID,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid,
                request = EpisodeUtils.newRequest()
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy {
            facade.update(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = TestConstants.UUID,
                request = EpisodeUtils.newRequest()
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NOT_EXIST")
            .hasMessageContaining("Episode doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.remove].
     */
    @Test
    fun remove() {
        val show = ShowUtils.getDomainShow(index = 1)
        val season = show.seasons.first()

        facade.remove(show = show.uuid, season = season.uuid, uuid = season.episodes.first().uuid)
        entityManager.flush()

        assertThat(EpisodeUtils.getDomainEpisode(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT - 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.remove] with not existing show.
     */
    @Test
    fun removeNotExistingShow() {
        assertThatThrownBy {
            facade.remove(
                show = TestConstants.UUID,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.remove] with not existing season.
     */
    @Test
    fun removeNotExistingSeason() {
        assertThatThrownBy {
            facade.remove(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = TestConstants.UUID,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NOT_EXIST")
            .hasMessageContaining("Episode doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val show = ShowUtils.getDomainShow(index = 1)
        val season = show.seasons.first()
        val expectedEpisode = EpisodeUtils.getEpisode(index = 1)
        val expectedDomainEpisode = season.episodes.first()
            .copy(id = EpisodeUtils.EPISODES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        season.episodes.add(expectedDomainEpisode)

        val result = facade.duplicate(show = show.uuid, season = season.uuid, uuid = season.episodes.first().uuid)
        entityManager.flush()

        EpisodeUtils.assertEpisodeDeepEquals(expected = expectedEpisode, actual = result, ignoreUuid = true)
        EpisodeUtils.assertEpisodeDeepEquals(
            expected = expectedDomainEpisode,
            actual = EpisodeUtils.getDomainEpisode(entityManager = entityManager, id = EpisodeUtils.EPISODES_COUNT + 1),
            ignoreUuid = true
        )

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT + 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.duplicate] with not existing show.
     */
    @Test
    fun duplicateNotExistingShow() {
        assertThatThrownBy {
            facade.duplicate(
                show = TestConstants.UUID,
                season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.duplicate] with not existing season.
     */
    @Test
    fun duplicateNotExistingSeason() {
        assertThatThrownBy {
            facade.duplicate(
                show = ShowUtils.getDomainShow(index = 1).uuid,
                season = TestConstants.UUID,
                uuid = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first().uuid
            )
        }.isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

    /**
     * Test method for [EpisodeFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(show = ShowUtils.getDomainShow(index = 1).uuid, season = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NOT_EXIST")
            .hasMessageContaining("Episode doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
    }

}
