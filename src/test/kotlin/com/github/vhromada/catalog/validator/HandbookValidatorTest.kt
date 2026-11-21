package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.HandbookUtils
import com.github.vhromada.catalog.validator.impl.HandbookValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [HandbookValidator].
 *
 * @author Vladimir Hromada
 */
class HandbookValidatorTest {

    /**
     * Instance of [HandbookValidator]
     */
    private lateinit var validator: HandbookValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = HandbookValidatorImpl()
    }

    /**
     * Test method for [HandbookValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = HandbookUtils.newRequest())
    }

    /**
     * Test method for [HandbookValidator.validateRequest] with request with null content.
     */
    @Test
    fun validateRequestNullContent() {
        val request = HandbookUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)
    }

    /**
     * Test method for [HandbookValidator.validateRequest] with request with empty content.
     */
    @Test
    fun validateRequestEmptyContent() {
        val request = HandbookUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)
    }

}
