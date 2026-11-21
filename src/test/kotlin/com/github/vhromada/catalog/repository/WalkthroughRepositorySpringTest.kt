package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.WalkthroughUtils
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
 * A class represents test for class [WalkthroughRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class WalkthroughRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [WalkthroughRepository]
     */
    @Autowired
    private lateinit var repository: WalkthroughRepository

    /**
     * Test method for get walkthrough.
     */
    @Test
    fun getWalkthrough() {
        for (i in 1..WalkthroughUtils.WALKTHROUGHS_COUNT) {
            val walkthrough = repository.findById(i).orElse(null)

            WalkthroughUtils.assertWalkthroughDeepEquals(expected = WalkthroughUtils.getDomainWalkthrough(index = i), actual = walkthrough)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for add walkthrough.
     */
    @Test
    fun add() {
        val walkthrough = WalkthroughUtils.newDomainWalkthrough(id = null)
        walkthrough.game = GameUtils.getDomainGame(entityManager = entityManager, id = 2)
        val expectedWalkthrough = WalkthroughUtils.newDomainWalkthrough(id = WalkthroughUtils.WALKTHROUGHS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedWalkthrough.game = GameUtils.getDomainGame(entityManager = entityManager, id = 2)

        repository.saveAndFlush(walkthrough)

        assertSoftly {
            it.assertThat(walkthrough.id).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT + 1)
            it.assertThat(walkthrough.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(walkthrough.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(walkthrough.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(walkthrough.updatedTime).isEqualTo(TestConstants.TIME)
        }
        WalkthroughUtils.assertWalkthroughDeepEquals(
            expected = expectedWalkthrough,
            actual = WalkthroughUtils.getDomainWalkthrough(entityManager = entityManager, id = WalkthroughUtils.WALKTHROUGHS_COUNT + 1)
        )

        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT + 1)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for update walkthrough.
     */
    @Test
    fun update() {
        val walkthrough = WalkthroughUtils.getDomainWalkthrough(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedWalkthrough = GameUtils.getDomainGame(index = 1).walkthrough!!
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(walkthrough)

        WalkthroughUtils.assertWalkthroughDeepEquals(expected = expectedWalkthrough, actual = WalkthroughUtils.getDomainWalkthrough(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for find walkthrough by game ID.
     */
    @Test
    fun findByGameId() {
        for (i in listOf(1, 3)) {
            val walkthrough = repository.findByGameId(id = i).orElse(null)

            WalkthroughUtils.assertWalkthroughDeepEquals(expected = GameUtils.getDomainGame(index = i).walkthrough, actual = walkthrough)
        }

        assertThat(repository.findByGameId(id = 2)).isNotPresent
        assertThat(repository.findByGameId(id = Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

    /**
     * Test method for find walkthrough by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in listOf(1, 3)) {
            val walkthrough = GameUtils.getDomainGame(index = i).walkthrough!!

            val result = repository.findByUuid(uuid = walkthrough.uuid).orElse(null)

            WalkthroughUtils.assertWalkthroughDeepEquals(expected = walkthrough, actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(WalkthroughUtils.getWalkthroughsCount(entityManager = entityManager)).isEqualTo(WalkthroughUtils.WALKTHROUGHS_COUNT)
            it.assertThat(GameUtils.getGamesCount(entityManager = entityManager)).isEqualTo(GameUtils.GAMES_COUNT)
        }
    }

}
