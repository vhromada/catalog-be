package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.MovieUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.validator.impl.MovieValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [MovieValidator].
 *
 * @author Vladimir Hromada
 */
class MovieValidatorTest {

    /**
     * Instance of [MovieValidator]
     */
    private lateinit var validator: MovieValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = MovieValidatorImpl()
    }

    /**
     * Test method for [MovieValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = MovieUtils.newRequest())
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with null czech name.
     */
    @Test
    fun validateRequestNullCzechName() {
        val request = MovieUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with empty czech name.
     */
    @Test
    fun validateRequestEmptyCzechName() {
        val request = MovieUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with null original name.
     */
    @Test
    fun validateRequestNullOriginalName() {
        val request = MovieUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with empty original name.
     */
    @Test
    fun validateRequestEmptyOriginalName() {
        val request = MovieUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with null year.
     */
    @Test
    fun validateRequestNullYear() {
        val request = MovieUtils.newRequest()
            .copy(year = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_YEAR_NULL")
            .hasMessageContaining("Year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with bad minimum year.
     */
    @Test
    fun validateRequestBadMinimumYear() {
        val request = MovieUtils.newRequest()
            .copy(year = TestConstants.BAD_MIN_YEAR)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with bad maximum year.
     */
    @Test
    fun validateRequestBadMaximumYear() {
        val request = MovieUtils.newRequest()
            .copy(year = TestConstants.BAD_MAX_YEAR)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with null languages.
     */
    @Test
    fun validateRequestNullLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_NULL")
            .hasMessageContaining("Languages mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with empty languages.
     */
    @Test
    fun validateRequestEmptyLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = emptyList())

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_EMPTY")
            .hasMessageContaining("Languages mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with languages with null value.
     */
    @Test
    fun validateRequestBadEmptyLanguages() {
        val request = MovieUtils.newRequest()
            .copy(languages = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_LANGUAGES_CONTAIN_NULL")
            .hasMessageContaining("Languages mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with null subtitles.
     */
    @Test
    fun validateRequestNullSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_SUBTITLES_NULL")
            .hasMessageContaining("Subtitles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with subtitles with null value.
     */
    @Test
    fun validateRequestBadSubtitles() {
        val request = MovieUtils.newRequest()
            .copy(subtitles = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_SUBTITLES_CONTAIN_NULL")
            .hasMessageContaining("Subtitles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with null media.
     */
    @Test
    fun validateRequestNullMedia() {
        val request = MovieUtils.newRequest()
            .copy(media = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIA_NULL")
            .hasMessageContaining("Media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with media with null value.
     */
    @Test
    fun validateRequestBadMedia() {
        val request = MovieUtils.newRequest()
            .copy(media = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIA_CONTAIN_NULL")
            .hasMessageContaining("Media mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with media with negative value as medium.
     */
    @Test
    fun validateRequestMediaBadMedium() {
        val request = MovieUtils.newRequest()
            .copy(media = listOf(-1))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_MEDIUM_NOT_POSITIVE")
            .hasMessageContaining("Medium must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with bad minimal IMDB code.
     */
    @Test
    fun validateRequestBadMinimalImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MIN_IMDB_CODE)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with bad maximal IMDB code.
     */
    @Test
    fun validateRequestBadMaximalImdb() {
        val request = MovieUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MAX_IMDB_CODE)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_MOVIE_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with null genres.
     */
    @Test
    fun validateRequestNullGenres() {
        val request = MovieUtils.newRequest()
            .copy(genres = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRES_NULL")
            .hasMessageContaining("Genres mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with genres with null value.
     */
    @Test
    fun validateRequestBadGenres() {
        val request = MovieUtils.newRequest()
            .copy(genres = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRES_CONTAIN_NULL")
            .hasMessageContaining("Genres mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MovieValidator.validateRequest] with request with genres with empty genre.
     */
    @Test
    fun validateRequestGenresBadGenre() {
        val request = MovieUtils.newRequest()
            .copy(genres = listOf(""))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_GENRE_EMPTY")
            .hasMessageContaining("Genre mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
