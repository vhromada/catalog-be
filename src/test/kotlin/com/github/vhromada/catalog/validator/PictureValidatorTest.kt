package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.PictureUtils
import com.github.vhromada.catalog.validator.impl.PictureValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [PictureValidator].
 *
 * @author Vladimir Hromada
 */
class PictureValidatorTest {

    /**
     * Instance of [PictureValidator]
     */
    private lateinit var validator: PictureValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = PictureValidatorImpl()
    }

    /**
     * Test method for [PictureValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = PictureUtils.newRequest())
    }

    /**
     * Test method for [PictureValidator.validateRequest] with request with null content.
     */
    @Test
    fun validateRequestNullContent() {
        val request = PictureUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [PictureValidator.validateRequest] with request with empty content.
     */
    @Test
    fun validateRequestEmptyContent() {
        val request = PictureUtils.newRequest()
            .copy(content = ByteArray(0))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
