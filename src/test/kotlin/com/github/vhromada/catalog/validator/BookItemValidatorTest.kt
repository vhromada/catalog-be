package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.BookItemUtils
import com.github.vhromada.catalog.validator.impl.BookItemValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [BookItemValidator].
 *
 * @author Vladimir Hromada
 */
class BookItemValidatorTest {

    /**
     * Instance of [BookItemValidator]
     */
    private lateinit var validator: BookItemValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = BookItemValidatorImpl()
    }

    /**
     * Test method for [BookItemValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = BookItemUtils.newRequest())
    }

    /**
     * Test method for [BookItemValidator.validateRequest] with request with null languages.
     */
    @Test
    fun validateRequestNullLanguages() {
        val request = BookItemUtils.newRequest()
            .copy(languages = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_LANGUAGES_NULL")
            .hasMessageContaining("Languages mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookItemValidator.validateRequest] with request with languages with null value.
     */
    @Test
    fun validateRequestBadLanguages() {
        val request = BookItemUtils.newRequest()
            .copy(languages = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_LANGUAGES_CONTAIN_NULL")
            .hasMessageContaining("Languages mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookItemValidator.validateRequest] with request with null format.
     */
    @Test
    fun validateRequestNullFormat() {
        val request = BookItemUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ITEM_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
