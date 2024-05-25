package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.AuthorFilter
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.AuthorUtils
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
 * A class represents test for class [AuthorRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class AuthorRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [AuthorRepository]
     */
    @Autowired
    private lateinit var repository: AuthorRepository

    /**
     * Test method for get authors.
     */
    @Test
    fun getAuthors() {
        val authors = repository.findAll()

        AuthorUtils.assertDomainAuthorsDeepEquals(expected = AuthorUtils.getDomainAuthors(), actual = authors)

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for get author.
     */
    @Test
    fun getAuthor() {
        for (i in 1..AuthorUtils.AUTHORS_COUNT) {
            val author = repository.findById(i).orElse(null)

            AuthorUtils.assertAuthorDeepEquals(expected = AuthorUtils.getDomainAuthor(index = i), actual = author)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for add author.
     */
    @Test
    @DirtiesContext
    fun add() {
        val author = AuthorUtils.newDomainAuthor(id = null)
        val expectedAuthor = AuthorUtils.newDomainAuthor(id = AuthorUtils.AUTHORS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(author)

        assertSoftly {
            it.assertThat(author.id).isEqualTo(AuthorUtils.AUTHORS_COUNT + 1)
            it.assertThat(author.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(author.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(author.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(author.updatedTime).isEqualTo(TestConstants.TIME)
        }
        AuthorUtils.assertAuthorDeepEquals(expected = expectedAuthor, actual = AuthorUtils.getDomainAuthor(entityManager = entityManager, id = AuthorUtils.AUTHORS_COUNT + 1))

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT + 1)
    }

    /**
     * Test method for update author.
     */
    @Test
    fun update() {
        val author = AuthorUtils.getDomainAuthor(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedAuthor = AuthorUtils.getDomainAuthor(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(author)

        AuthorUtils.assertAuthorDeepEquals(expected = expectedAuthor, actual = AuthorUtils.getDomainAuthor(entityManager = entityManager, id = 1))

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for remove author.
     */
    @Test
    @DirtiesContext
    fun remove() {
        clearReferencedData()

        repository.delete(AuthorUtils.getDomainAuthor(entityManager = entityManager, id = 1)!!)

        assertThat(AuthorUtils.getDomainAuthor(entityManager = entityManager, id = 1)).isNull()

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT - 1)
    }

    /**
     * Test method for remove all authors.
     */
    @Test
    @DirtiesContext
    fun removeAll() {
        clearReferencedData()

        repository.deleteAll()

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(0)
    }

    /**
     * Test method for search authors by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..AuthorUtils.AUTHORS_COUNT) {
            val author = AuthorUtils.getDomainAuthor(index = i)
            val filter = AuthorFilter(firstName = author.firstName, middleName = author.middleName, lastName = author.lastName)

            val result = repository.findAll(filter.toSpecification())

            AuthorUtils.assertDomainAuthorsDeepEquals(expected = listOf(author), actual = result.toList())
        }

        assertThat(AuthorUtils.getAuthorsCount(entityManager = entityManager)).isEqualTo(AuthorUtils.AUTHORS_COUNT)
    }

    /**
     * Test method for find author by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..AuthorUtils.AUTHORS_COUNT) {
            val author = AuthorUtils.getDomainAuthor(index = i)

            val result = repository.findByUuid(uuid = author.uuid).orElse(null)

            AuthorUtils.assertAuthorDeepEquals(expected = AuthorUtils.getDomainAuthor(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

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
