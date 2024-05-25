package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.SongUtils
import com.github.vhromada.catalog.validator.impl.SongValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [SongValidator].
 *
 * @author Vladimir Hromada
 */
class SongValidatorTest {

    /**
     * Instance of [SongValidator]
     */
    private lateinit var validator: SongValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = SongValidatorImpl()
    }

    /**
     * Test method for [SongValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = SongUtils.newRequest())
    }

    /**
     * Test method for [SongValidator.validateRequest] with request with null name.
     */
    @Test
    fun validateRequestNullName() {
        val request = SongUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SongValidator.validateRequest] with request with empty name.
     */
    @Test
    fun validateRequestEmptyName() {
        val request = SongUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SongValidator.validateRequest] with request with null length of song.
     */
    @Test
    fun validateRequestNullLength() {
        val request = SongUtils.newRequest()
            .copy(length = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_LENGTH_NULL")
            .hasMessageContaining("Length of song mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SongValidator.validateRequest] with request with negative length of song.
     */
    @Test
    fun validateRequestNegativeLength() {
        val request = SongUtils.newRequest()
            .copy(length = -1)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_LENGTH_NEGATIVE")
            .hasMessageContaining("Length of song mustn't be negative number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
