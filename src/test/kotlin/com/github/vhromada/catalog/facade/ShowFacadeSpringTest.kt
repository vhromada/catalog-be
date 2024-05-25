package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.EpisodeUtils
import com.github.vhromada.catalog.utils.GenreUtils
import com.github.vhromada.catalog.utils.PictureUtils
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
 * A class represents test for class [ShowFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class ShowFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [ShowFacade]
     */
    @Autowired
    private lateinit var facade: ShowFacade

    /**
     * Test method for [ShowFacade.search].
     */
    @Test
    fun search() {
        val filter = MultipleNameFilter()
        filter.page = 1
        filter.limit = ShowUtils.SHOWS_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        ShowUtils.assertShowListDeepEquals(expected = ShowUtils.getShows(), actual = result.data)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = MultipleNameFilter()
        filter.page = 2
        filter.limit = 1
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(ShowUtils.SHOWS_COUNT)
        }
        ShowUtils.assertShowListDeepEquals(expected = listOf(ShowUtils.getShow(index = 2)), actual = result.data)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = MultipleNameFilter()
        filter.page = 2
        filter.limit = ShowUtils.SHOWS_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getShow(index = i)
            val filter = MultipleNameFilter(czechName = show.czechName, originalName = show.originalName)
            filter.page = 1
            filter.limit = ShowUtils.SHOWS_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            ShowUtils.assertShowListDeepEquals(expected = listOf(show), actual = result.data)
        }

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..ShowUtils.SHOWS_COUNT) {
            val show = ShowUtils.getShow(index = i)

            val result = facade.get(uuid = show.uuid)

            ShowUtils.assertShowDeepEquals(expected = ShowUtils.getShow(index = i), actual = result)
        }

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedShow = ShowUtils.newShow()
        val expectedDomainShow = ShowUtils.newDomainShow(id = ShowUtils.SHOWS_COUNT + 1)
            .copy(picture = 1, genres = mutableListOf(GenreUtils.getDomainGenre(index = 1)))
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = ShowUtils.newRequest())
        entityManager.flush()

        ShowUtils.assertShowDeepEquals(expected = expectedShow, actual = result, ignoreUuid = true)
        ShowUtils.assertShowDeepEquals(expected = expectedDomainShow, actual = ShowUtils.getDomainShow(entityManager = entityManager, id = ShowUtils.SHOWS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT + 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with null czech name.
     */
    @Test
    fun addNullCzechName() {
        val request = ShowUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with empty string as czech name.
     */
    @Test
    fun addEmptyCzechName() {
        val request = ShowUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with null original name.
     */
    @Test
    fun addNullOriginalName() {
        val request = ShowUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with empty original name.
     */
    @Test
    fun addEmptyOriginalName() {
        val request = ShowUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with bad minimal IMDB code.
     */
    @Test
    fun addBadMinimalImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MIN_IMDB_CODE)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with bad divider IMDB code.
     */
    @Test
    fun addBadDividerImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = 0)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with bad maximal IMDB code.
     */
    @Test
    fun addBadMaximalImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MAX_IMDB_CODE)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with null genres.
     */
    @Test
    fun addNullGenres() {
        val request = ShowUtils.newRequest()
            .copy(genres = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRES_NULL")
            .hasMessageContaining("Genres mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with genres with null value.
     */
    @Test
    fun addBadGenres() {
        val request = ShowUtils.newRequest()
            .copy(genres = listOf(null))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRES_CONTAIN_NULL")
            .hasMessageContaining("Genres mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with request with genres with empty genre.
     */
    @Test
    fun addGenresBadGenre() {
        val request = ShowUtils.newRequest()
            .copy(genres = listOf(""))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRE_EMPTY")
            .hasMessageContaining("Genre mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with not existing picture.
     */
    @Test
    fun addNotExistingPicture() {
        val request = ShowUtils.newRequest()
            .copy(picture = TestConstants.UUID)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.add] with not existing genre.
     */
    @Test
    fun addNotExistingGenre() {
        val request = ShowUtils.newRequest()
            .copy(genres = mutableListOf(TestConstants.UUID))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = ShowUtils.newRequest()
        val expectedShow = ShowUtils.getShow(index = 1)
            .copy(picture = PictureUtils.getPicture(index = 1).uuid)
            .updated()
        val expectedDomainShow = ShowUtils.getDomainShow(index = 1)
            .copy(picture = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedDomainShow.seasons.forEach { it.show = expectedDomainShow }

        val result = facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request)
        entityManager.flush()

        ShowUtils.assertShowDeepEquals(expected = expectedShow, actual = result)
        ShowUtils.assertShowDeepEquals(expected = expectedDomainShow, actual = ShowUtils.getDomainShow(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with null czech name.
     */
    @Test
    fun updateNullCzechName() {
        val request = ShowUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with empty string as czech name.
     */
    @Test
    fun updateEmptyCzechName() {
        val request = ShowUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with null original name.
     */
    @Test
    fun updateNullOriginalName() {
        val request = ShowUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with empty original name.
     */
    @Test
    fun updateEmptyOriginalName() {
        val request = ShowUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with bad minimal IMDB code.
     */
    @Test
    fun updateBadMinimalImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MIN_IMDB_CODE)

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with bad divider IMDB code.
     */
    @Test
    fun updateBadDividerImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = 0)

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with bad maximal IMDB code.
     */
    @Test
    fun updateBadMaximalImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MAX_IMDB_CODE)

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with null genres.
     */
    @Test
    fun updateNullGenres() {
        val request = ShowUtils.newRequest()
            .copy(genres = null)

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRES_NULL")
            .hasMessageContaining("Genres mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with genres with null value.
     */
    @Test
    fun updateBadGenres() {
        val request = ShowUtils.newRequest()
            .copy(genres = listOf(null))

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRES_CONTAIN_NULL")
            .hasMessageContaining("Genres mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with request with genres with empty genre.
     */
    @Test
    fun updateGenresBadGenre() {
        val request = ShowUtils.newRequest()
            .copy(genres = listOf(""))

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRE_EMPTY")
            .hasMessageContaining("Genre mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with not existing picture.
     */
    @Test
    fun updateNotExistingPicture() {
        val request = ShowUtils.newRequest()
            .copy(picture = TestConstants.UUID)

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with not existing genre.
     */
    @Test
    fun updateNotExistingGenre() {
        val request = ShowUtils.newRequest()
            .copy(genres = mutableListOf(TestConstants.UUID))

        assertThatThrownBy { facade.update(uuid = ShowUtils.getDomainShow(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = ShowUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.remove].
     */
    @Test
    fun remove() {
        facade.remove(uuid = ShowUtils.getShow(index = 1).uuid)
        entityManager.flush()

        assertThat(ShowUtils.getDomainShow(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT - 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT - SeasonUtils.SEASONS_PER_SHOW_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT - EpisodeUtils.EPISODES_PER_SHOW_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedShow = ShowUtils.getShow(index = 1)
        val expectedDomainShow = ShowUtils.getDomainShow(index = 1)
            .copy(id = ShowUtils.SHOWS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        val expectedDomainSeasons = expectedDomainShow.seasons.mapIndexed { index, season ->
            val expectedDomainSeason = season.copy(id = SeasonUtils.SEASONS_COUNT + index + 1, show = expectedDomainShow)
                .fillAudit(audit = AuditUtils.newAudit())
            val expectedDomainEpisodes = expectedDomainSeason.episodes.mapIndexed { episodeIndex, episode ->
                episode.copy(id = EpisodeUtils.EPISODES_COUNT + EpisodeUtils.EPISODES_PER_SEASON_COUNT * index + episodeIndex + 1, season = expectedDomainSeason)
                    .fillAudit(audit = AuditUtils.newAudit())
            }
            expectedDomainSeason.episodes.clear()
            expectedDomainSeason.episodes.addAll(expectedDomainEpisodes)

            expectedDomainSeason
        }
        expectedDomainShow.seasons.clear()
        expectedDomainShow.seasons.addAll(expectedDomainSeasons)

        val result = facade.duplicate(uuid = ShowUtils.getShow(index = 1).uuid)
        entityManager.flush()

        ShowUtils.assertShowDeepEquals(expected = expectedShow, actual = result, ignoreUuid = true)
        ShowUtils.assertShowDeepEquals(expected = expectedDomainShow, actual = ShowUtils.getDomainShow(entityManager = entityManager, id = ShowUtils.SHOWS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT + 1)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT + SeasonUtils.SEASONS_PER_SHOW_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT + EpisodeUtils.EPISODES_PER_SHOW_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_NOT_EXIST")
            .hasMessageContaining("Show doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [ShowFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        val result = facade.getStatistics()

        ShowUtils.assertStatisticsDeepEquals(expected = ShowUtils.getStatistics(), actual = result)

        assertSoftly {
            it.assertThat(ShowUtils.getShowsCount(entityManager = entityManager)).isEqualTo(ShowUtils.SHOWS_COUNT)
            it.assertThat(SeasonUtils.getSeasonsCount(entityManager = entityManager)).isEqualTo(SeasonUtils.SEASONS_COUNT)
            it.assertThat(EpisodeUtils.getEpisodesCount(entityManager = entityManager)).isEqualTo(EpisodeUtils.EPISODES_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

}
