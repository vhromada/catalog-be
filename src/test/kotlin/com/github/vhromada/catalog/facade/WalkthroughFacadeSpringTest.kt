package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.WalkthroughUtils
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
 * A class represents test for class [WalkthroughFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class WalkthroughFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [WalkthroughFacade]
     */
    @Autowired
    private lateinit var facade: WalkthroughFacade

    /**
     * Test method for [WalkthroughFacade.find].
     */
    @Test
    fun find() {
        for (i in listOf(1, 3)) {
            val game = GameUtils.getDomainGame(index = i)

            val result = facade.find(game = game.uuid)

            WalkthroughUtils.assertWalkthroughDeepEquals(expected = game.walkthrough!!, actual = result)
        }

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.find] with not existing game.
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
     * Test method for [WalkthroughFacade.find] with not existing walkthrough.
     */
    @Test
    fun findNotExisting() {
        assertThatThrownBy { facade.find(game = GameUtils.getDomainGame(index = 2).uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_NOT_EXIST")
            .hasMessageContaining("Walkthrough doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.get].
     */
    @Test
    fun get() {
        for (i in listOf(1, 3)) {
            val game = GameUtils.getDomainGame(index = i)

            val result = facade.get(game = game.uuid, uuid = game.walkthrough!!.uuid)

            WalkthroughUtils.assertWalkthroughDeepEquals(expected = game.walkthrough!!, actual = result)
        }

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.get] with not existing game.
     */
    @Test
    fun getNotExistingGame() {
        assertThatThrownBy { facade.get(game = TestConstants.UUID, uuid = GameUtils.getDomainGame(index = 1).walkthrough!!.uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(game = GameUtils.getDomainGame(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_NOT_EXIST")
            .hasMessageContaining("Walkthrough doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.add].
     */
    @Test
    fun add() {
        val game = GameUtils.getDomainGame(index = 2)
            .fillAudit(audit = AuditUtils.updatedAudit())
        val expectedWalkthrough = WalkthroughUtils.newWalkthrough()
        val expectedDomainWalkthrough = WalkthroughUtils.newDomainWalkthrough(id = WalkthroughUtils.WALKTHROUGHS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedDomainWalkthrough.game = game

        val result = facade.add(game = game.uuid, request = WalkthroughUtils.newRequest())
        entityManager.flush()

        WalkthroughUtils.assertWalkthroughDeepEquals(expected = expectedWalkthrough, actual = result, ignoreUuid = true)
        WalkthroughUtils.assertWalkthroughDeepEquals(
            expected = expectedDomainWalkthrough,
            actual = WalkthroughUtils.getDomainWalkthrough(entityManager = entityManager, id = WalkthroughUtils.WALKTHROUGHS_COUNT + 1),
            ignoreUuid = true
        )

        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT + 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for [WalkthroughFacade.add] with request with null content.
     */
    @Test
    fun addNullContent() {
        val request = WalkthroughUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 2).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.add] with request with empty content.
     */
    @Test
    fun addEmptyContent() {
        val request = WalkthroughUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 2).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.add] with existing walkthrough.
     */
    @Test
    fun addExistingWalkthrough() {
        assertThatThrownBy { facade.add(game = GameUtils.getDomainGame(index = 1).uuid, request = WalkthroughUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_EXISTS")
            .hasMessageContaining("Walkthrough already exists.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.add] with not existing game.
     */
    @Test
    fun addNotExistingGame() {
        assertThatThrownBy { facade.add(game = TestConstants.UUID, request = WalkthroughUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.update].
     */
    @Test
    fun update() {
        val game = GameUtils.getDomainGame(index = 1)
        val expectedWalkthrough = WalkthroughUtils.getWalkthrough(index = 1)
            .updated()
        val expectedDomainWalkthrough = game.walkthrough!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(game = game.uuid, uuid = game.walkthrough!!.uuid, request = WalkthroughUtils.newRequest())
        entityManager.flush()

        WalkthroughUtils.assertWalkthroughDeepEquals(expected = expectedWalkthrough, actual = result)
        WalkthroughUtils.assertWalkthroughDeepEquals(expected = expectedDomainWalkthrough, actual = WalkthroughUtils.getDomainWalkthrough(entityManager = entityManager, id = 1))

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.update] with request with null content.
     */
    @Test
    fun updateNullContent() {
        val game = GameUtils.getDomainGame(index = 1)
        val request = WalkthroughUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.update(game = game.uuid, uuid = game.walkthrough!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.update] with request with empty content.
     */
    @Test
    fun updateEmptyContent() {
        val game = GameUtils.getDomainGame(index = 1)
        val request = WalkthroughUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { facade.update(game = game.uuid, uuid = game.walkthrough!!.uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_CONTENT)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.update] with not existing game.
     */
    @Test
    fun updateNotExistingGame() {
        val uuid = GameUtils.getDomainGame(index = 1).walkthrough!!.uuid

        assertThatThrownBy { facade.update(game = TestConstants.UUID, uuid = uuid, request = WalkthroughUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(game = GameUtils.getDomainGame(index = 1).uuid, uuid = TestConstants.UUID, request = WalkthroughUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_NOT_EXIST")
            .hasMessageContaining("Walkthrough doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getDomainGame(index = 1)

        facade.remove(game = game.uuid, uuid = game.walkthrough!!.uuid)
        entityManager.flush()

        assertThat(WalkthroughUtils.getDomainWalkthrough(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT - 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for [WalkthroughFacade.remove] with not existing game.
     */
    @Test
    fun removeNotExistingGame() {
        assertThatThrownBy { facade.remove(game = TestConstants.UUID, uuid = GameUtils.getDomainGame(index = 1).walkthrough!!.uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Test method for [WalkthroughFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(game = GameUtils.getDomainGame(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_NOT_EXIST")
            .hasMessageContaining("Walkthrough doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertCounts()
    }

    /**
     * Asserts that counts of walkthroughs and games are unchanged.
     */
    private fun assertCounts() {
        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

}
