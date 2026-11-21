package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.WalkthroughUtils
import com.github.vhromada.catalog.validator.impl.WalkthroughValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [WalkthroughValidator].
 *
 * @author Vladimir Hromada
 */
class WalkthroughValidatorTest {

    /**
     * Instance of [WalkthroughValidator]
     */
    private lateinit var validator: WalkthroughValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = WalkthroughValidatorImpl()
    }

    /**
     * Test method for [WalkthroughValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = WalkthroughUtils.newRequest())
    }

    /**
     * Test method for [WalkthroughValidator.validateRequest] with request with null content.
     */
    @Test
    fun validateRequestNullContent() {
        val request = WalkthroughUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)
    }

    /**
     * Test method for [WalkthroughValidator.validateRequest] with request with empty content.
     */
    @Test
    fun validateRequestEmptyContent() {
        val request = WalkthroughUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)
    }

}
