package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.CheatDataUtils
import com.github.vhromada.catalog.utils.CheatUtils
import com.github.vhromada.catalog.validator.impl.CheatValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [CheatValidator].
 *
 * @author Vladimir Hromada
 */
class CheatValidatorTest {

    /**
     * Instance of [CheatValidator]
     */
    private lateinit var validator: CheatValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = CheatValidatorImpl()
    }

    /**
     * Test method for [CheatValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = CheatUtils.newRequest())
    }

    /**
     * Test method for [CheatValidator.validateRequest] with request with null cheat's data.
     */
    @Test
    fun validateRequestNullCheatData() {
        val request = CheatUtils.newRequest()
            .copy(data = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_NULL")
            .hasMessageContaining("Cheat's data mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [CheatValidator.validateRequest] with request with cheat's data with null value.
     */
    @Test
    fun validateRequestBadCheatData() {
        val request = CheatUtils.newRequest()
            .copy(data = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_CONTAIN_NULL")
            .hasMessageContaining("Cheat's data mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [CheatValidator.validateRequest] with request with cheat's data with null action.
     */
    @Test
    fun validateRequestCheatDataWithNullAction() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(action = null)
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_ACTION_NULL")
            .hasMessageContaining("Cheat's data action mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [CheatValidator.validateRequest] with request with cheat's data with empty action.
     */
    @Test
    fun validateRequestCheatDataWithEmptyAction() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(action = "")
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_ACTION_EMPTY")
            .hasMessageContaining("Cheat's data action mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [CheatValidator.validateRequest] with request with cheat's data with null description.
     */
    @Test
    fun validateRequestCheatDataWithNullDescription() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(description = null)
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_DESCRIPTION_NULL")
            .hasMessageContaining("Cheat's data description mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [CheatValidator.validateRequest] with request with cheat's data with empty description.
     */
    @Test
    fun validateRequestCheatDataWithEmptyDescription() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(description = "")
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_DESCRIPTION_EMPTY")
            .hasMessageContaining("Cheat's data description mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
