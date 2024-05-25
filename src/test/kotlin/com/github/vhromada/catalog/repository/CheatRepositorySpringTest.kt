package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
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
 * A class represents test for class [CheatRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class CheatRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [CheatRepository]
     */
    @Autowired
    private lateinit var repository: CheatRepository

    /**
     * Test method for get cheat.
     */
    @Test
    fun getCheat() {
        for (i in 2..GameUtils.GAMES_COUNT) {
            val cheat = repository.findById(i - 1).orElse(null)

            CheatUtils.assertCheatDeepEquals(expected = GameUtils.getDomainGame(index = i).cheat, actual = cheat)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for add cheat.
     */
    @Test
    @DirtiesContext
    fun add() {
        val cheat = CheatUtils.newDomainCheat(id = null)
        cheat.game = GameUtils.getDomainGame(entityManager = entityManager, id = 1)
        val expectedCheat = CheatUtils.newDomainCheat(id = CheatUtils.CHEATS_COUNT + 1)
            .copy(data = mutableListOf(CheatDataUtils.newDomainCheatData(id = CheatDataUtils.CHEAT_DATA_COUNT + 1).fillAudit(audit = AuditUtils.newAudit())))
            .fillAudit(audit = AuditUtils.newAudit())
        expectedCheat.game = GameUtils.getDomainGame(entityManager = entityManager, id = 1)

        repository.saveAndFlush(cheat)

        assertSoftly {
            it.assertThat(cheat.id).isEqualTo(CheatUtils.CHEATS_COUNT + 1)
            it.assertThat(cheat.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(cheat.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(cheat.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(cheat.updatedTime).isEqualTo(TestConstants.TIME)
        }
        CheatUtils.assertCheatDeepEquals(expected = expectedCheat, actual = CheatUtils.getDomainCheat(entityManager = entityManager, id = CheatUtils.CHEATS_COUNT + 1))

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT + 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT + 1)
        }
    }

    /**
     * Test method for update cheat.
     */
    @Test
    fun update() {
        val cheat = CheatUtils.getDomainCheat(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedCheat = GameUtils.getDomainGame(index = 2).cheat!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(cheat)

        CheatUtils.assertCheatDeepEquals(expected = expectedCheat, actual = CheatUtils.getDomainCheat(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for update cheat with added cheat's data.
     */
    @Test
    @DirtiesContext
    fun updateAddedData() {
        val cheat = CheatUtils.getDomainCheat(entityManager = entityManager, id = 1)!!
            .updated()
        cheat.data.add(CheatDataUtils.newDomainCheatData(id = null))
        val expectedCheat = GameUtils.getDomainGame(index = 2).cheat!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedCheat.data.add(CheatDataUtils.newDomainCheatData(id = CheatDataUtils.CHEAT_DATA_COUNT + 1).fillAudit(audit = AuditUtils.newAudit()))

        repository.saveAndFlush(cheat)

        CheatUtils.assertCheatDeepEquals(expected = expectedCheat, actual = CheatUtils.getDomainCheat(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT + 1)
        }
    }

    /**
     * Test method for update cheat with removed cheat's data.
     */
    @Test
    @DirtiesContext
    fun updateRemovedData() {
        val cheat = CheatUtils.getDomainCheat(entityManager = entityManager, id = 1)!!
            .updated()
        cheat.data.clear()
        val expectedCheat = GameUtils.getDomainGame(index = 2).cheat!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedCheat.data.clear()

        repository.saveAndFlush(cheat)

        CheatUtils.assertCheatDeepEquals(expected = expectedCheat, actual = CheatUtils.getDomainCheat(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT - CheatUtils.getDomainCheat(index = 1).data.size)
        }
    }

    /**
     * Test method for find cheat by game ID.
     */
    @Test
    fun findByGameId() {
        for (i in 2..GameUtils.GAMES_COUNT) {
            val cheat = repository.findByGameId(id = i).orElse(null)

            CheatUtils.assertCheatDeepEquals(expected = GameUtils.getDomainGame(index = i).cheat, actual = cheat)
        }

        assertThat(repository.findByGameId(id = 1)).isNotPresent
        assertThat(repository.findByGameId(id = Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

    /**
     * Test method for find cheat by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 2..GameUtils.GAMES_COUNT) {
            val cheat = GameUtils.getDomainGame(index = i).cheat!!

            val result = repository.findByUuid(uuid = cheat.uuid).orElse(null)

            CheatUtils.assertCheatDeepEquals(expected = cheat, actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(CheatUtils.getCheatsCount(entityManager = entityManager)).isEqualTo(CheatUtils.CHEATS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
            it.assertThat(CheatDataUtils.getCheatDataCount(entityManager = entityManager)).isEqualTo(CheatDataUtils.CHEAT_DATA_COUNT)
        }
    }

}
