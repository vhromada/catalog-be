package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.CheatDataUtils
import com.github.vhromada.catalog.utils.CheatUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [GameFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class GameFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [GameFacade]
     */
    @Autowired
    private lateinit var facade: GameFacade

    /**
     * Test method for [GameFacade.search].
     */
    @Test
    fun search() {
        val filter = NameFilter()
        filter.page = 1
        filter.limit = GameUtils.GAMES_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        GameUtils.assertGameListDeepEquals(expected = GameUtils.getGames(), actual = result.data)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = 1
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(GameUtils.GAMES_COUNT)
        }
        GameUtils.assertGameListDeepEquals(expected = listOf(GameUtils.getGame(index = 2)), actual = result.data)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = GameUtils.GAMES_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..GameUtils.GAMES_COUNT) {
            val game = GameUtils.getGame(index = i)
            val filter = NameFilter(name = game.name)
            filter.page = 1
            filter.limit = GameUtils.GAMES_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            GameUtils.assertGameListDeepEquals(expected = listOf(game), actual = result.data)
        }

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..GameUtils.GAMES_COUNT) {
            val game = GameUtils.getGame(index = i)

            val result = facade.get(uuid = game.uuid)

            GameUtils.assertGameDeepEquals(expected = GameUtils.getGame(index = i), actual = result)
        }

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedGame = GameUtils.newGame()
        val expectedDomainGame = GameUtils.newDomainGame(id = GameUtils.GAMES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = GameUtils.newRequest())
        entityManager.flush()

        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = result, ignoreUuid = true)
        GameUtils.assertGameDeepEquals(expected = expectedDomainGame, actual = GameUtils.getDomainGame(entityManager = entityManager, id = GameUtils.GAMES_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT + 1)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with request with null name.
     */
    @Test
    fun addNullName() {
        val request = GameUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with request with empty string as name.
     */
    @Test
    fun addEmptyName() {
        val request = GameUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null count of media.
     */
    @Test
    fun addNullMediaCount() {
        val request = GameUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with not positive count of media.
     */
    @Test
    fun addNotPositiveMediaCount() {
        val request = GameUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null format.
     */
    @Test
    fun addNullFormat() {
        val request = GameUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null crack.
     */
    @Test
    fun addNullCrack() {
        val request = GameUtils.newRequest()
            .copy(crack = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_CRACK_NULL")
            .hasMessageContaining("Crack mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null serial key.
     */
    @Test
    fun addNullSerialKey() {
        val request = GameUtils.newRequest()
            .copy(serialKey = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_SERIAL_KEY_NULL")
            .hasMessageContaining("Serial key mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null patch.
     */
    @Test
    fun addNullPatch() {
        val request = GameUtils.newRequest()
            .copy(patch = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_PATCH_NULL")
            .hasMessageContaining("Patch mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null trainer.
     */
    @Test
    fun addNullTrainer() {
        val request = GameUtils.newRequest()
            .copy(trainer = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_TRAINER_NULL")
            .hasMessageContaining("Trainer mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null data for trainer.
     */
    @Test
    fun addNullTrainerData() {
        val request = GameUtils.newRequest()
            .copy(trainerData = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_TRAINER_DATA_NULL")
            .hasMessageContaining("Data for trainer mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null editor.
     */
    @Test
    fun addNullEditor() {
        val request = GameUtils.newRequest()
            .copy(editor = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_EDITOR_NULL")
            .hasMessageContaining("Editor mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with null saves.
     */
    @Test
    fun addNullSaves() {
        val request = GameUtils.newRequest()
            .copy(saves = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_SAVES_NULL")
            .hasMessageContaining("Saves mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.add] with game with not existing format.
     */
    @Test
    fun addNotExistingFormat() {
        val request = GameUtils.newRequest()
            .copy(format = TestConstants.UUID)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = GameUtils.newRequest()
        val expectedGame = GameUtils.getGame(index = 1)
            .updated()
        val expectedDomainGame = GameUtils.getDomainGame(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request)
        entityManager.flush()

        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = result)
        GameUtils.assertGameDeepEquals(expected = expectedDomainGame, actual = GameUtils.getDomainGame(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with request with null name.
     */
    @Test
    fun updateNullName() {
        val request = GameUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with request with empty string as name.
     */
    @Test
    fun updateEmptyName() {
        val request = GameUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GameFacade.update] with game with null count of media.
     */
    @Test
    fun updateNullMediaCount() {
        val request = GameUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with not positive count of media.
     */
    @Test
    fun updateNotPositiveMediaCount() {
        val request = GameUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null format.
     */
    @Test
    fun updateNullFormat() {
        val request = GameUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null crack.
     */
    @Test
    fun updateNullCrack() {
        val request = GameUtils.newRequest()
            .copy(crack = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_CRACK_NULL")
            .hasMessageContaining("Crack mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null serial key.
     */
    @Test
    fun updateNullSerialKey() {
        val request = GameUtils.newRequest()
            .copy(serialKey = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_SERIAL_KEY_NULL")
            .hasMessageContaining("Serial key mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null patch.
     */
    @Test
    fun updateNullPatch() {
        val request = GameUtils.newRequest()
            .copy(patch = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_PATCH_NULL")
            .hasMessageContaining("Patch mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null trainer.
     */
    @Test
    fun updateNullTrainer() {
        val request = GameUtils.newRequest()
            .copy(trainer = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_TRAINER_NULL")
            .hasMessageContaining("Trainer mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null data for trainer.
     */
    @Test
    fun updateNullTrainerData() {
        val request = GameUtils.newRequest()
            .copy(trainerData = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_TRAINER_DATA_NULL")
            .hasMessageContaining("Data for trainer mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null editor.
     */
    @Test
    fun updateNullEditor() {
        val request = GameUtils.newRequest()
            .copy(editor = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_EDITOR_NULL")
            .hasMessageContaining("Editor mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with game with null saves.
     */
    @Test
    fun updateNullSaves() {
        val request = GameUtils.newRequest()
            .copy(saves = null)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_SAVES_NULL")
            .hasMessageContaining("Saves mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with not existing format.
     */
    @Test
    fun updateNotExistingFormat() {
        val request = GameUtils.newRequest()
            .copy(format = TestConstants.UUID)

        assertThatThrownBy { facade.update(uuid = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = GameUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.remove].
     */
    @Test
    fun remove() {
        facade.remove(uuid = GameUtils.getGame(index = 1).uuid)
        entityManager.flush()

        assertThat(GameUtils.getDomainGame(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT - 1)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedGame = GameUtils.getGame(index = 1)
        val expectedDomainGame = GameUtils.getDomainGame(index = 1)
            .copy(id = GameUtils.GAMES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.duplicate(uuid = GameUtils.getGame(index = 1).uuid)
        entityManager.flush()

        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = result, ignoreUuid = true)
        GameUtils.assertGameDeepEquals(expected = expectedDomainGame, actual = GameUtils.getDomainGame(entityManager = entityManager, id = GameUtils.GAMES_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT + 1)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [GameFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        val result = facade.getStatistics()

        GameUtils.assertStatisticsDeepEquals(expected = GameUtils.getDomainStatistics(), actual = result)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

}
