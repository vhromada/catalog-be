package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.HandbookUtils
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
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [HandbookFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class HandbookFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [HandbookFacade]
     */
    @Autowired
    private lateinit var facade: HandbookFacade

    /**
     * Test method for [HandbookFacade.find].
     */
    @Test
    fun find() {
        for (i in listOf(1, 2)) {
            val game = GameUtils.getDomainGame(index = i)

            val result = facade.find(game = game.uuid)

            HandbookUtils.assertHandbookDeepEquals(expected = game.handbook!!, actual = result)
        }

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.find] with not existing game.
     */
    @Test
    fun findNotExistingGame() {
        assertThatThrownBy { facade.find(game = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.find] with not existing handbook.
     */
    @Test
    fun findNotExisting() {
        assertThatThrownBy { facade.find(game = GameUtils.getDomainGame(index = 3).uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_NOT_EXIST")
            .hasMessageContaining("Handbook doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.get].
     */
    @Test
    fun get() {
        for (i in listOf(1, 2)) {
            val game = GameUtils.getDomainGame(index = i)

            val result = facade.get(game = game.uuid, uuid = game.handbook!!.uuid)

            HandbookUtils.assertHandbookDeepEquals(expected = game.handbook!!, actual = result)
        }

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.get] with not existing game.
     */
    @Test
    fun getNotExistingGame() {
        assertThatThrownBy { facade.get(game = TestConstants.UUID, uuid = GameUtils.getDomainGame(index = 1).handbook!!.uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(game = GameUtils.getDomainGame(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_NOT_EXIST")
            .hasMessageContaining("Handbook doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.add].
     */
    @Test
    fun add() {
        val game = GameUtils.getDomainGame(index = 3)
            .fillAudit(audit = AuditUtils.updatedAudit())
        val expectedHandbook = HandbookUtils.newHandbook()
        val expectedDomainHandbook = HandbookUtils.newDomainHandbook(id = HandbookUtils.HANDBOOKS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedDomainHandbook.game = game

        val result = facade.add(game = game.uuid, request = HandbookUtils.newRequest())
        entityManager.flush()

        HandbookUtils.assertHandbookDeepEquals(expected = expectedHandbook, actual = result, ignoreUuid = true)
        HandbookUtils.assertHandbookDeepEquals(
            expected = expectedDomainHandbook,
            actual = HandbookUtils.getDomainHandbook(entityManager = entityManager, id = HandbookUtils.HANDBOOKS_COUNT + 1),
            ignoreUuid = true
        )

        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT + 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for [HandbookFacade.add] with request with null content.
     */
    @Test
    fun addNullContent() {
        val request = HandbookUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 3).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.add] with request with empty content.
     */
    @Test
    fun addEmptyContent() {
        val request = HandbookUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 3).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.add] with existing handbook.
     */
    @Test
    fun addExistingHandbook() {
        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = HandbookUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_EXISTS")
            .hasMessageContaining("Handbook already exists.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.add] with not existing game.
     */
    @Test
    fun addNotExistingGame() {
        assertThatThrownBy { facade.add(game = TestConstants.UUID, request = HandbookUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.update].
     */
    @Test
    fun update() {
        val game = GameUtils.getDomainGame(index = 1)
        val expectedHandbook = HandbookUtils.getHandbook(index = 1)
            .updated()
        val expectedDomainHandbook = game.handbook!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(game = game.uuid, uuid = game.handbook!!.uuid, request = HandbookUtils.newRequest())
        entityManager.flush()

        HandbookUtils.assertHandbookDeepEquals(expected = expectedHandbook, actual = result)
        HandbookUtils.assertHandbookDeepEquals(expected = expectedDomainHandbook, actual = HandbookUtils.getDomainHandbook(entityManager = entityManager, id = 1))

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.update] with request with null content.
     */
    @Test
    fun updateNullContent() {
        val game = GameUtils.getDomainGame(index = 1)
        val request = HandbookUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.update(game = game.uuid, uuid = game.handbook!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.update] with request with empty content.
     */
    @Test
    fun updateEmptyContent() {
        val game = GameUtils.getDomainGame(index = 1)
        val request = HandbookUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { facade.update(game = game.uuid, uuid = game.handbook!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.update] with not existing game.
     */
    @Test
    fun updateNotExistingGame() {
        val uuid = GameUtils.getDomainGame(index = 1).handbook!!.uuid

        assertThatThrownBy { facade.update(game = TestConstants.UUID, uuid = uuid, request = HandbookUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 1).uuid, uuid = TestConstants.UUID, request = HandbookUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_NOT_EXIST")
            .hasMessageContaining("Handbook doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getDomainGame(index = 1)

        facade.remove(game = game.uuid, uuid = game.handbook!!.uuid)
        entityManager.flush()

        assertThat(HandbookUtils.getDomainHandbook(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT - 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for [HandbookFacade.remove] with not existing game.
     */
    @Test
    fun removeNotExistingGame() {
        assertThatThrownBy { facade.remove(game = TestConstants.UUID, uuid = GameUtils.getDomainGame(index = 1).handbook!!.uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [HandbookFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(game = GameUtils.getDomainGame(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_NOT_EXIST")
            .hasMessageContaining("Handbook doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Asserts that counts of handbooks and games are unchanged.
     */
    private fun assertCounts() {
        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

}
