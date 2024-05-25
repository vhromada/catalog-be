package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.PictureUtils
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
 * A class represents test for class [PictureFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class PictureFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [PictureFacade]
     */
    @Autowired
    private lateinit var facade: PictureFacade

    /**
     * Test method for [PictureFacade.search].
     */
    @Test
    fun search() {
        val filter = NameFilter()
        filter.page = 1
        filter.limit = PictureUtils.PICTURES_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        PictureUtils.assertPictureListDeepEquals(expected = PictureUtils.getPictures(), actual = result.data)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = 1
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(PictureUtils.PICTURES_COUNT)
        }
        PictureUtils.assertPictureListDeepEquals(expected = listOf(PictureUtils.getPicture(index = 2)), actual = result.data)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = PictureUtils.PICTURES_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..PictureUtils.PICTURES_COUNT) {
            val picture = PictureUtils.getPicture(index = i)

            val result = facade.get(uuid = picture.uuid)

            PictureUtils.assertPictureDeepEquals(expected = PictureUtils.getPicture(index = i), actual = result)
        }

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedPicture = PictureUtils.newPicture()
        val expectedDomainPicture = PictureUtils.newDomainPicture(id = PictureUtils.PICTURES_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = PictureUtils.newRequest())
        entityManager.flush()

        PictureUtils.assertPictureDeepEquals(expected = expectedPicture, actual = result, ignoreUuid = true)
        PictureUtils.assertPictureDeepEquals(
            expected = expectedDomainPicture,
            actual = PictureUtils.getDomainPicture(entityManager = entityManager, id = PictureUtils.PICTURES_COUNT + 1),
            ignoreUuid = true
        )

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT + 1)
    }

    /**
     * Test method for [PictureFacade.add] with request with null content.
     */
    @Test
    fun addNullContent() {
        val request = PictureUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.add] with request with empty string as content.
     */
    @Test
    fun addEmptyContent() {
        val request = PictureUtils.newRequest()
            .copy(content = ByteArray(0))

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = PictureUtils.newRequest()
        val expectedPicture = PictureUtils.getPicture(index = 1)
            .updated()
        val expectedDomainPicture = PictureUtils.getDomainPicture(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(uuid = PictureUtils.getDomainPicture(index = 1).uuid, request = request)
        entityManager.flush()

        PictureUtils.assertPictureDeepEquals(expected = expectedPicture, actual = result)
        PictureUtils.assertPictureDeepEquals(expected = expectedDomainPicture, actual = PictureUtils.getDomainPicture(entityManager = entityManager, id = 1))

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.update] with request with null content.
     */
    @Test
    fun updateNullContent() {
        val request = PictureUtils.newRequest()
            .copy(content = null)

        assertThatThrownBy { facade.update(uuid = PictureUtils.getDomainPicture(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_CONTENT_NULL")
            .hasMessageContaining("Content mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.update] with request with empty string as content.
     */
    @Test
    fun updateEmptyContent() {
        val request = PictureUtils.newRequest()
            .copy(content = ByteArray(0))

        assertThatThrownBy { facade.update(uuid = PictureUtils.getDomainPicture(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_CONTENT_EMPTY")
            .hasMessageContaining("Content mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [PictureFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = PictureUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT)
    }

    /**
     * Test method for [PictureFacade.remove].
     */
    @Test
    @DirtiesContext
    fun remove() {
        clearReferencedData()

        facade.remove(uuid = PictureUtils.getPicture(index = 1).uuid)
        entityManager.flush()

        assertThat(PictureUtils.getDomainPicture(entityManager = entityManager, id = 1)).isNull()

        assertThat(PictureUtils.getPicturesCount(entityManager = entityManager)).isEqualTo(PictureUtils.PICTURES_COUNT - 1)
    }

    /**
     * Test method for [PictureFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

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
