package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.JokeUtils
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
 * A class represents test for class [JokeRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class JokeRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [JokeRepository]
     */
    @Autowired
    private lateinit var repository: JokeRepository

    /**
     * Test method for get jokes.
     */
    @Test
    fun getJokes() {
        val jokes = repository.findAll()

        JokeUtils.assertDomainJokesDeepEquals(expected = JokeUtils.getDomainJokes(), actual = jokes)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for get joke.
     */
    @Test
    fun getJoke() {
        for (i in 1..JokeUtils.JOKES_COUNT) {
            val joke = repository.findById(i).orElse(null)

            JokeUtils.assertJokeDeepEquals(expected = JokeUtils.getDomainJoke(index = i), actual = joke)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for add joke.
     */
    @Test
    @DirtiesContext
    fun add() {
        val joke = JokeUtils.newDomainJoke(id = null)
        val expectedJoke = JokeUtils.newDomainJoke(id = JokeUtils.JOKES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(joke)

        assertSoftly {
            it.assertThat(joke.id).isEqualTo(JokeUtils.JOKES_COUNT + 1)
            it.assertThat(joke.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(joke.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(joke.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(joke.updatedTime).isEqualTo(TestConstants.TIME)
        }
        JokeUtils.assertJokeDeepEquals(expected = expectedJoke, actual = JokeUtils.getDomainJoke(entityManager = entityManager, id = JokeUtils.JOKES_COUNT + 1))

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT + 1)
    }

    /**
     * Test method for update joke.
     */
    @Test
    fun update() {
        val joke = JokeUtils.getDomainJoke(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedJoke = JokeUtils.getDomainJoke(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(joke)

        JokeUtils.assertJokeDeepEquals(expected = expectedJoke, actual = JokeUtils.getDomainJoke(entityManager = entityManager, id = 1))

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for remove joke.
     */
    @Test
    fun remove() {
        repository.delete(JokeUtils.getDomainJoke(entityManager = entityManager, id = 1)!!)

        assertThat(JokeUtils.getDomainJoke(entityManager = entityManager, id = 1)).isNull()

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT - 1)
    }

    /**
     * Test method for remove all jokes.
     */
    @Test
    fun removeAll() {
        repository.deleteAll()

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(0)
    }

    /**
     * Test method for find joke by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..JokeUtils.JOKES_COUNT) {
            val joke = JokeUtils.getDomainJoke(index = i)

            val result = repository.findByUuid(uuid = joke.uuid).orElse(null)

            JokeUtils.assertJokeDeepEquals(expected = JokeUtils.getDomainJoke(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

}
