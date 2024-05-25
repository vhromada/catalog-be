package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.JokeUtils
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
 * A class represents test for class [JokeFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class JokeFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [JokeFacade]
     */
    @Autowired
    private lateinit var facade: JokeFacade

    /**
     * Test method for [JokeFacade.search].
     */
    @Test
    fun search() {
        val filter = PagingFilter()
        filter.page = 1
        filter.limit = JokeUtils.JOKES_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        JokeUtils.assertJokeListDeepEquals(expected = JokeUtils.getJokes(), actual = result.data)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = PagingFilter()
        filter.page = 2
        filter.limit = 1

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(JokeUtils.JOKES_COUNT)
        }
        JokeUtils.assertJokeListDeepEquals(expected = listOf(JokeUtils.getJoke(index = 2)), actual = result.data)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = PagingFilter()
        filter.page = 2
        filter.limit = JokeUtils.JOKES_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..JokeUtils.JOKES_COUNT) {
            val joke = JokeUtils.getJoke(index = i)

            val result = facade.get(uuid = joke.uuid)

            JokeUtils.assertJokeDeepEquals(expected = JokeUtils.getJoke(index = i), actual = result)
        }

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_NOT_EXIST")
            .hasMessageContaining("Joke doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedJoke = JokeUtils.newJoke()
        val expectedDomainJoke = JokeUtils.newDomainJoke(id = JokeUtils.JOKES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = JokeUtils.newRequest())
        entityManager.flush()

        JokeUtils.assertJokeDeepEquals(expected = expectedJoke, actual = result, ignoreUuid = true)
        JokeUtils.assertJokeDeepEquals(expected = expectedDomainJoke, actual = JokeUtils.getDomainJoke(entityManager = entityManager, id = JokeUtils.JOKES_COUNT + 1), ignoreUuid = true)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT + 1)
    }

    /**
     * Test method for [JokeFacade.add] with request with null content.
     */
    @Test
    fun addNullContent() {
        val request = JokeUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.add] with request with empty string as content.
     */
    @Test
    fun addEmptyContent() {
        val request = JokeUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = JokeUtils.newRequest()
        val expectedJoke = JokeUtils.getJoke(index = 1)
            .updated()
        val expectedDomainJoke = JokeUtils.getDomainJoke(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(uuid = JokeUtils.getDomainJoke(index = 1).uuid, request = request)
        entityManager.flush()

        JokeUtils.assertJokeDeepEquals(expected = expectedJoke, actual = result)
        JokeUtils.assertJokeDeepEquals(expected = expectedDomainJoke, actual = JokeUtils.getDomainJoke(entityManager = entityManager, id = 1))

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.update] with request with null content.
     */
    @Test
    fun updateNullContent() {
        val request = JokeUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.update(uuid = JokeUtils.getDomainJoke(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.update] with request with empty string as content.
     */
    @Test
    fun updateEmptyContent() {
        val request = JokeUtils.newRequest()
            .copy(content = "")

        assertThatThrownBy { facade.update(uuid = JokeUtils.getDomainJoke(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [JokeFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = JokeUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_NOT_EXIST")
            .hasMessageContaining("Joke doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.remove].
     */
    @Test
    @DirtiesContext
    fun remove() {
        facade.remove(uuid = JokeUtils.getJoke(index = 1).uuid)
        entityManager.flush()

        assertThat(JokeUtils.getDomainJoke(entityManager = entityManager, id = 1)).isNull()

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT - 1)
    }

    /**
     * Test method for [JokeFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_NOT_EXIST")
            .hasMessageContaining("Joke doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

    /**
     * Test method for [JokeFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        assertThat(facade.getStatistics().count).isEqualTo(JokeUtils.JOKES_COUNT)

        assertThat(JokeUtils.getJokesCount(entityManager = entityManager)).isEqualTo(JokeUtils.JOKES_COUNT)
    }

}
