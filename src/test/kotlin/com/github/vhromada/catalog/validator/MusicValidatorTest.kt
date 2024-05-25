package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.validator.impl.MusicValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [MusicValidator].
 *
 * @author Vladimir Hromada
 */
class MusicValidatorTest {

    /**
     * Instance of [MusicValidator]
     */
    private lateinit var validator: MusicValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = MusicValidatorImpl()
    }

    /**
     * Test method for [MusicValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = MusicUtils.newRequest())
    }

    /**
     * Test method for [MusicValidator.validateRequest] with request with null name.
     */
    @Test
    fun validateRequestNullName() {
        val request = MusicUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MusicValidator.validateRequest] with request with empty name.
     */
    @Test
    fun validateRequestEmptyName() {
        val request = MusicUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MusicValidator.validateRequest] with request with null count of media.
     */
    @Test
    fun validateRequestNullMediaCount() {
        val request = MusicUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MusicValidator.validateRequest] with request with not positive count of media.
     */
    @Test
    fun validateRequestNotPositiveMediaCount() {
        val request = MusicUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
