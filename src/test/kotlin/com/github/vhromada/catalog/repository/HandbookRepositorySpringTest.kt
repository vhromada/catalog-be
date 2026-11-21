package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.HandbookUtils
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [HandbookRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class HandbookRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [HandbookRepository]
     */
    @Autowired
    private lateinit var repository: HandbookRepository

    /**
     * Test method for get handbook.
     */
    @Test
    fun getHandbook() {
        for (i in 1..HandbookUtils.HANDBOOKS_COUNT) {
            val handbook = repository.findById(i).orElse(null)

            HandbookUtils.assertHandbookDeepEquals(expected = HandbookUtils.getDomainHandbook(index = i), actual = handbook)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for add handbook.
     */
    @Test
    fun add() {
        val handbook = HandbookUtils.newDomainHandbook(id = null)
        handbook.game = GameUtils.getDomainGame(entityManager = entityManager, id = 3)
        val expectedHandbook = HandbookUtils.newDomainHandbook(id = HandbookUtils.HANDBOOKS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedHandbook.game = GameUtils.getDomainGame(entityManager = entityManager, id = 3)

        repository.saveAndFlush(handbook)

        assertSoftly {
            it.assertThat(handbook.id).isEqualTo(HandbookUtils.HANDBOOKS_COUNT + 1)
            it.assertThat(handbook.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(handbook.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(handbook.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(handbook.updatedTime).isEqualTo(TestConstants.TIME)
        }
        HandbookUtils.assertHandbookDeepEquals(
            expected = expectedHandbook,
            actual = HandbookUtils.getDomainHandbook(entityManager = entityManager, id = HandbookUtils.HANDBOOKS_COUNT + 1)
        )

        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT + 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for update handbook.
     */
    @Test
    fun update() {
        val handbook = HandbookUtils.getDomainHandbook(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedHandbook = GameUtils.getDomainGame(index = 1).handbook!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(handbook)

        HandbookUtils.assertHandbookDeepEquals(expected = expectedHandbook, actual = HandbookUtils.getDomainHandbook(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for find handbook by game ID.
     */
    @Test
    fun findByGameId() {
        for (i in listOf(1, 2)) {
            val handbook = repository.findByGameId(id = i).orElse(null)

            HandbookUtils.assertHandbookDeepEquals(expected = GameUtils.getDomainGame(index = i).handbook, actual = handbook)
        }

        assertThat(repository.findByGameId(id = 3)).isNotPresent
        assertThat(repository.findByGameId(id = Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for find handbook by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in listOf(1, 2)) {
            val handbook = GameUtils.getDomainGame(index = i).handbook!!

            val result = repository.findByUuid(uuid = handbook.uuid).orElse(null)

            HandbookUtils.assertHandbookDeepEquals(expected = handbook, actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(HandbookUtils.getHandbooksCount(entityManager = entityManager)).isEqualTo(HandbookUtils.HANDBOOKS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

}
