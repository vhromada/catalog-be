package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.CheatData
import com.github.vhromada.catalog.entity.io.ChangeCheatData
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates cheat's data fields.
 *
 * @return updated cheat's data
 */
fun com.github.vhromada.catalog.domain.CheatData.updated(): com.github.vhromada.catalog.domain.CheatData {
    action = "action"
    description = "description"
    return this
}

/**
 * Updates cheat's data fields.
 *
 * @return updated cheat's data
 */
fun CheatData.updated(): CheatData {
    return copy(
        action = "action",
        description = "description"
    )
}

/**
 * A class represents utility class for cheat's data.
 *
 * @author Vladimir Hromada
 */
object CheatDataUtils {

    /**
     * Count of cheat's data
     */
    const val CHEAT_DATA_COUNT = 6

    /**
     * Count of cheat's data in cheat
     */
    const val CHEAT_DATA_CHEAT_COUNT = 3

    /**
     * Returns list of cheat's data.
     *
     * @param cheat cheat ID
     * @return list of cheat's data
     */
    fun getDomainCheatDataList(cheat: Int): MutableList<com.github.vhromada.catalog.domain.CheatData> {
        val cheatData = mutableListOf<com.github.vhromada.catalog.domain.CheatData>()
        for (i in 1..CHEAT_DATA_CHEAT_COUNT) {
            cheatData.add(getDomainCheatData(cheatIndex = cheat, cheatDataIndex = i))
        }

        return cheatData
    }

    /**
     * Returns list of cheat's data.
     *
     * @param cheat cheat ID
     * @return list of cheat's data
     */
    fun getCheatDataList(cheat: Int): MutableList<CheatData> {
        val cheatData = mutableListOf<CheatData>()
        for (i in 1..CHEAT_DATA_CHEAT_COUNT) {
            cheatData.add(getCheatData(cheatIndex = cheat, cheatDataIndex = i))
        }

        return cheatData
    }

    /**
     * Returns cheat's data for indexes.
     *
     * @param cheatIndex     cheat index
     * @param cheatDataIndex cheat's data index
     * @return cheat's data for indexes
     */
    private fun getDomainCheatData(cheatIndex: Int, cheatDataIndex: Int): com.github.vhromada.catalog.domain.CheatData {
        return com.github.vhromada.catalog.domain.CheatData(
            id = (cheatIndex - 1) * CHEAT_DATA_CHEAT_COUNT + cheatDataIndex,
            action = "Cheat $cheatIndex Data $cheatDataIndex action",
            description = "Cheat $cheatIndex Data $cheatDataIndex description"
        ).fillAudit(audit = AuditUtils.getAudit())
    }

    /**
     * Returns cheat's data for indexes.
     *
     * @param cheatIndex     cheat index
     * @param cheatDataIndex cheat's data index
     * @return cheat's data for indexes
     */
    private fun getCheatData(cheatIndex: Int, cheatDataIndex: Int): CheatData {
        return CheatData(
            action = "Cheat $cheatIndex Data $cheatDataIndex action",
            description = "Cheat $cheatIndex Data $cheatDataIndex description"
        )
    }

    /**
     * Returns count of cheat's data.
     *
     * @param entityManager entity manager
     * @return count of cheat's data
     */
    fun getCheatDataCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(c.id) FROM CheatData c", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns cheat's data.
     *
     * @param id ID
     * @return cheat's data
     */
    fun newDomainCheatData(id: Int?): com.github.vhromada.catalog.domain.CheatData {
        return com.github.vhromada.catalog.domain.CheatData(
            id = id,
            action = "",
            description = ""
        ).updated()
    }

    /**
     * Returns cheat's data.
     *
     * @return cheat's data
     */
    fun newCheatData(): CheatData {
        return CheatData(
            action = "",
            description = ""
        ).updated()
    }

    /**
     * Returns changing cheat's data.
     *
     * @return changing cheat's data
     */
    fun newRequest(): ChangeCheatData {
        return ChangeCheatData(
            action = "action",
            description = "description"
        )
    }

    /**
     * Asserts list of cheat's data deep equals.
     *
     * @param expected expected list of cheat's data
     * @param actual   actual list of cheat's data
     */
    fun assertDomainCheatDataDeepEquals(expected: List<com.github.vhromada.catalog.domain.CheatData>, actual: List<com.github.vhromada.catalog.domain.CheatData>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertCheatDataDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts cheat's data deep equals.
     *
     * @param expected expected cheat's data
     * @param actual   actual cheat's data
     */
    private fun assertCheatDataDeepEquals(expected: com.github.vhromada.catalog.domain.CheatData?, actual: com.github.vhromada.catalog.domain.CheatData?) {
        if (expected == null) {
            assertThat(actual).isNull()
        } else {
            assertThat(actual).isNotNull
            assertSoftly {
                it.assertThat(actual!!.id).isEqualTo(expected.id)
                it.assertThat(actual.action).isEqualTo(expected.action)
                it.assertThat(actual.description).isEqualTo(expected.description)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
        }
    }

    /**
     * Asserts list of cheat's data deep equals.
     *
     * @param expected expected list of cheat's data
     * @param actual   actual list of cheat's data
     */
    fun assertCheatDataDeepEquals(expected: List<com.github.vhromada.catalog.domain.CheatData>, actual: List<CheatData>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertCheatDataDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts cheat's data deep equals.
     *
     * @param expected expected cheat's data
     * @param actual   actual cheat's data
     */
    private fun assertCheatDataDeepEquals(expected: com.github.vhromada.catalog.domain.CheatData, actual: CheatData) {
        assertSoftly {
            it.assertThat(actual.action).isEqualTo(expected.action)
            it.assertThat(actual.description).isEqualTo(expected.description)
        }
    }

    /**
     * Asserts list of cheat's data deep equals.
     *
     * @param expected expected list of cheat's data
     * @param actual   actual list of cheat's data
     */
    fun assertCheatDataListDeepEquals(expected: List<CheatData>, actual: List<CheatData>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertCheatDataDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts cheat's data deep equals.
     *
     * @param expected expected cheat's data
     * @param actual   actual cheat's data
     */
    private fun assertCheatDataDeepEquals(expected: CheatData, actual: CheatData) {
        assertSoftly {
            it.assertThat(actual.action).isEqualTo(expected.action)
            it.assertThat(actual.description).isEqualTo(expected.description)
        }
    }

    /**
     * Asserts list of requests and list of cheat's data deep equals.
     *
     * @param expected expected list of changing cheat's data
     * @param actual   actual list of cheat's data
     */
    fun assertRequestsDeepEquals(expected: List<ChangeCheatData>, actual: List<com.github.vhromada.catalog.domain.CheatData>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertRequestDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts request and cheat's data deep equals.
     *
     * @param expected expected changing cheat's data
     * @param actual   actual cheat's data
     */
    fun assertRequestDeepEquals(expected: ChangeCheatData, actual: com.github.vhromada.catalog.domain.CheatData) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.action).isEqualTo(expected.action)
            it.assertThat(actual.description).isEqualTo(expected.description)
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

}
