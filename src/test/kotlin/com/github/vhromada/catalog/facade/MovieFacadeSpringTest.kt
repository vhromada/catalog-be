package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GenreUtils
import com.github.vhromada.catalog.utils.MediumUtils
import com.github.vhromada.catalog.utils.MovieUtils
import com.github.vhromada.catalog.utils.PictureUtils
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
 * A class represents test for class [MovieFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class MovieFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [MovieFacade]
     */
    @Autowired
    private lateinit var facade: MovieFacade

    /**
     * Test method for [MovieFacade.search].
     */
    @Test
    fun search() {
        val filter = MultipleNameFilter()
        filter.page = 1
        filter.limit = MovieUtils.MOVIES_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        MovieUtils.assertMovieListDeepEquals(expected = MovieUtils.getMovies(), actual = result.data)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = MultipleNameFilter()
        filter.page = 2
        filter.limit = 1
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(MovieUtils.MOVIES_COUNT)
        }
        MovieUtils.assertMovieListDeepEquals(expected = listOf(MovieUtils.getMovie(index = 2)), actual = result.data)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = MultipleNameFilter()
        filter.page = 2
        filter.limit = MovieUtils.MOVIES_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..MovieUtils.MOVIES_COUNT) {
            val movie = MovieUtils.getMovie(index = i)
            val filter = MultipleNameFilter(czechName = movie.czechName, originalName = movie.originalName)
            filter.page = 1
            filter.limit = MovieUtils.MOVIES_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            MovieUtils.assertMovieListDeepEquals(expected = listOf(movie), actual = result.data)
        }

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..MovieUtils.MOVIES_COUNT) {
            val movie = MovieUtils.getMovie(index = i)

            val result = facade.get(uuid = movie.uuid)

            MovieUtils.assertMovieDeepEquals(expected = MovieUtils.getMovie(index = i), actual = result)
        }

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_NOT_EXIST")
            .hasMessageContaining("Movie doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedMovie = MovieUtils.newMovie()
        val expectedDomainMovie = MovieUtils.newDomainMovie(id = MovieUtils.MOVIES_COUNT + 1)
            .copy(picture = 1, genres = mutableListOf(GenreUtils.getDomainGenre(index = 1)))
            .fillAudit(audit = AuditUtils.newAudit())
        val expectedDomainMedia = expectedDomainMovie.media.mapIndexed { index, medium ->
            medium.copy(id = MediumUtils.MEDIA_COUNT + index + 1)
                .fillAudit(audit = AuditUtils.newAudit())
        }
        expectedDomainMovie.media.clear()
        expectedDomainMovie.media.addAll(expectedDomainMedia)

        val result = facade.add(request = MovieUtils.newRequest())
        entityManager.flush()

        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = result, ignoreUuid = true)
        MovieUtils.assertMovieDeepEquals(expected = expectedDomainMovie, actual = MovieUtils.getDomainMovie(entityManager = entityManager, id = MovieUtils.MOVIES_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT + 1)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT + 1)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with null czech name.
     */
    @Test
    fun addNullCzechName() {
        val request = MovieUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with empty string as czech name.
     */
    @Test
    fun addEmptyCzechName() {
        val request = MovieUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with null original name.
     */
    @Test
    fun addNullOriginalName() {
        val request = MovieUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with empty original name.
     */
    @Test
    fun addEmptyOriginalName() {
        val request = MovieUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with null year.
     */
    @Test
    fun addNullYear() {
        val request = MovieUtils.newRequest()
            .copy(year = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_YEAR_NULL")
            .hasMessageContaining("Year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with bad minimum year.
     */
    @Test
    fun addBadMinimumYear() {
        val request = MovieUtils.newRequest()
            .copy(year = TestConstants.BAD_MIN_YEAR)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with bad maximum year.
     */
    @Test
    fun addBadMaximumYear() {
        val request = MovieUtils.newRequest()
            .copy(year = TestConstants.BAD_MAX_YEAR)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with null languages.
     */
    @Test
    fun addNullLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_NULL")
            .hasMessageContaining("Languages mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with empty languages.
     */
    @Test
    fun addEmptyLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = emptyList())

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_EMPTY")
            .hasMessageContaining("Languages mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with languages with null value.
     */
    @Test
    fun addBadLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = listOf(null))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_CONTAIN_NULL")
            .hasMessageContaining("Languages mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with null subtitles.
     */
    @Test
    fun addNullSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_SUBTITLES_NULL")
            .hasMessageContaining("Subtitles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with subtitles with null value.
     */
    @Test
    fun addBadSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = listOf(null))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_SUBTITLES_CONTAIN_NULL")
            .hasMessageContaining("Subtitles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with null media.
     */
    @Test
    fun addNullMedia() {
        val request = MovieUtils.newRequest()
            .copy(media = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIA_NULL")
            .hasMessageContaining("Media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with media with null value.
     */
    @Test
    fun addBadMedia() {
        val request = MovieUtils.newRequest()
            .copy(media = listOf(null))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIA_CONTAIN_NULL")
            .hasMessageContaining("Media mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with media with negative value as medium.
     */
    @Test
    fun addMediaBadMedium() {
        val request = MovieUtils.newRequest()
            .copy(media = listOf(-1))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIUM_NOT_POSITIVE")
            .hasMessageContaining("Medium must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with bad minimal IMDB code.
     */
    @Test
    fun addBadMinimalImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MIN_IMDB_CODE)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with bad divider IMDB code.
     */
    @Test
    fun addBadDividerImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = 0)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with bad maximal IMDB code.
     */
    @Test
    fun addBadMaximalImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MAX_IMDB_CODE)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with null genres.
     */
    @Test
    fun addNullGenres() {
        val request = MovieUtils.newRequest()
            .copy(genres = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRES_NULL")
            .hasMessageContaining("Genres mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with genres with null value.
     */
    @Test
    fun addBadGenres() {
        val request = MovieUtils.newRequest()
            .copy(genres = listOf(null))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRES_CONTAIN_NULL")
            .hasMessageContaining("Genres mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with request with genres with empty genre.
     */
    @Test
    fun addGenresBadGenre() {
        val request = MovieUtils.newRequest()
            .copy(genres = listOf(""))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRE_EMPTY")
            .hasMessageContaining("Genre mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with not existing language.
     */
    @Test
    fun addNotExistingLanguage() {
        val request = MovieUtils.newRequest()
            .copy(languages = listOf(TestConstants.UUID))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with not existing subtitles.
     */
    @Test
    fun addNotExistingSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = listOf(TestConstants.UUID))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with not existing picture.
     */
    @Test
    fun addNotExistingPicture() {
        val request = MovieUtils.newRequest()
            .copy(picture = TestConstants.UUID)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.add] with not existing genre.
     */
    @Test
    fun addNotExistingGenre() {
        val request = MovieUtils.newRequest()
            .copy(genres = mutableListOf(TestConstants.UUID))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = MovieUtils.newRequest()
        val expectedMovie = MovieUtils.getMovie(index = 1)
            .copy(picture = PictureUtils.getPicture(index = 1).uuid, media = listOf(MediumUtils.newMedium()))
            .updated()
        val expectedDomainMovie = MovieUtils.getDomainMovie(index = 1)
            .copy(picture = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedDomainMovie.media.forEach {
            it.length = 10
            it.fillAudit(audit = AuditUtils.updatedAudit())
        }

        val result = facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request)
        entityManager.flush()

        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = result)
        MovieUtils.assertMovieDeepEquals(expected = expectedDomainMovie, actual = MovieUtils.getDomainMovie(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with null czech name.
     */
    @Test
    fun updateNullCzechName() {
        val request = MovieUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with empty string as czech name.
     */
    @Test
    fun updateEmptyCzechName() {
        val request = MovieUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with null original name.
     */
    @Test
    fun updateNullOriginalName() {
        val request = MovieUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with empty original name.
     */
    @Test
    fun updateEmptyOriginalName() {
        val request = MovieUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with null year.
     */
    @Test
    fun updateNullYear() {
        val request = MovieUtils.newRequest()
            .copy(year = null)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_YEAR_NULL")
            .hasMessageContaining("Year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with bad minimum year.
     */
    @Test
    fun updateBadMinimumYear() {
        val request = MovieUtils.newRequest()
            .copy(year = TestConstants.BAD_MIN_YEAR)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with bad maximum year.
     */
    @Test
    fun updateBadMaximumYear() {
        val request = MovieUtils.newRequest()
            .copy(year = TestConstants.BAD_MAX_YEAR)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with null languages.
     */
    @Test
    fun updateNullLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = null)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_NULL")
            .hasMessageContaining("Languages mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with empty languages.
     */
    @Test
    fun updateEmptyLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = emptyList())

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_EMPTY")
            .hasMessageContaining("Languages mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with languages with null value.
     */
    @Test
    fun updateBadLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = listOf(null))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_CONTAIN_NULL")
            .hasMessageContaining("Languages mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with null subtitles.
     */
    @Test
    fun updateNullSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = null)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_SUBTITLES_NULL")
            .hasMessageContaining("Subtitles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with subtitles with null value.
     */
    @Test
    fun updateBadSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = listOf(null))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_SUBTITLES_CONTAIN_NULL")
            .hasMessageContaining("Subtitles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with null media.
     */
    @Test
    fun updateNullMedia() {
        val request = MovieUtils.newRequest()
            .copy(media = null)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIA_NULL")
            .hasMessageContaining("Media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with media with null value.
     */
    @Test
    fun updateBadMedia() {
        val request = MovieUtils.newRequest()
            .copy(media = listOf(null))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIA_CONTAIN_NULL")
            .hasMessageContaining("Media mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with media with negative value as medium.
     */
    @Test
    fun updateMediaBadMedium() {
        val request = MovieUtils.newRequest()
            .copy(media = listOf(-1))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIUM_NOT_POSITIVE")
            .hasMessageContaining("Medium must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with bad minimal IMDB code.
     */
    @Test
    fun updateBadMinimalImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MIN_IMDB_CODE)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with bad divider IMDB code.
     */
    @Test
    fun updateBadDividerImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = 0)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with bad maximal IMDB code.
     */
    @Test
    fun updateBadMaximalImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MAX_IMDB_CODE)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with null genres.
     */
    @Test
    fun updateNullGenres() {
        val request = MovieUtils.newRequest()
            .copy(genres = null)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRES_NULL")
            .hasMessageContaining("Genres mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with genres with null value.
     */
    @Test
    fun updateBadGenres() {
        val request = MovieUtils.newRequest()
            .copy(genres = listOf(null))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRES_CONTAIN_NULL")
            .hasMessageContaining("Genres mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with request with genres with empty genre.
     */
    @Test
    fun updateGenresBadGenre() {
        val request = MovieUtils.newRequest()
            .copy(genres = listOf(""))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRE_EMPTY")
            .hasMessageContaining("Genre mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with not existing language.
     */
    @Test
    fun updateNotExistingLanguage() {
        val request = MovieUtils.newRequest()
            .copy(languages = listOf(TestConstants.UUID))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with not existing subtitles.
     */
    @Test
    fun updateNotExistingSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = listOf(TestConstants.UUID))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with not existing picture.
     */
    @Test
    fun updateNotExistingPicture() {
        val request = MovieUtils.newRequest()
            .copy(picture = TestConstants.UUID)

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with not existing genre.
     */
    @Test
    fun updateNotExistingGenre() {
        val request = MovieUtils.newRequest()
            .copy(genres = mutableListOf(TestConstants.UUID))

        assertThatThrownBy { facade.update(uuid = MovieUtils.getDomainMovie(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = MovieUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_NOT_EXIST")
            .hasMessageContaining("Movie doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.remove].
     */
    @Test
    fun remove() {
        facade.remove(uuid = MovieUtils.getMovie(index = 1).uuid)
        entityManager.flush()

        assertThat(MovieUtils.getDomainMovie(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT - 1)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT - MovieUtils.getMovie(index = 1).media.size)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_NOT_EXIST")
            .hasMessageContaining("Movie doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedMovie = MovieUtils.getMovie(index = 1)
        val expectedDomainMovie = MovieUtils.getDomainMovie(index = 1)
            .copy(id = MovieUtils.MOVIES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        val expectedDomainMedia = expectedDomainMovie.media.mapIndexed { index, medium ->
            medium.copy(id = MediumUtils.MEDIA_COUNT + index + 1)
                .fillAudit(audit = AuditUtils.newAudit())
        }
        expectedDomainMovie.media.clear()
        expectedDomainMovie.media.addAll(expectedDomainMedia)

        val result = facade.duplicate(uuid = MovieUtils.getMovie(index = 1).uuid)
        entityManager.flush()

        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = result, ignoreUuid = true)
        MovieUtils.assertMovieDeepEquals(expected = expectedDomainMovie, actual = MovieUtils.getDomainMovie(entityManager = entityManager, id = MovieUtils.MOVIES_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT + 1)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT + MovieUtils.getMovie(index = 1).media.size)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_NOT_EXIST")
            .hasMessageContaining("Movie doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

    /**
     * Test method for [MovieFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        val result = facade.getStatistics()

        MovieUtils.assertStatisticsDeepEquals(expected = MovieUtils.getStatistics(), actual = result)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
            it.assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
            it.assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
        }
    }

}
