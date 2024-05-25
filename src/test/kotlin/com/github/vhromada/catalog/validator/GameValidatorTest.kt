package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.validator.impl.GameValidatorImpl
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

/**
 * A class represents test for class [GameValidator].
 *
 * @author Vladimir Hromada
 */
class GameValidatorTest {

    /**
     * Instance of [GameValidator]
     */
    private lateinit var validator: GameValidator

    /**
     * Initializes validator.
     */
    @BeforeEach
    fun setUp() {
        validator = GameValidatorImpl()
    }

    /**
     * Test method for [GameValidator.validateRequest].
     */
    @Test
    fun validateRequest() {
        validator.validateRequest(request = GameUtils.newRequest())
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null name.
     */
    @Test
    fun validateRequestNullName() {
        val request = GameUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with empty name.
     */
    @Test
    fun validateRequestEmptyName() {
        val request = GameUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null count of media.
     */
    @Test
    fun validateRequestNullMediaCount() {
        val request = GameUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with not positive count of media.
     */
    @Test
    fun validateRequestNotPositiveMediaCount() {
        val request = GameUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null format.
     */
    @Test
    fun validateRequestNullFormat() {
        val request = GameUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null crack.
     */
    @Test
    fun validateRequestNullCrack() {
        val request = GameUtils.newRequest()
            .copy(crack = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_CRACK_NULL")
            .hasMessageContaining("Crack mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null serial key.
     */
    @Test
    fun validateRequestNullSerialKey() {
        val request = GameUtils.newRequest()
            .copy(serialKey = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_SERIAL_KEY_NULL")
            .hasMessageContaining("Serial key mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null patch.
     */
    @Test
    fun validateRequestNullPatch() {
        val request = GameUtils.newRequest()
            .copy(patch = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_PATCH_NULL")
            .hasMessageContaining("Patch mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null trainer.
     */
    @Test
    fun validateRequestNullTrainer() {
        val request = GameUtils.newRequest()
            .copy(trainer = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_TRAINER_NULL")
            .hasMessageContaining("Trainer mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null data for trainer.
     */
    @Test
    fun validateRequestNullTrainerData() {
        val request = GameUtils.newRequest()
            .copy(trainerData = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_TRAINER_DATA_NULL")
            .hasMessageContaining("Data for trainer mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null editor.
     */
    @Test
    fun validateRequestNullEditor() {
        val request = GameUtils.newRequest()
            .copy(editor = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_EDITOR_NULL")
            .hasMessageContaining("Editor mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameValidator.validateRequest] with request with null saves.
     */
    @Test
    fun validateRequestNullSaves() {
        val request = GameUtils.newRequest()
            .copy(saves = null)

        assertThatThrownBy { validator.validateRequest(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_SAVES_NULL")
            .hasMessageContaining("Saves mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

}
