package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.ProgramUtils
import com.github.vhromada.catalog.validator.impl.ProgramValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [ProgramValidator].
 *
 * @author Vladimir Hromada
 */
class ProgramValidatorTest {

    /**
     * Instance of [ProgramValidator]
     */
    private lateinit var validator: ProgramValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = ProgramValidatorImpl()
    }

    /**
     * Test method for [ProgramValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = ProgramUtils.newRequest())
    }

    /**
     * Test method for [ProgramValidator.validateRequest] with request with null name.
     */
    @Test
    fun validateRequestNullName() {
        val request = ProgramUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ProgramValidator.validateRequest] with request with empty name.
     */
    @Test
    fun validateRequestEmptyName() {
        val request = ProgramUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ProgramValidator.validateRequest] with request with null count of media.
     */
    @Test
    fun validateRequestNullMediaCount() {
        val request = ProgramUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ProgramValidator.validateRequest] with request with not positive count of media.
     */
    @Test
    fun validateRequestNotPositiveMediaCount() {
        val request = ProgramUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ProgramValidator.validateRequest] with request with null format.
     */
    @Test
    fun validateRequestNullFormat() {
        val request = ProgramUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ProgramValidator.validateRequest] with request with null crack.
     */
    @Test
    fun validateRequestNullCrack() {
        val request = ProgramUtils.newRequest()
            .copy(crack = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_CRACK_NULL")
            .hasMessageContaining("Crack mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ProgramValidator.validateRequest] with request with null serial key.
     */
    @Test
    fun validateRequestNullSerialKey() {
        val request = ProgramUtils.newRequest()
            .copy(serialKey = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_SERIAL_KEY_NULL")
            .hasMessageContaining("Serial key mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
