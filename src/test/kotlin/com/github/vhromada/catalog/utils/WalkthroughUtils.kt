package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.Walkthrough
import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates walkthrough fields.
 *
 * @return updated walkthrough
 */
fun com.github.vhromada.catalog.domain.Walkthrough.updated(): com.github.vhromada.catalog.domain.Walkthrough {
    content = "Content"
    return this
}

/**
 * Updates walkthrough fields.
 *
 * @return updated walkthrough
 */
fun Walkthrough.updated(): Walkthrough {
    return copy(content = "Content")
}

/**
 * A class represents utility class for walkthroughs.
 *
 * @author Vladimir Hromada
 */
object WalkthroughUtils {

    /**
     * Count of walkthroughs
     */
    const val WALKTHROUGHS_COUNT = 2

    /**
     * Returns walkthrough for index.
     *
     * @param index index
     * @return walkthrough for index
     */
    fun getDomainWalkthrough(index: Int): com.github.vhromada.catalog.domain.Walkthrough {
        return com.github.vhromada.catalog.domain.Walkthrough(
            id = index,
            uuid = getUuid(index = index),
            content = "Walkthrough $index content"
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
            1 -> "5d1b7a0e-3b8c-4a52-9a0f-7b0e2f6f9c11"
            2 -> "c4f3e8a2-91d6-4e0b-8b47-2a6d5c1e7f30"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns walkthrough.
     *
     * @param entityManager entity manager
     * @param id            walkthrough ID
     * @return walkthrough
     */
    fun getDomainWalkthrough(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Walkthrough? {
        return entityManager.find(com.github.vhromada.catalog.domain.Walkthrough::class.java, id)
    }

    /**
     * Returns walkthrough for index.
     *
     * @param index index
     * @return walkthrough for index
     */
    fun getWalkthrough(index: Int): Walkthrough {
        return Walkthrough(
            uuid = getUuid(index = index),
            content = "Walkthrough $index content"
        )
    }

    /**
     * Returns count of walkthroughs.
     *
     * @param entityManager entity manager
     * @return count of walkthroughs
     */
    fun getWalkthroughsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(w.id) FROM Walkthrough w", Long::class.java).singleResult.toInt()
    }

    /**
     * Returns walkthrough.
     *
     * @param id ID
     * @return walkthrough
     */
    fun newDomainWalkthrough(id: Int?): com.github.vhromada.catalog.domain.Walkthrough {
        return com.github.vhromada.catalog.domain.Walkthrough(
            id = id,
            uuid = TestConstants.UUID,
            content = ""
        ).updated()
    }

    /**
     * Returns walkthrough.
     *
     * @return walkthrough
     */
    fun newWalkthrough(): Walkthrough {
        return Walkthrough(
            uuid = TestConstants.UUID,
            content = ""
        ).updated()
    }

    /**
     * Returns request for changing walkthrough.
     *
     * @return request for changing walkthrough
     */
    fun newRequest(): ChangeWalkthroughRequest {
        return ChangeWalkthroughRequest(
            content = "Content"
        )
    }

    /**
     * Asserts walkthrough deep equals.
     *
     * @param expected   expected walkthrough
     * @param actual     actual walkthrough
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertWalkthroughDeepEquals(
        expected: com.github.vhromada.catalog.domain.Walkthrough?,
        actual: com.github.vhromada.catalog.domain.Walkthrough?,
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
     * Asserts walkthrough deep equals.
     *
     * @param expected   expected walkthrough
     * @param actual     actual walkthrough
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertWalkthroughDeepEquals(expected: com.github.vhromada.catalog.domain.Walkthrough, actual: Walkthrough, ignoreUuid: Boolean = false) {
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
     * Asserts walkthrough deep equals.
     *
     * @param expected   expected walkthrough
     * @param actual     actual walkthrough
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertWalkthroughDeepEquals(expected: Walkthrough, actual: Walkthrough, ignoreUuid: Boolean = false) {
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
     * Asserts request and walkthrough deep equals.
     *
     * @param expected expected request for changing walkthrough
     * @param actual   actual walkthrough
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeWalkthroughRequest, actual: com.github.vhromada.catalog.domain.Walkthrough, uuid: String) {
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
