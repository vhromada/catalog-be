package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
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
 * A class represents test for class [CheatFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class CheatFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [CheatFacade]
     */
    @Autowired
    private lateinit var facade: CheatFacade

    /**
     * Test method for [CheatFacade.find].
     */
    @Test
    fun find() {
        for (i in 2..GameUtils.GAMES_COUNT) {
            val game = GameUtils.getDomainGame(index = i)

            val result = facade.find(game = game.uuid)

            CheatUtils.assertCheatDeepEquals(expected = CheatUtils.getDomainCheat(index = i - 1), actual = result)
        }

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.find] with not existing game.
     */
    @Test
    fun findNotExistingGame() {
        assertThatThrownBy { facade.find(game = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.find] with not existing UUID.
     */
    @Test
    fun findNotExisting() {
        assertThatThrownBy { facade.find(game = GameUtils.getDomainGame(index = 1).uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_NOT_EXIST")
            .hasMessageContaining("Cheat doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.get].
     */
    @Test
    fun get() {
        for (i in 2..GameUtils.GAMES_COUNT) {
            val game = GameUtils.getDomainGame(index = i)
            val result = facade.get(game = game.uuid, uuid = game.cheat!!.uuid)

            CheatUtils.assertCheatDeepEquals(expected = game.cheat!!, actual = result)
        }

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.get] with not existing game.
     */
    @Test
    fun getNotExistingGame() {
        assertThatThrownBy { facade.get(game = TestConstants.UUID, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(game = GameUtils.getDomainGame(index = 2).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_NOT_EXIST")
            .hasMessageContaining("Cheat doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val game = GameUtils.getDomainGame(index = 1)
            .fillAudit(audit = AuditUtils.updatedAudit())
        val expectedCheat = CheatUtils.newCheat()
        val expectedDomainCheat = CheatUtils.newDomainCheat(id = CheatUtils.CHEATS_COUNT + 1)
            .copy(data = mutableListOf(CheatDataUtils.newDomainCheatData(id = CheatDataUtils.CHEAT_DATA_COUNT + 1).fillAudit(audit = AuditUtils.newAudit())))
            .fillAudit(audit = AuditUtils.newAudit())
        expectedDomainCheat.game = game

        val result = facade.add(game = game.uuid, request = CheatUtils.newRequest())
        entityManager.flush()

        CheatUtils.assertCheatDeepEquals(expected = expectedCheat, actual = result, ignoreUuid = true)
        CheatUtils.assertCheatDeepEquals(expected = expectedDomainCheat, actual = CheatUtils.getDomainCheat(entityManager = entityManager, id = CheatUtils.CHEATS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT + 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT + 1)
        }
    }

    /**
     * Test method for [CheatFacade.add] with request with null cheat's data.
     */
    @Test
    fun addNullCheatData() {
        val request = CheatUtils.newRequest()
            .copy(data = null)

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_NULL")
            .hasMessageContaining("Cheat's data mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add] with request with cheat's data with null value.
     */
    @Test
    fun addBadCheatData() {
        val request = CheatUtils.newRequest()
            .copy(data = listOf(null))

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_CONTAIN_NULL")
            .hasMessageContaining("Cheat's data mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add] with request with cheat's data with null action.
     */
    @Test
    fun addCheatDataWithNullAction() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(action = null)
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_ACTION_NULL")
            .hasMessageContaining("Cheat's data action mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add] with request with cheat's data with empty action.
     */
    @Test
    fun addCheatDataWithEmptyAction() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(action = "")
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_ACTION_EMPTY")
            .hasMessageContaining("Cheat's data action mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add] with request with cheat's data with null description.
     */
    @Test
    fun addCheatDataWithNullDescription() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(description = null)
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_DESCRIPTION_NULL")
            .hasMessageContaining("Cheat's data description mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add] with request with cheat's data with empty description.
     */
    @Test
    fun addCheatDataWithEmptyDescription() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(description = "")
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_DESCRIPTION_EMPTY")
            .hasMessageContaining("Cheat's data description mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add] with existing cheat.
     */
    @Test
    fun addExistingCheat() {
        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 2).uuid, request = CheatUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_EXIST")
            .hasMessageContaining("Cheat already exists.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.add] with not existing game.
     */
    @Test
    fun addNotExistingGame() {
        assertThatThrownBy { facade.add(game = TestConstants.UUID, request = CheatUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val game = GameUtils.getDomainGame(index = 2)
        val request = CheatUtils.newRequest()
        val expectedCheat = CheatUtils.getCheat(index = 1)
            .copy(data = listOf(CheatDataUtils.newCheatData()))
            .updated()
        val expectedDomainCheat = game.cheat!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedDomainCheat.data.clear()
        expectedDomainCheat.data.add(GameUtils.getDomainGame(index = 2).cheat!!.data.first().updated().fillAudit(audit = AuditUtils.updatedAudit()))

        val result = facade.update(game = game.uuid, uuid = game.cheat!!.uuid, request = request)
        entityManager.flush()

        CheatUtils.assertCheatDeepEquals(expected = expectedCheat, actual = result)
        CheatUtils.assertCheatDeepEquals(expected = expectedDomainCheat, actual = CheatUtils.getDomainCheat(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT - CheatDataUtils.CHEAT_DATA_CHEAT_COUNT + 1)
        }
    }

    /**
     * Test method for [CheatFacade.update] with request with null cheat's data.
     */
    @Test
    fun updateNullCheatData() {
        val request = CheatUtils.newRequest()
            .copy(data = null)

        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 2).uuid, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_NULL")
            .hasMessageContaining("Cheat's data mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update] with request with cheat's data with null value.
     */
    @Test
    fun updateBadCheatData() {
        val request = CheatUtils.newRequest()
            .copy(data = listOf(null))

        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 2).uuid, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_CONTAIN_NULL")
            .hasMessageContaining("Cheat's data mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update] with request with cheat's data with null action.
     */
    @Test
    fun updateCheatDataWithNullAction() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(action = null)
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 2).uuid, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_ACTION_NULL")
            .hasMessageContaining("Cheat's data action mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update] with request with cheat's data with empty action.
     */
    @Test
    fun updateCheatDataWithEmptyAction() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(action = "")
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 2).uuid, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_ACTION_EMPTY")
            .hasMessageContaining("Cheat's data action mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update] with request with cheat's data with null description.
     */
    @Test
    fun updateCheatDataWithNullDescription() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(description = null)
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 2).uuid, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_DESCRIPTION_NULL")
            .hasMessageContaining("Cheat's data description mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update] with request with cheat's data with empty description.
     */
    @Test
    fun updateCheatDataWithEmptyDescription() {
        val cheatData = CheatDataUtils.newRequest()
            .copy(description = "")
        val request = CheatUtils.newRequest()
            .copy(data = listOf(cheatData))

        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 2).uuid, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_DATA_DESCRIPTION_EMPTY")
            .hasMessageContaining("Cheat's data description mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update] with not existing game.
     */
    @Test
    fun updateNotExistingGame() {
        assertThatThrownBy { facade.update(game = TestConstants.UUID, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid, request = CheatUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 2).uuid, uuid = TestConstants.UUID, request = CheatUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_NOT_EXIST")
            .hasMessageContaining("Cheat doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getDomainGame(index = 2)

        facade.remove(game = game.uuid, uuid = game.cheat!!.uuid)
        entityManager.flush()

        assertThat(CheatUtils.getDomainCheat(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT - 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT - CheatDataUtils.CHEAT_DATA_CHEAT_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.remove] with not existing game.
     */
    @Test
    fun removeNotExistingGame() {
        assertThatThrownBy { facade.remove(game = TestConstants.UUID, uuid = GameUtils.getDomainGame(index = 2).cheat!!.uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for [CheatFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(game = GameUtils.getDomainGame(index = 2).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_NOT_EXIST")
            .hasMessageContaining("Cheat doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

}
