package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.AuthorFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.AuthorUtils
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
 * A class represents test for class [AuthorFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class AuthorFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [AuthorFacade]
     */
    @Autowired
    private lateinit var facade: AuthorFacade

    /**
     * Test method for [AuthorFacade.search].
     */
    @Test
    fun search() {
        val filter = AuthorFilter()
        filter.page = 1
        filter.limit = AuthorUtils.AUTHORS_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        AuthorUtils.assertAuthorListDeepEquals(expected = AuthorUtils.getAuthors(), actual = result.data)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = AuthorFilter()
        filter.page = 2
        filter.limit = 1

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(AuthorUtils.AUTHORS_COUNT)
        }
        AuthorUtils.assertAuthorListDeepEquals(expected = listOf(AuthorUtils.getAuthor(index = 2)), actual = result.data)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = AuthorFilter()
        filter.page = 2
        filter.limit = AuthorUtils.AUTHORS_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..AuthorUtils.AUTHORS_COUNT) {
            val author = AuthorUtils.getAuthor(index = i)
            val filter = AuthorFilter(firstName = author.firstName, middleName = author.middleName, lastName = author.lastName)
            filter.page = 1
            filter.limit = AuthorUtils.AUTHORS_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            AuthorUtils.assertAuthorListDeepEquals(expected = listOf(author), actual = result.data)
        }

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..AuthorUtils.AUTHORS_COUNT) {
            val author = AuthorUtils.getAuthor(index = i)

            val result = facade.get(uuid = author.uuid)

            AuthorUtils.assertAuthorDeepEquals(expected = AuthorUtils.getAuthor(index = i), actual = result)
        }

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_NOT_EXIST")
            .hasMessageContaining("Author doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedAuthor = AuthorUtils.newAuthor()
        val expectedDomainAuthor = AuthorUtils.newDomainAuthor(id = AuthorUtils.AUTHORS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = AuthorUtils.newRequest())
        entityManager.flush()

        AuthorUtils.assertAuthorDeepEquals(expected = expectedAuthor, actual = result, ignoreUuid = true)
        AuthorUtils.assertAuthorDeepEquals(expected = expectedDomainAuthor, actual = AuthorUtils.getDomainAuthor(entityManager = entityManager, id = AuthorUtils.AUTHORS_COUNT + 1), ignoreUuid = true)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT + 1)
    }

    /**
     * Test method for [AuthorFacade.add] with request with null first name.
     */
    @Test
    fun addNullFirstName() {
        val request = AuthorUtils.newRequest()
            .copy(firstName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_FIRST_NAME_NULL")
            .hasMessageContaining("First name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.add] with request with empty string as first name.
     */
    @Test
    fun addEmptyFirstName() {
        val request = AuthorUtils.newRequest()
            .copy(firstName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_FIRST_NAME_EMPTY")
            .hasMessageContaining("First name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.add] with request with null last name.
     */
    @Test
    fun addNullLastName() {
        val request = AuthorUtils.newRequest()
            .copy(lastName = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_LAST_NAME_NULL")
            .hasMessageContaining("Last name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.add] with request with empty string as last name.
     */
    @Test
    fun addEmptyLastName() {
        val request = AuthorUtils.newRequest()
            .copy(lastName = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_LAST_NAME_EMPTY")
            .hasMessageContaining("Last name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = AuthorUtils.newRequest()
        val expectedAuthor = AuthorUtils.getAuthor(index = 1)
            .updated()
        val expectedDomainAuthor = AuthorUtils.getDomainAuthor(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(uuid = AuthorUtils.getDomainAuthor(index = 1).uuid, request = request)
        entityManager.flush()

        AuthorUtils.assertAuthorDeepEquals(expected = expectedAuthor, actual = result)
        AuthorUtils.assertAuthorDeepEquals(expected = expectedDomainAuthor, actual = AuthorUtils.getDomainAuthor(entityManager = entityManager, id = 1))

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.update] with request with null first name.
     */
    @Test
    fun updateNullFirstName() {
        val request = AuthorUtils.newRequest()
            .copy(firstName = null)

        assertThatThrownBy { facade.update(uuid = AuthorUtils.getDomainAuthor(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_FIRST_NAME_NULL")
            .hasMessageContaining("First name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.update] with request with empty string as first name.
     */
    @Test
    fun updateEmptyFirstName() {
        val request = AuthorUtils.newRequest()
            .copy(firstName = "")

        assertThatThrownBy { facade.update(uuid = AuthorUtils.getDomainAuthor(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_FIRST_NAME_EMPTY")
            .hasMessageContaining("First name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AuthorFacade.update] with request with null last name.
     */
    @Test
    fun updateNullLastName() {
        val request = AuthorUtils.newRequest()
            .copy(lastName = null)

        assertThatThrownBy { facade.update(uuid = AuthorUtils.getDomainAuthor(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_LAST_NAME_NULL")
            .hasMessageContaining("Last name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.update] with request with empty string as last name.
     */
    @Test
    fun updateEmptyLastName() {
        val request = AuthorUtils.newRequest()
            .copy(lastName = "")

        assertThatThrownBy { facade.update(uuid = AuthorUtils.getDomainAuthor(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_LAST_NAME_EMPTY")
            .hasMessageContaining("Last name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [AuthorFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = AuthorUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_NOT_EXIST")
            .hasMessageContaining("Author doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.remove].
     */
    @Test
    @DirtiesContext
    fun remove() {
        clearReferencedData()

        facade.remove(uuid = AuthorUtils.getAuthor(index = 1).uuid)
        entityManager.flush()

        assertThat(AuthorUtils.getDomainAuthor(entityManager = entityManager, id = 1)).isNull()

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT - 1)
    }

    /**
     * Test method for [AuthorFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_NOT_EXIST")
            .hasMessageContaining("Author doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedAuthor = AuthorUtils.getAuthor(index = 1)
        val expectedDomainAuthor = AuthorUtils.getDomainAuthor(index = 1)
            .copy(id = AuthorUtils.AUTHORS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.duplicate(uuid = AuthorUtils.getAuthor(index = 1).uuid)
        entityManager.flush()

        AuthorUtils.assertAuthorDeepEquals(expected = expectedAuthor, actual = result, ignoreUuid = true)
        AuthorUtils.assertAuthorDeepEquals(expected = expectedDomainAuthor, actual = AuthorUtils.getDomainAuthor(entityManager = entityManager, id = AuthorUtils.AUTHORS_COUNT + 1), ignoreUuid = true)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT + 1)
    }

    /**
     * Test method for [AuthorFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("AUTHOR_NOT_EXIST")
            .hasMessageContaining("Author doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for [AuthorFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        assertThat(facade.getStatistics().count).isEqualTo(AuthorUtils.AUTHORS_COUNT)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Clears referenced data.
     */
    @Suppress("SqlNoDataSourceInspection", "SqlWithoutWhere", "SqlResolve")
    private fun clearReferencedData() {
        entityManager.createNativeQuery("DELETE FROM book_authors").executeUpdate()
        entityManager.flush()
    }

}
