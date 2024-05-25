package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.BookUtils
import com.github.vhromada.catalog.validator.impl.BookValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [BookValidator].
 *
 * @author Vladimir Hromada
 */
class BookValidatorTest {

    /**
     * Instance of [BookValidator]
     */
    private lateinit var validator: BookValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = BookValidatorImpl()
    }

    /**
     * Test method for [BookValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = BookUtils.newRequest())
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with null czech name.
     */
    @Test
    fun validateRequestNullCzechName() {
        val request = BookUtils.newRequest()
            .copy(czechName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_CZECH_NAME_NULL")
            .hasMessageContaining("Czech name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with empty czech name.
     */
    @Test
    fun validateRequestEmptyCzechName() {
        val request = BookUtils.newRequest()
            .copy(czechName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_CZECH_NAME_EMPTY")
            .hasMessageContaining("Czech name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with null original name.
     */
    @Test
    fun validateRequestNullOriginalName() {
        val request = BookUtils.newRequest()
            .copy(originalName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ORIGINAL_NAME_NULL")
            .hasMessageContaining("Original name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with empty original name.
     */
    @Test
    fun validateRequestEmptyOriginalName() {
        val request = BookUtils.newRequest()
            .copy(originalName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_ORIGINAL_NAME_EMPTY")
            .hasMessageContaining("Original name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with null description.
     */
    @Test
    fun validateRequestNullDescription() {
        val request = BookUtils.newRequest()
            .copy(description = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_DESCRIPTION_NULL")
            .hasMessageContaining("Description mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with empty description.
     */
    @Test
    fun validateRequestEmptyDescription() {
        val request = BookUtils.newRequest()
            .copy(description = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_DESCRIPTION_EMPTY")
            .hasMessageContaining("Description mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with null authors.
     */
    @Test
    fun validateRequestNullAuthors() {
        val request = BookUtils.newRequest()
            .copy(authors = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHORS_NULL")
            .hasMessageContaining("Authors mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with authors with null value.
     */
    @Test
    fun validateRequestBadAuthors() {
        val request = BookUtils.newRequest()
            .copy(authors = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHORS_CONTAIN_NULL")
            .hasMessageContaining("Authors mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [BookValidator.validateRequest] with request with authors with empty author.
     */
    @Test
    fun validateRequestAuthorsBadAuthor() {
        val request = BookUtils.newRequest()
            .copy(authors = listOf(""))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("BOOK_AUTHOR_EMPTY")
            .hasMessageContaining("Author mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
