package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GenreUtils
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
 * A class represents test for class [GenreRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class GenreRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [GenreRepository]
     */
    @Autowired
    private lateinit var repository: GenreRepository

    /**
     * Test method for get genres.
     */
    @Test
    fun getGenres() {
        val genres = repository.findAll()

        GenreUtils.assertDomainGenresDeepEquals(expected = GenreUtils.getDomainGenres(), actual = genres)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for get genre.
     */
    @Test
    fun getGenre() {
        for (i in 1..GenreUtils.GENRES_COUNT) {
            val genre = repository.findById(i).orElse(null)

            GenreUtils.assertGenreDeepEquals(expected = GenreUtils.getDomainGenre(index = i), actual = genre)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for add genre.
     */
    @Test
    @DirtiesContext
    fun add() {
        val genre = GenreUtils.newDomainGenre(id = null)
        val expectedGenre = GenreUtils.newDomainGenre(id = GenreUtils.GENRES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(genre)

        assertSoftly {
            it.assertThat(genre.id).isEqualTo(GenreUtils.GENRES_COUNT + 1)
            it.assertThat(genre.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(genre.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(genre.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(genre.updatedTime).isEqualTo(TestConstants.TIME)
        }
        GenreUtils.assertGenreDeepEquals(expected = expectedGenre, actual = GenreUtils.getDomainGenre(entityManager = entityManager, id = GenreUtils.GENRES_COUNT + 1))

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT + 1)
    }

    /**
     * Test method for update genre.
     */
    @Test
    fun update() {
        val genre = GenreUtils.getDomainGenre(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedGenre = GenreUtils.getDomainGenre(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(genre)

        GenreUtils.assertGenreDeepEquals(expected = expectedGenre, actual = GenreUtils.getDomainGenre(entityManager = entityManager, id = 1))

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for remove genre.
     */
    @Test
    @DirtiesContext
    fun remove() {
        clearReferencedData()

        repository.delete(GenreUtils.getDomainGenre(entityManager = entityManager, id = 1)!!)

        assertThat(GenreUtils.getDomainGenre(entityManager = entityManager, id = 1)).isNull()

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT - 1)
    }

    /**
     * Test method for remove all genres.
     */
    @Test
    @DirtiesContext
    fun removeAll() {
        clearReferencedData()

        repository.deleteAll()

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(0)
    }

    /**
     * Test method for search genres by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..GenreUtils.GENRES_COUNT) {
            val genre = GenreUtils.getDomainGenre(index = i)
            val filter = GenreFilter(name = genre.name)

            val result = repository.findAll(filter.toSpecification())

            GenreUtils.assertDomainGenresDeepEquals(expected = listOf(genre), actual = result.toList())
        }

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for find genre by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..GenreUtils.GENRES_COUNT) {
            val genre = GenreUtils.getDomainGenre(index = i)

            val result = repository.findByUuid(uuid = genre.uuid).orElse(null)

            GenreUtils.assertGenreDeepEquals(expected = GenreUtils.getDomainGenre(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Clears referenced data.
     */
    @Suppress("SqlNoDataSourceInspection", "SqlWithoutWhere", "SqlResolve")
    private fun clearReferencedData() {
        entityManager.createNativeQuery("DELETE FROM movie_genres").executeUpdate()
        entityManager.createNativeQuery("DELETE FROM tv_show_genres").executeUpdate()
        entityManager.flush()
    }

}
