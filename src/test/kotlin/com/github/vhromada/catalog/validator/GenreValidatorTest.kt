package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.GenreUtils
import com.github.vhromada.catalog.validator.impl.GenreValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [GenreValidator].
 *
 * @author Vladimir Hromada
 */
class GenreValidatorTest {

    /**
     * Instance of [GenreValidator]
     */
    private lateinit var validator: GenreValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = GenreValidatorImpl()
    }

    /**
     * Test method for [GenreValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = GenreUtils.newRequest())
    }

    /**
     * Test method for [GenreValidator.validateRequest] with request with null name.
     */
    @Test
    fun validateRequestNullName() {
        val request = GenreUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GenreValidator.validateRequest] with request with empty name.
     */
    @Test
    fun validateRequestEmptyName() {
        val request = GenreUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
