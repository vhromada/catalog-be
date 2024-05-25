package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.SeasonUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.validator.impl.SeasonValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [SeasonValidator].
 *
 * @author Vladimir Hromada
 */
class SeasonValidatorTest {

    /**
     * Instance of [SeasonValidator]
     */
    private lateinit var validator: SeasonValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = SeasonValidatorImpl()
    }

    /**
     * Test method for [SeasonValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = SeasonUtils.newRequest())
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with null number of season.
     */
    @Test
    fun validateRequestNullNumber() {
        val request = SeasonUtils.newRequest()
            .copy(number = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NUMBER_NULL")
            .hasMessageContaining("Number of season mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with not positive number of season.
     */
    @Test
    fun validateRequestNotPositiveNumber() {
        val request = SeasonUtils.newRequest()
            .copy(number = -1)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_NUMBER_NOT_POSITIVE")
            .hasMessageContaining("Number of season must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with null starting year.
     */
    @Test
    fun validateRequestNullStartingYear() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_START_YEAR_NULL")
            .hasMessageContaining("Starting year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with null ending year.
     */
    @Test
    fun validateRequestNullEndingYear() {
        val request = SeasonUtils.newRequest()
            .copy(endYear = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_END_YEAR_NULL")
            .hasMessageContaining("Ending year mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with bad minimum starting year and bad minimum ending year.
     */
    @Test
    fun validateRequestBadMinimumYears() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = TestConstants.BAD_MIN_YEAR, endYear = TestConstants.BAD_MIN_YEAR)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_STARTING_YEAR_EVENT.toString())
            .hasMessageContaining(TestConstants.INVALID_ENDING_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with bad maximum starting year and bad maximum ending year.
     */
    @Test
    fun validateRequestBadMaximumYears() {
        val request = SeasonUtils.newRequest()
            .copy(startYear = TestConstants.BAD_MAX_YEAR, endYear = TestConstants.BAD_MAX_YEAR)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining(TestConstants.INVALID_STARTING_YEAR_EVENT.toString())
            .hasMessageContaining(TestConstants.INVALID_ENDING_YEAR_EVENT.toString())
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with starting year greater than ending yea.
     */
    @Test
    fun validateRequestBadYears() {
        var request = SeasonUtils.newRequest()
        request = request.copy(startYear = request.endYear!! + 1)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_YEARS_NOT_VALID")
            .hasMessageContaining("Starting year mustn't be greater than ending year.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with null language.
     */
    @Test
    fun validateRequestNullLanguage() {
        val request = SeasonUtils.newRequest()
            .copy(language = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_LANGUAGE_NULL")
            .hasMessageContaining("Language mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with null subtitles.
     */
    @Test
    fun validateRequestNullSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_SUBTITLES_NULL")
            .hasMessageContaining("Subtitles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SeasonValidator.validateRequest] with request with subtitles with null value.
     */
    @Test
    fun validateRequestBadSubtitles() {
        val request = SeasonUtils.newRequest()
            .copy(subtitles = listOf(null))

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SEASON_SUBTITLES_CONTAIN_NULL")
            .hasMessageContaining("Subtitles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
