package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.PictureUtils
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [PictureRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class PictureRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [PictureRepository]
     */
    @Autowired
    private lateinit var repository: PictureRepository

    /**
     * Test method for get pictures.
     */
    @Test
    fun getPictures() {
        val pictures = repository.findAll()

        PictureUtils.assertDomainPicturesDeepEquals(expected = PictureUtils.getDomainPictures(), actual = pictures)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for get picture.
     */
    @Test
    fun getPicture() {
        for (i in 1..PictureUtils.PICTURES_COUNT) {
            val picture = repository.findById(i).orElse(null)

            PictureUtils.assertPictureDeepEquals(expected = PictureUtils.getDomainPicture(index = i), actual = picture)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for add picture.
     */
    @Test
    @DirtiesContext
    fun add() {
        val picture = PictureUtils.newDomainPicture(id = null)
        val expectedPicture = PictureUtils.newDomainPicture(id = PictureUtils.PICTURES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(picture)

        assertSoftly {
            it.assertThat(picture.id).isEqualTo(PictureUtils.PICTURES_COUNT + 1)
            it.assertThat(picture.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(picture.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(picture.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(picture.updatedTime).isEqualTo(TestConstants.TIME)
        }
        PictureUtils.assertPictureDeepEquals(expected = expectedPicture, actual = PictureUtils.getDomainPicture(entityManager = entityManager, id = PictureUtils.PICTURES_COUNT + 1))

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT + 1)
    }

    /**
     * Test method for update picture.
     */
    @Test
    fun update() {
        val picture = PictureUtils.getDomainPicture(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedPicture = PictureUtils.getDomainPicture(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(picture)

        PictureUtils.assertPictureDeepEquals(expected = expectedPicture, actual = PictureUtils.getDomainPicture(entityManager = entityManager, id = 1))

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for remove picture.
     */
    @Test
    @DirtiesContext
    fun remove() {
        clearReferencedData()

        repository.delete(PictureUtils.getDomainPicture(entityManager = entityManager, id = 1)!!)

        assertThat(PictureUtils.getDomainPicture(entityManager = entityManager, id = 1)).isNull()

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT - 1)
    }

    /**
     * Test method for remove all pictures.
     */
    @Test
    @DirtiesContext
    fun removeAll() {
        clearReferencedData()

        repository.deleteAll()

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(0)
    }

    /**
     * Test method for search pictures.
     */
    @Test
    fun search() {
        val pictures = repository.findAll(Pageable.ofSize(PictureUtils.PICTURES_COUNT))

        assertSoftly {
            it.assertThat(pictures.number).isEqualTo(0)
            it.assertThat(pictures.totalPages).isEqualTo(1)
            it.assertThat(pictures.totalElements).isEqualTo(PictureUtils.PICTURES_COUNT.toLong())
        }
        PictureUtils.assertDomainPicturesDeepEquals(expected = PictureUtils.getDomainPictures(), actual = pictures.content)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for search pictures with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val pictures = repository.findAll(PageRequest.of(2, PictureUtils.PICTURES_COUNT))

        assertSoftly {
            it.assertThat(pictures.content).isEmpty()
            it.assertThat(pictures.number).isEqualTo(2)
            it.assertThat(pictures.totalPages).isEqualTo(1)
            it.assertThat(pictures.totalElements).isEqualTo(PictureUtils.PICTURES_COUNT.toLong())
        }

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for find picture by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..PictureUtils.PICTURES_COUNT) {
            val picture = PictureUtils.getDomainPicture(index = i)

            val result = repository.findByUuid(uuid = picture.uuid).orElse(null)

            PictureUtils.assertPictureDeepEquals(expected = PictureUtils.getDomainPicture(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Clears referenced data.
     */
    @Suppress("SqlNoDataSourceInspection", "SqlWithoutWhere", "SqlResolve")
    private fun clearReferencedData() {
        entityManager.createNativeQuery("UPDATE movies SET picture = NULL").executeUpdate()
        entityManager.createNativeQuery("UPDATE tv_shows SET picture = NULL").executeUpdate()
        entityManager.flush()
    }

}
