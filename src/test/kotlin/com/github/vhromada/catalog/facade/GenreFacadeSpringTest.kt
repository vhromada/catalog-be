package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GenreUtils
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
 * A class represents test for class [GenreFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class GenreFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [GenreFacade]
     */
    @Autowired
    private lateinit var facade: GenreFacade

    /**
     * Test method for [GenreFacade.search].
     */
    @Test
    fun search() {
        val filter = NameFilter()
        filter.page = 1
        filter.limit = GenreUtils.GENRES_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        GenreUtils.assertGenreListDeepEquals(expected = GenreUtils.getGenres(), actual = result.data)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = 1

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(GenreUtils.GENRES_COUNT)
        }
        GenreUtils.assertGenreListDeepEquals(expected = listOf(GenreUtils.getGenre(index = 2)), actual = result.data)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = GenreUtils.GENRES_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..GenreUtils.GENRES_COUNT) {
            val genre = GenreUtils.getGenre(index = i)
            val filter = NameFilter(name = genre.name)
            filter.page = 1
            filter.limit = GenreUtils.GENRES_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            GenreUtils.assertGenreListDeepEquals(expected = listOf(genre), actual = result.data)
        }

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..GenreUtils.GENRES_COUNT) {
            val genre = GenreUtils.getGenre(index = i)

            val result = facade.get(uuid = genre.uuid)

            GenreUtils.assertGenreDeepEquals(expected = GenreUtils.getGenre(index = i), actual = result)
        }

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedGenre = GenreUtils.newGenre()
        val expectedDomainGenre = GenreUtils.newDomainGenre(id = GenreUtils.GENRES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = GenreUtils.newRequest())
        entityManager.flush()

        GenreUtils.assertGenreDeepEquals(expected = expectedGenre, actual = result, ignoreUuid = true)
        GenreUtils.assertGenreDeepEquals(expected = expectedDomainGenre, actual = GenreUtils.getDomainGenre(entityManager = entityManager, id = GenreUtils.GENRES_COUNT + 1), ignoreUuid = true)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT + 1)
    }

    /**
     * Test method for [GenreFacade.add] with request with null name.
     */
    @Test
    fun addNullName() {
        val request = GenreUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.add] with request with empty string as name.
     */
    @Test
    fun addEmptyName() {
        val request = GenreUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = GenreUtils.newRequest()
        val expectedGenre = GenreUtils.getGenre(index = 1)
            .updated()
        val expectedDomainGenre = GenreUtils.getDomainGenre(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(uuid = GenreUtils.getDomainGenre(index = 1).uuid, request = request)
        entityManager.flush()

        GenreUtils.assertGenreDeepEquals(expected = expectedGenre, actual = result)
        GenreUtils.assertGenreDeepEquals(expected = expectedDomainGenre, actual = GenreUtils.getDomainGenre(entityManager = entityManager, id = 1))

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.update] with request with null name.
     */
    @Test
    fun updateNullName() {
        val request = GenreUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.update(uuid = GenreUtils.getDomainGenre(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.update] with request with empty string as name.
     */
    @Test
    fun updateEmptyName() {
        val request = GenreUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.update(uuid = GenreUtils.getDomainGenre(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [GenreFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = GenreUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.remove].
     */
    @Test
    @DirtiesContext
    fun remove() {
        clearReferencedData()

        facade.remove(uuid = GenreUtils.getGenre(index = 1).uuid)
        entityManager.flush()

        assertThat(GenreUtils.getDomainGenre(entityManager = entityManager, id = 1)).isNull()

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT - 1)
    }

    /**
     * Test method for [GenreFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedGenre = GenreUtils.getGenre(index = 1)
        val expectedDomainGenre = GenreUtils.getDomainGenre(index = 1)
            .copy(id = GenreUtils.GENRES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.duplicate(uuid = GenreUtils.getGenre(index = 1).uuid)
        entityManager.flush()

        GenreUtils.assertGenreDeepEquals(expected = expectedGenre, actual = result, ignoreUuid = true)
        GenreUtils.assertGenreDeepEquals(expected = expectedDomainGenre, actual = GenreUtils.getDomainGenre(entityManager = entityManager, id = GenreUtils.GENRES_COUNT + 1), ignoreUuid = true)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT + 1)
    }

    /**
     * Test method for [GenreFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(GenreUtils.getGenresCount(entityManager = entityManager)).isEqualTo(GenreUtils.GENRES_COUNT)
    }

    /**
     * Test method for [GenreFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        assertThat(facade.getStatistics().count).isEqualTo(GenreUtils.GENRES_COUNT)

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
