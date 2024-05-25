package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.RoleUtils
import com.github.vhromada.catalog.validator.impl.RoleValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [RoleValidator].
 *
 * @author Vladimir Hromada
 */
class RoleValidatorTest {

    /**
     * Instance of [RoleValidator]
     */
    private lateinit var validator: RoleValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = RoleValidatorImpl()
    }

    /**
     * Test method for [RoleValidator.validateRequest] with correct request.
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = RoleUtils.newRequest())
    }

    /**
     * Test method for [RoleValidator.validateRequest] with request with null roles.
     */
    @Test
    fun validateRequestNullRoles() {
        val request = RoleUtils.newRequest()
            .copy(roles = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_ROLES_NULL")
            .hasMessageContaining("Roles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [RoleValidator.validateRequest] with request with empty roles.
     */
    @Test
    fun validateRequestEmptyRoles() {
        val request = RoleUtils.newRequest()
            .copy(roles = emptyList())

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_ROLES_EMPTY")
            .hasMessageContaining("Roles mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [RoleValidator.validateRequest] with request with roles with null value.
     */
    @Test
    fun validateRequestNullRole() {
        val request = RoleUtils.newRequest()
            .copy(roles = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_ROLES_CONTAIN_NULL")
            .hasMessageContaining("Roles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
