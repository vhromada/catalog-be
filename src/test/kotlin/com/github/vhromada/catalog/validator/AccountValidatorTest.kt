package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.validator.impl.AccountValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [AccountValidator].
 *
 * @author Vladimir Hromada
 */
class AccountValidatorTest {

    /**
     * Instance of [AccountValidator]
     */
    private lateinit var validator: AccountValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = AccountValidatorImpl()
    }

    /**
     * Test method for [AccountValidator.validateCredentials] with correct credentials.
     */
    @Test
    fun validateCredentials() {
        validator.validateCredentials(credentials = AccountUtils.newCredentials())
    }

    /**
     * Test method for [AccountValidator.validateCredentials] with credentials with null username.
     */
    @Test
    fun validateCredentialsNullUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = null)

        assertThatThrownBy { validator.validateCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_NULL")
            .hasMessageContaining("Username mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AccountValidator.validateCredentials] with credentials with empty username.
     */
    @Test
    fun validateCredentialsEmptyUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = "")

        assertThatThrownBy { validator.validateCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_EMPTY")
            .hasMessageContaining("Username mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AccountValidator.validateCredentials] with credentials with null password.
     */
    @Test
    fun validateCredentialsNullPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = null)

        assertThatThrownBy { validator.validateCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_NULL")
            .hasMessageContaining("Password mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AccountValidator.validateCredentials] with credentials with empty password.
     */
    @Test
    fun validateCredentialsEmptyPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = "")

        assertThatThrownBy { validator.validateCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_EMPTY")
            .hasMessageContaining("Password mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
