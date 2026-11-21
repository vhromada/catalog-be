package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.Handbook
import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates handbook fields.
 *
 * @return updated handbook
 */
fun com.github.vhromada.catalog.domain.Handbook.updated(): com.github.vhromada.catalog.domain.Handbook {
    content = "Content"
    return this
}

/**
 * Updates handbook fields.
 *
 * @return updated handbook
 */
fun Handbook.updated(): Handbook {
    return copy(content = "Content")
}

/**
 * A class represents utility class for handbooks.
 *
 * @author Vladimir Hromada
 */
object HandbookUtils {

    /**
     * Count of handbooks
     */
    const val HANDBOOKS_COUNT = 2

    /**
     * Returns handbook for index.
     *
     * @param index index
     * @return handbook for index
     */
    fun getDomainHandbook(index: Int): com.github.vhromada.catalog.domain.Handbook {
        return com.github.vhromada.catalog.domain.Handbook(
            id = index,
            uuid = getUuid(index = index),
            content = "Handbook $index content"
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
            1 -> "1aa89acb-2eff-4c94-9161-1af303cb0219"
            2 -> "329c0a7b-56bc-4820-95fe-ced10e93f692"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns handbook.
     *
     * @param entityManager entity manager
     * @param id            handbook ID
     * @return handbook
     */
    fun getDomainHandbook(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Handbook? {
        return entityManager.find(com.github.vhromada.catalog.domain.Handbook::class.java, id)
    }

    /**
     * Returns handbook for index.
     *
     * @param index index
     * @return handbook for index
     */
    fun getHandbook(index: Int): Handbook {
        return Handbook(
            uuid = getUuid(index = index),
            content = "Handbook $index content"
        )
    }

    /**
     * Returns count of handbooks.
     *
     * @param entityManager entity manager
     * @return count of handbooks
     */
    fun getHandbooksCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(w.id) FROM Handbook w", Long::class.java).singleResult.toInt()
    }

    /**
     * Returns handbook.
     *
     * @param id ID
     * @return handbook
     */
    fun newDomainHandbook(id: Int?): com.github.vhromada.catalog.domain.Handbook {
        return com.github.vhromada.catalog.domain.Handbook(
            id = id,
            uuid = TestConstants.UUID,
            content = ""
        ).updated()
    }

    /**
     * Returns handbook.
     *
     * @return handbook
     */
    fun newHandbook(): Handbook {
        return Handbook(
            uuid = TestConstants.UUID,
            content = ""
        ).updated()
    }

    /**
     * Returns request for changing handbook.
     *
     * @return request for changing handbook
     */
    fun newRequest(): ChangeHandbookRequest {
        return ChangeHandbookRequest(
            content = "Content"
        )
    }

    /**
     * Asserts handbook deep equals.
     *
     * @param expected   expected handbook
     * @param actual     actual handbook
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertHandbookDeepEquals(
        expected: com.github.vhromada.catalog.domain.Handbook?,
        actual: com.github.vhromada.catalog.domain.Handbook?,
        ignoreUuid: Boolean = false
    ) {
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
            if (expected.game != null) {
                assertThat(actual.game).isNotNull
                GameUtils.assertGameDeepEquals(expected = expected.game!!, actual = actual.game!!, checkCheat = false, checkWalkthrough = false, checkHandbook = false)
            }
        }
    }

    /**
     * Asserts handbook deep equals.
     *
     * @param expected   expected handbook
     * @param actual     actual handbook
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertHandbookDeepEquals(expected: com.github.vhromada.catalog.domain.Handbook, actual: Handbook, ignoreUuid: Boolean = false) {
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
     * Asserts handbook deep equals.
     *
     * @param expected   expected handbook
     * @param actual     actual handbook
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertHandbookDeepEquals(expected: Handbook, actual: Handbook, ignoreUuid: Boolean = false) {
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
     * Asserts request and handbook deep equals.
     *
     * @param expected expected request for changing handbook
     * @param actual   actual handbook
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeHandbookRequest, actual: com.github.vhromada.catalog.domain.Handbook, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.content).isEqualTo(expected.content)
            it.assertThat(actual.game).isNull()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

}
