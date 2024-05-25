package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.Picture
import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates picture fields.
 *
 * @return updated picture
 */
fun com.github.vhromada.catalog.domain.Picture.updated(): com.github.vhromada.catalog.domain.Picture {
    content = PictureUtils.CONTENT.toByteArray()
    return this
}

/**
 * Updates picture fields.
 *
 * @return updated picture
 */
fun Picture.updated(): Picture {
    return copy(content = PictureUtils.CONTENT.toByteArray())
}

/**
 * A class represents utility class for pictures.
 *
 * @author Vladimir Hromada
 */
object PictureUtils {

    /**
     * Count of pictures
     */
    const val PICTURES_COUNT = 6

    /**
     * Picture content
     */
    const val CONTENT = "Picture"

    /**
     * Returns pictures.
     *
     * @return pictures
     */
    fun getDomainPictures(): List<com.github.vhromada.catalog.domain.Picture> {
        val pictures = mutableListOf<com.github.vhromada.catalog.domain.Picture>()
        for (i in 1..PICTURES_COUNT) {
            pictures.add(getDomainPicture(index = i))
        }

        return pictures
    }

    /**
     * Returns pictures.
     *
     * @return pictures
     */
    fun getPictures(): List<Picture> {
        val pictures = mutableListOf<Picture>()
        for (i in 1..PICTURES_COUNT) {
            pictures.add(getPicture(index = i))
        }

        return pictures
    }

    /**
     * Returns picture for index.
     *
     * @param index index
     * @return picture for index
     */
    fun getDomainPicture(index: Int): com.github.vhromada.catalog.domain.Picture {
        return com.github.vhromada.catalog.domain.Picture(
            id = index,
            uuid = getUuid(index = index),
            content = getContent(index = index)
        ).fillAudit(audit = AuditUtils.getAudit())
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "93d2c748-c60a-44ba-a5ca-98a090c4ab93"
            2 -> "3321434e-7dcd-4471-abf9-72141f02f433"
            3 -> "2ee97c4f-2de7-4783-bf8c-3a3186f6a570"
            4 -> "d0e5fca5-5b80-4f78-b56c-f81509812ca1"
            5 -> "64a96352-c0ee-42bd-9e20-0d4dff484c42"
            6 -> "49ec0625-3143-4fbc-8da1-81ebd543c143"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns content for index.
     *
     * @param index index
     * @return content for index
     */
    private fun getContent(index: Int): ByteArray {
        return index.toString().toByteArray()
    }

    /**
     * Returns picture.
     *
     * @param entityManager entity manager
     * @param id            picture ID
     * @return picture
     */
    fun getDomainPicture(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Picture? {
        return entityManager.find(com.github.vhromada.catalog.domain.Picture::class.java, id)
    }

    /**
     * Returns picture for index.
     *
     * @param index index
     * @return picture for index
     */
    fun getPicture(index: Int): Picture {
        return Picture(
            uuid = getUuid(index = index),
            content = getContent(index = index)
        )
    }

    /**
     * Returns count of pictures.
     *
     * @param entityManager entity manager
     * @return count of pictures
     */
    fun getPicturesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(p.id) FROM Picture p", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns picture.
     *
     * @param id ID
     * @return picture
     */
    fun newDomainPicture(id: Int?): com.github.vhromada.catalog.domain.Picture {
        return com.github.vhromada.catalog.domain.Picture(
            id = id,
            uuid = TestConstants.UUID,
            content = ByteArray(0)
        ).updated()
    }

    /**
     * Returns picture.
     *
     * @return picture
     */
    fun newPicture(): Picture {
        return Picture(
            uuid = TestConstants.UUID,
            content = ByteArray(0)
        ).updated()
    }

    /**
     * Returns request for changing picture.
     *
     * @return request for changing picture
     */
    fun newRequest(): ChangePictureRequest {
        return ChangePictureRequest(content = CONTENT.toByteArray())
    }

    /**
     * Asserts list of pictures deep equals.
     *
     * @param expected expected list of pictures
     * @param actual   actual list of pictures
     */
    fun assertDomainPicturesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Picture>, actual: List<com.github.vhromada.catalog.domain.Picture>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertPictureDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts picture deep equals.
     *
     * @param expected   expected picture
     * @param actual     actual picture
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertPictureDeepEquals(expected: com.github.vhromada.catalog.domain.Picture?, actual: com.github.vhromada.catalog.domain.Picture?, ignoreUuid: Boolean = false) {
        if (expected == null) {
            assertThat(actual).isNull()
        } else {
            assertThat(actual).isNotNull
            assertSoftly {
                it.assertThat(actual!!.id).isEqualTo(expected.id)
                if (ignoreUuid) {
                    it.assertThat(actual.uuid).isNotEmpty
                } else {
                    it.assertThat(actual.uuid).isEqualTo(expected.uuid)
                }
                it.assertThat(actual.content).isEqualTo(expected.content)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
        }
    }

    /**
     * Asserts list of pictures deep equals.
     *
     * @param expected expected list of pictures
     * @param actual   actual list of pictures
     */
    fun assertPicturesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Picture>, actual: List<String>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertThat(actual[i]).isEqualTo(expected[i].uuid)
            }
        }
    }

    /**
     * Asserts picture deep equals.
     *
     * @param expected   expected picture
     * @param actual    actual picture
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertPictureDeepEquals(expected: com.github.vhromada.catalog.domain.Picture, actual: Picture, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.content).isEqualTo(expected.content)
        }
    }

    /**
     * Asserts list of pictures deep equals.
     *
     * @param expected expected list of pictures
     * @param actual   actual list of pictures
     */
    fun assertPictureListDeepEquals(expected: List<Picture>, actual: List<String>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertThat(actual[i]).isEqualTo(expected[i].uuid)
            }
        }
    }

    /**
     * Asserts picture deep equals.
     *
     * @param expected   expected picture
     * @param actual     actual picture
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertPictureDeepEquals(expected: Picture, actual: Picture, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.content).isEqualTo(expected.content)
        }
    }

    /**
     * Asserts request and picture deep equals.
     *
     * @param expected expected request for changing picture
     * @param actual   actual picture
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangePictureRequest, actual: com.github.vhromada.catalog.domain.Picture, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.content).isEqualTo(expected.content)
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

}
