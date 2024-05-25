package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.EpisodeUtils
import com.github.vhromada.catalog.validator.impl.EpisodeValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [EpisodeValidator].
 *
 * @author Vladimir Hromada
 */
class EpisodeValidatorTest {

    /**
     * Instance of [EpisodeValidator]
     */
    private lateinit var validator: EpisodeValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = EpisodeValidatorImpl()
    }

    /**
     * Test method for [EpisodeValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = EpisodeUtils.newRequest())
    }

    /**
     * Test method for [EpisodeValidator.validateRequest] with request with null number of episode.
     */
    @Test
    fun validateRequestNullNumber() {
        val request = EpisodeUtils.newRequest()
            .copy(number = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NUMBER_NULL")
            .hasMessageContaining("Number of episode mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [EpisodeValidator.validateRequest] with request with not positive number of episode.
     */
    @Test
    fun validateRequestNotPositiveNumber() {
        val request = EpisodeUtils.newRequest()
            .copy(number = -1)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NUMBER_NOT_POSITIVE")
            .hasMessageContaining("Number of episode must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [EpisodeValidator.validateRequest] with request with null name.
     */
    @Test
    fun validateRequestNullName() {
        val request = EpisodeUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [EpisodeValidator.validateRequest] with request with empty name.
     */
    @Test
    fun validateRequestEmptyName() {
        val request = EpisodeUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [EpisodeValidator.validateRequest] with request with null length of episode.
     */
    @Test
    fun validateRequestNullLength() {
        val request = EpisodeUtils.newRequest()
            .copy(length = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_LENGTH_NULL")
            .hasMessageContaining("Length of episode mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [EpisodeValidator.validateRequest] with request with negative length of episode.
     */
    @Test
    fun validateRequestNegativeLength() {
        val request = EpisodeUtils.newRequest()
            .copy(length = -1)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EPISODE_LENGTH_NEGATIVE")
            .hasMessageContaining("Length of episode mustn't be negative number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
