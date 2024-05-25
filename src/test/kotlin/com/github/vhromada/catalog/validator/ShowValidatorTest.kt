package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.ShowUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.validator.impl.ShowValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [ShowValidator].
 *
 * @author Vladimir Hromada
 */
class ShowValidatorTest {

    /**
     * Instance of [ShowValidator]
     */
    private lateinit var validator: ShowValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = ShowValidatorImpl()
    }

    /**
     * Test method for [ShowValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = ShowUtils.newRequest())
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with null czech name.
     */
    @Test
    fun validateRequestNullCzechName() {
        val request = ShowUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with empty czech name.
     */
    @Test
    fun validateRequestEmptyCzechName() {
        val request = ShowUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with null original name.
     */
    @Test
    fun validateRequestNullOriginalName() {
        val request = ShowUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with empty original name.
     */
    @Test
    fun validateRequestEmptyOriginalName() {
        val request = ShowUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with bad minimal IMDB code.
     */
    @Test
    fun validateRequestBadMinimalImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MIN_IMDB_CODE)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with bad maximal IMDB code.
     */
    @Test
    fun validateRequestBadMaximalImdb() {
        val request = ShowUtils.newRequest()
            .copy(imdbCode = TestConstants.BAD_MAX_IMDB_CODE)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_SHOW_IMDB_CODE_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with null genres.
     */
    @Test
    fun validateRequestNullGenres() {
        val request = ShowUtils.newRequest()
            .copy(genres = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRES_NULL")
            .hasMessageContaining("Genres mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with genres with null value.
     */
    @Test
    fun validateRequestBadGenres() {
        val request = ShowUtils.newRequest()
            .copy(genres = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRES_CONTAIN_NULL")
            .hasMessageContaining("Genres mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ShowValidator.validateRequest] with request with genres with empty genre.
     */
    @Test
    fun validateRequestGenresBadGenre() {
        val request = ShowUtils.newRequest()
            .copy(genres = listOf(""))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SHOW_GENRE_EMPTY")
            .hasMessageContaining("Genre mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
