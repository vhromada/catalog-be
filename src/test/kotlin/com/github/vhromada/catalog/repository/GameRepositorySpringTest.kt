package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.GameFilter
import com.github.vhromada.catalog.utils.AccountUtils
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
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [GameRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class GameRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [GameRepository]
     */
    @Autowired
    private lateinit var repository: GameRepository

    /**
     * Test method for get games.
     */
    @Test
    fun getGames() {
        val games = repository.findAll()

        GameUtils.assertDomainGamesDeepEquals(expected = GameUtils.getDomainGames(), actual = games)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for get game.
     */
    @Test
    fun getGame() {
        for (i in 1..GameUtils.GAMES_COUNT) {
            val game = repository.findById(i).orElse(null)

            GameUtils.assertGameDeepEquals(expected = GameUtils.getDomainGame(index = i), actual = game)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for add game.
     */
    @Test
    @DirtiesContext
    fun add() {
        val game = GameUtils.newDomainGame(id = null)
        val expectedGame = GameUtils.newDomainGame(id = GameUtils.GAMES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(game)

        assertSoftly {
            it.assertThat(game.id).isEqualTo(GameUtils.GAMES_COUNT + 1)
            it.assertThat(game.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(game.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(game.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(game.updatedTime).isEqualTo(TestConstants.TIME)
        }
        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = GameUtils.getDomainGame(entityManager = entityManager, id = GameUtils.GAMES_COUNT + 1))

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT + 1)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for update game.
     */
    @Test
    fun update() {
        val game = GameUtils.getDomainGame(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedGame = GameUtils.getDomainGame(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(game)

        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = GameUtils.getDomainGame(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for update game with added cheat.
     */
    @Test
    @DirtiesContext
    fun updateAddCheat() {
        val game = GameUtils.getDomainGame(entityManager = entityManager, id = 1)!!
            .updated()
        game.cheat = CheatUtils.newDomainCheat(id = null)
        val expectedCheat = CheatUtils.newDomainCheat(id = CheatUtils.CHEATS_COUNT + 1)
            .copy(data = mutableListOf(CheatDataUtils.newDomainCheatData(id = CheatDataUtils.CHEAT_DATA_COUNT + 1).fillAudit(audit = AuditUtils.newAudit())))
            .fillAudit(audit = AuditUtils.newAudit())
        val expectedGame = GameUtils.getDomainGame(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedGame.cheat = expectedCheat

        repository.saveAndFlush(game)

        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = GameUtils.getDomainGame(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT + 1)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT + 1)
        }
    }

    /**
     * Test method for update game with remove cheat.
     */
    @Test
    @DirtiesContext
    fun updateRemoveCheat() {
        val game = GameUtils.getDomainGame(entityManager = entityManager, id = 2)!!
            .updated()
        game.cheat = null
        val expectedGame = GameUtils.getDomainGame(index = 2)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedGame.cheat = null

        repository.saveAndFlush(game)

        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = GameUtils.getDomainGame(entityManager = entityManager, id = 2))

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT - 1)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT - CheatDataUtils.CHEAT_DATA_CHEAT_COUNT)
        }
    }

    /**
     * Test method for remove game.
     */
    @Test
    fun remove() {
        repository.delete(GameUtils.getDomainGame(entityManager = entityManager, id = 1)!!)

        assertThat(GameUtils.getDomainGame(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT - 1)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for remove all games.
     */
    @Test
    fun removeAll() {
        repository.deleteAll()

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(0)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(0)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(0)
        }
    }

    /**
     * Test method for search games by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..GameUtils.GAMES_COUNT) {
            val game = GameUtils.getDomainGame(index = i)
            val filter = GameFilter(name = game.name)

            val result = repository.findAll(filter.toSpecification())

            GameUtils.assertDomainGamesDeepEquals(expected = listOf(game), actual = result.toList())
        }

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for find game by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..GameUtils.GAMES_COUNT) {
            val game = GameUtils.getDomainGame(index = i)

            val result = repository.findByUuid(uuid = game.uuid).orElse(null)

            GameUtils.assertGameDeepEquals(expected = GameUtils.getDomainGame(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for get statistics.
     */
    @Test
    fun getStatistics() {
        val result = repository.getStatistics()

        GameUtils.assertStatisticsDeepEquals(expected = GameUtils.getDomainStatistics(), actual = result)

        assertSoftly {
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

}
