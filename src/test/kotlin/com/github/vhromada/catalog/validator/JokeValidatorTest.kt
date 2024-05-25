package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.JokeUtils
import com.github.vhromada.catalog.validator.impl.JokeValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [JokeValidator].
 *
 * @author Vladimir Hromada
 */
class JokeValidatorTest {

    /**
     * Instance of [JokeValidator]
     */
    private lateinit var validator: JokeValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = JokeValidatorImpl()
    }

    /**
     * Test method for [JokeValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = JokeUtils.newRequest())
    }

    /**
     * Test method for [JokeValidator.validateRequest] with request with null content.
     */
    @Test
    fun validateRequestNullContent() {
        val request = JokeUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [JokeValidator.validateRequest] with request with empty content.
     */
    @Test
    fun validateRequestEmptyContent() {
        val request = JokeUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
