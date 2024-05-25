package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.Cheat
import com.github.vhromada.catalog.entity.io.ChangeCheatRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates cheat fields.
 *
 * @return updated cheat
 */
fun com.github.vhromada.catalog.domain.Cheat.updated(): com.github.vhromada.catalog.domain.Cheat {
    gameSetting = "gameSetting"
    cheatSetting = "cheatSetting"
    return this
}

/**
 * Updates cheat fields.
 *
 * @return updated cheat
 */
fun Cheat.updated(): Cheat {
    return copy(
        gameSetting = "gameSetting",
        cheatSetting = "cheatSetting"
    )
}

/**
 * A class represents utility class for cheats.
 *
 * @author Vladimir Hromada
 */
object CheatUtils {

    /**
     * Count of cheats
     */
    const val CHEATS_COUNT = 2

    /**
     * Returns cheat for index.
     *
     * @param index index
     * @return cheat for index
     */
    fun getDomainCheat(index: Int): com.github.vhromada.catalog.domain.Cheat {
        return com.github.vhromada.catalog.domain.Cheat(
            id = index,
            uuid = getUuid(index = index),
            gameSetting = "Game $index setting",
            cheatSetting = "Cheat $index setting",
            data = CheatDataUtils.getDomainCheatDataList(cheat = index)
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
            1 -> "e0a6f352-0b41-4a6d-b71b-69a11e0578bc"
            2 -> "1ce7a9b3-64ac-4cd0-a58a-225f5a2e7849"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns cheat.
     *
     * @param entityManager entity manager
     * @param id            cheat ID
     * @return cheat
     */
    fun getDomainCheat(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Cheat? {
        return entityManager.find(com.github.vhromada.catalog.domain.Cheat::class.java, id)
    }

    /**
     * Returns cheat for index.
     *
     * @param index index
     * @return cheat for index
     */
    fun getCheat(index: Int): Cheat {
        return Cheat(
            uuid = getUuid(index = index),
            gameSetting = "Game $index setting",
            cheatSetting = "Cheat $index setting",
            data = CheatDataUtils.getCheatDataList(cheat = index)
        )
    }

    /**
     * Returns count of cheats.
     *
     * @param entityManager entity manager
     * @return count of cheats
     */
    fun getCheatsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(c.id) FROM Cheat c", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns cheat.
     *
     * @param id ID
     * @return cheat
     */
    fun newDomainCheat(id: Int?): com.github.vhromada.catalog.domain.Cheat {
        return com.github.vhromada.catalog.domain.Cheat(
            id = id,
            uuid = TestConstants.UUID,
            gameSetting = "",
            cheatSetting = "",
            data = mutableListOf(CheatDataUtils.newDomainCheatData(id = id))
        ).updated()
    }

    /**
     * Returns cheat.
     *
     * @return cheat
     */
    fun newCheat(): Cheat {
        return Cheat(
            uuid = TestConstants.UUID,
            gameSetting = "",
            cheatSetting = "",
            data = listOf(CheatDataUtils.newCheatData())
        ).updated()
    }

    /**
     * Returns request for changing cheat.
     *
     * @return request for changing cheat
     */
    fun newRequest(): ChangeCheatRequest {
        return ChangeCheatRequest(
            gameSetting = "gameSetting",
            cheatSetting = "cheatSetting",
            data = listOf(CheatDataUtils.newRequest())
        )
    }

    /**
     * Asserts cheat deep equals.
     *
     * @param expected   expected cheat
     * @param actual     actual cheat
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertCheatDeepEquals(expected: com.github.vhromada.catalog.domain.Cheat?, actual: com.github.vhromada.catalog.domain.Cheat?, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.gameSetting).isEqualTo(expected.gameSetting)
                it.assertThat(actual.cheatSetting).isEqualTo(expected.cheatSetting)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            CheatDataUtils.assertDomainCheatDataDeepEquals(expected = expected.data, actual = actual.data)
            if (expected.game != null) {
                assertThat(actual.game).isNotNull
                GameUtils.assertGameDeepEquals(expected = expected.game!!, actual = actual.game!!, checkCheat = false)
            }
        }
    }

    /**
     * Asserts cheat deep equals.
     *
     * @param expected   expected cheat
     * @param actual     actual cheat
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertCheatDeepEquals(expected: com.github.vhromada.catalog.domain.Cheat, actual: Cheat, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.gameSetting).isEqualTo(expected.gameSetting)
            it.assertThat(actual.cheatSetting).isEqualTo(expected.cheatSetting)
        }
        CheatDataUtils.assertCheatDataDeepEquals(expected = expected.data, actual = actual.data)
    }

    /**
     * Asserts cheat deep equals.
     *
     * @param expected   expected cheat
     * @param actual     actual cheat
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertCheatDeepEquals(expected: Cheat, actual: Cheat, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.gameSetting).isEqualTo(expected.gameSetting)
            it.assertThat(actual.cheatSetting).isEqualTo(expected.cheatSetting)
        }
        CheatDataUtils.assertCheatDataListDeepEquals(expected = expected.data, actual = actual.data)
    }

    /**
     * Asserts request and cheat deep equals.
     *
     * @param expected expected request for changing cheat
     * @param actual   actual cheat
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeCheatRequest, actual: com.github.vhromada.catalog.domain.Cheat, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.gameSetting).isEqualTo(expected.gameSetting)
            it.assertThat(actual.cheatSetting).isEqualTo(expected.cheatSetting)
            it.assertThat(actual.game).isNull()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
        CheatDataUtils.assertRequestsDeepEquals(expected = expected.data!!.filterNotNull(), actual = actual.data)
    }

}
