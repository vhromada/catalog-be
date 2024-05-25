package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.entity.filter.PagingFilter
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
 * A class represents test for class [SeasonFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class SeasonFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [SeasonFacade]
     */
    @Autowired
    private lateinit var facade: SeasonFacade

    /**
     * Test method for [SeasonFacade.findAll].
     */
    @Test
    fun findAll() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            val filter = PagingFilter()
            filter.page = 1
            filter.limit = SeasonUtils.SEASONS_PER_SHOW_COUNT

            val result = facade.findAll(show = show.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            SeasonUtils.assertSeasonListDeepEquals(expected = SeasonUtils.getSeasons(show = i), actual = result.data)
        }

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.findAll] with not existing show.
     */
    @Test
    fun findAllNotExistingShow() {
        assertThatThrownBy { facade.findAll(show = TestConstants.UUID, filter = PagingFilter()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.findAll] with paging.
     */
    @Test
    fun findAllPaging() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            val filter = PagingFilter()
            filter.page = 2
            filter.limit = 1

            val result = facade.findAll(show = show.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(SeasonUtils.SEASONS_PER_SHOW_COUNT)
            }
            SeasonUtils.assertSeasonsDeepEquals(expected = listOf(show.seasons[1]), actual = result.data)
        }

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.findAll] with invalid paging.
     */
    @Test
    fun findAllInvalidPaging() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            val filter = PagingFilter()
            filter.page = 2
            filter.limit = SeasonUtils.SEASONS_PER_SHOW_COUNT

            val result = facade.findAll(show = show.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.data).isEmpty()
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
        }

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getDomainShow(index = i)
            for (season in show.seasons) {
                val result = facade.get(show = show.uuid, uuid = season.uuid)

                SeasonUtils.assertSeasonDeepEquals(expected = season, actual = result)
            }
        }

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.get] with not existing show.
     */
    @Test
    fun getNotExistingShow() {
        assertThatThrownBy { facade.get(show = TestConstants.UUID, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val show = ShowUtils.getDomainShow(index = 1)
        val expectedSeason = SeasonUtils.newSeason()
        val expectedDomainSeason = SeasonUtils.newDomainSeason(id = SeasonUtils.SEASONS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedDomainSeason.show = show

        val result = facade.add(show = show.uuid, request = SeasonUtils.newRequest())
        entityManager.flush()

        SeasonUtils.assertSeasonDeepEquals(expected = expectedSeason, actual = result, ignoreUuid = true)
        SeasonUtils.assertSeasonDeepEquals(expected = expectedDomainSeason, actual = SeasonUtils.getDomainSeason(entityManager = entityManager, id = SeasonUtils.SEASONS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT + 1)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with null number of season.
     */
    @Test
    fun addNullNumber() {
        val request = SeasonUtils.newRequest()
            .copy(number = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NUMBER_NULL")
            .hasMessageContaining("Number of season mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with not positive number of season.
     */
    @Test
    fun addNotPositiveNumber() {
        val request = SeasonUtils.newRequest()
            .copy(number = -1)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NUMBER_NOT_POSITIVE")
            .hasMessageContaining("Number of season must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with null starting year.
     */
    @Test
    fun addNullStartingYear() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_START_YEAR_NULL")
            .hasMessageContaining("Starting year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with null ending year.
     */
    @Test
    fun addNullEndingYear() {
        val request = SeasonUtils.newRequest()
            .copy(endYear = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_END_YEAR_NULL")
            .hasMessageContaining("Ending year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with bad minimum starting year and bad minimum ending year.
     */
    @Test
    fun addBadMinimumYears() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = TestConstants.BAD_MIN_YEAR, endYear = TestConstants.BAD_MIN_YEAR)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_STARTING_YEAR_EVENT.toString())
            .hasMessageContaining(TestConstants.INVALID_ENDING_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with bad maximum starting year and bad maximum ending year.
     */
    @Test
    fun addBadMaximumYears() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = TestConstants.BAD_MAX_YEAR, endYear = TestConstants.BAD_MAX_YEAR)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_STARTING_YEAR_EVENT.toString())
            .hasMessageContaining(TestConstants.INVALID_ENDING_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with starting year greater than ending yea.
     */
    @Test
    fun addBadYears() {
        var request = SeasonUtils.newRequest()
        request = request.copy(startYear = request.endYear!! + 1)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_YEARS_NOT_VALID")
            .hasMessageContaining("Starting year mustn't be greater than ending year.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with null language.
     */
    @Test
    fun addNullLanguage() {
        val request = SeasonUtils.newRequest()
            .copy(language = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_LANGUAGE_NULL")
            .hasMessageContaining("Language mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with null subtitles.
     */
    @Test
    fun addNullSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = null)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_SUBTITLES_NULL")
            .hasMessageContaining("Subtitles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with request with subtitles with null value.
     */
    @Test
    fun addBadSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = listOf(null))

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_SUBTITLES_CONTAIN_NULL")
            .hasMessageContaining("Subtitles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with not existing show.
     */
    @Test
    fun addNotExistingShow() {
        assertThatThrownBy { facade.add(show = TestConstants.UUID, request = SeasonUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with not existing language.
     */
    @Test
    fun addNotExistingLanguage() {
        val request = SeasonUtils.newRequest()
            .copy(language = TestConstants.UUID)

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.add] with not existing subtitles.
     */
    @Test
    fun addNotExistingSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = listOf(TestConstants.UUID))

        assertThatThrownBy { facade.add(show = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val show = ShowUtils.getDomainShow(index = 1)
        val request = SeasonUtils.newRequest()
        val expectedSeason = SeasonUtils.getSeason(index = 1)
            .updated()
        val expectedDomainSeason = show.seasons.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(show = show.uuid, uuid = show.seasons.first().uuid, request = request)
        entityManager.flush()

        SeasonUtils.assertSeasonDeepEquals(expected = expectedSeason, actual = result)
        SeasonUtils.assertSeasonDeepEquals(expected = expectedDomainSeason, actual = SeasonUtils.getDomainSeason(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with null number of season.
     */
    @Test
    fun updateNullNumber() {
        val request = SeasonUtils.newRequest()
            .copy(number = null)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NUMBER_NULL")
            .hasMessageContaining("Number of season mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with not positive number of season.
     */
    @Test
    fun updateNotPositiveNumber() {
        val request = SeasonUtils.newRequest()
            .copy(number = -1)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NUMBER_NOT_POSITIVE")
            .hasMessageContaining("Number of season must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with null starting year.
     */
    @Test
    fun updateNullStartingYear() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = null)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_START_YEAR_NULL")
            .hasMessageContaining("Starting year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with null ending year.
     */
    @Test
    fun updateNullEndingYear() {
        val request = SeasonUtils.newRequest()
            .copy(endYear = null)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_END_YEAR_NULL")
            .hasMessageContaining("Ending year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with bad minimum starting year and bad minimum ending year.
     */
    @Test
    fun updateBadMinimumYears() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = TestConstants.BAD_MIN_YEAR, endYear = TestConstants.BAD_MIN_YEAR)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_STARTING_YEAR_EVENT.toString())
            .hasMessageContaining(TestConstants.INVALID_ENDING_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with bad maximum starting year and bad maximum ending year.
     */
    @Test
    fun updateBadMaximumYears() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = TestConstants.BAD_MAX_YEAR, endYear = TestConstants.BAD_MAX_YEAR)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_STARTING_YEAR_EVENT.toString())
            .hasMessageContaining(TestConstants.INVALID_ENDING_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with starting year greater than ending yea.
     */
    @Test
    fun updateBadYears() {
        var request = SeasonUtils.newRequest()
        request = request.copy(startYear = request.endYear!! + 1)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_YEARS_NOT_VALID")
            .hasMessageContaining("Starting year mustn't be greater than ending year.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with null language.
     */
    @Test
    fun updateNullLanguage() {
        val request = SeasonUtils.newRequest()
            .copy(language = null)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_LANGUAGE_NULL")
            .hasMessageContaining("Language mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with null subtitles.
     */
    @Test
    fun updateNullSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = null)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_SUBTITLES_NULL")
            .hasMessageContaining("Subtitles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with request with subtitles with null value.
     */
    @Test
    fun updateBadSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = listOf(null))

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_SUBTITLES_CONTAIN_NULL")
            .hasMessageContaining("Subtitles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with not existing show.
     */
    @Test
    fun updateNotExistingShow() {
        assertThatThrownBy { facade.update(show = TestConstants.UUID, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = SeasonUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with not existing language.
     */
    @Test
    fun updateNotExistingLanguage() {
        val request = SeasonUtils.newRequest()
            .copy(language = TestConstants.UUID)

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with not existing subtitles.
     */
    @Test
    fun updateNotExistingSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = listOf(TestConstants.UUID))

        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = TestConstants.UUID, request = SeasonUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.remove].
     */
    @Test
    fun remove() {
        val show = ShowUtils.getDomainShow(index = 1)

        facade.remove(show = show.uuid, uuid = show.seasons.first().uuid)
        entityManager.flush()

        assertThat(SeasonUtils.getDomainSeason(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT - 1)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT - EpisodeUtils.EPISODES_PER_SEASON_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.remove] with not existing show.
     */
    @Test
    fun removeNotExistingShow() {
        assertThatThrownBy { facade.remove(show = TestConstants.UUID, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val show = ShowUtils.getDomainShow(index = 1)
        val expectedSeason = SeasonUtils.getSeason(index = 1)
        val expectedDomainSeason = show.seasons.first()
            .copy(id = SeasonUtils.SEASONS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        show.seasons.add(expectedDomainSeason)
        val expectedDomainEpisodes = expectedDomainSeason.episodes.mapIndexed { index, episode ->
            episode.copy(id = EpisodeUtils.EPISODES_COUNT + index + 1, season = expectedDomainSeason)
                .fillAudit(audit = AuditUtils.newAudit())
        }
        expectedDomainSeason.episodes.clear()
        expectedDomainSeason.episodes.addAll(expectedDomainEpisodes)

        val result = facade.duplicate(show = show.uuid, uuid = show.seasons.first().uuid)
        entityManager.flush()

        SeasonUtils.assertSeasonDeepEquals(expected = expectedSeason, actual = result, ignoreUuid = true)
        SeasonUtils.assertSeasonDeepEquals(expected = expectedDomainSeason, actual = SeasonUtils.getDomainSeason(entityManager = entityManager, id = SeasonUtils.SEASONS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT + 1)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT + EpisodeUtils.EPISODES_PER_SEASON_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.duplicate] with not existing show.
     */
    @Test
    fun duplicateNotExistingShow() {
        assertThatThrownBy { facade.duplicate(show = TestConstants.UUID, uuid = ShowUtils.getDomainShow(index = 1).seasons.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

    /**
     * Test method for [SeasonFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(show = ShowUtils.getDomainShow(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NOT_EXIST")
            .hasMessageContaining("Season doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
        }
    }

}
