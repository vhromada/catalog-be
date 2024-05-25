package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuthorUtils
import com.github.vhromada.catalog.validator.impl.AuthorValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [AuthorValidator].
 *
 * @author Vladimir Hromada
 */
class AuthorValidatorTest {

    /**
     * Instance of [AuthorValidator]
     */
    private lateinit var validator: AuthorValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = AuthorValidatorImpl()
    }

    /**
     * Test method for [AuthorValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = AuthorUtils.newRequest())
    }

    /**
     * Test method for [AuthorValidator.validateRequest] with request with null first name.
     */
    @Test
    fun validateRequestNullFirstName() {
        val request = AuthorUtils.newRequest()
            .copy(firstName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_FIRST_NAME_NULL")
            .hasMessageContaining("First name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AuthorValidator.validateRequest] with request with empty first name.
     */
    @Test
    fun validateRequestEmptyFirstName() {
        val request = AuthorUtils.newRequest()
            .copy(firstName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_FIRST_NAME_EMPTY")
            .hasMessageContaining("First name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AuthorValidator.validateRequest] with request with null last name.
     */
    @Test
    fun validateRequestNullLastName() {
        val request = AuthorUtils.newRequest()
            .copy(lastName = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_LAST_NAME_NULL")
            .hasMessageContaining("Last name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AuthorValidator.validateRequest] with request with empty last name.
     */
    @Test
    fun validateRequestEmptyLastName() {
        val request = AuthorUtils.newRequest()
            .copy(lastName = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_LAST_NAME_EMPTY")
            .hasMessageContaining("Last name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
