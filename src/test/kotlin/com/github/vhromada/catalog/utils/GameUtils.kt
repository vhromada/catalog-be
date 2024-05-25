package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.filter.GameFilter
import com.github.vhromada.catalog.domain.io.GameStatistics
import com.github.vhromada.catalog.entity.Game
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGameRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates game fields.
 *
 * @return updated game
 */
fun com.github.vhromada.catalog.domain.Game.updated(): com.github.vhromada.catalog.domain.Game {
    name = "Name"
    normalizedName = "Name"
    wikiEn = "enWiki"
    wikiCz = "czWiki"
    mediaCount = 1
    format = "STEAM"
    crack = true
    serialKey = true
    patch = true
    trainer = true
    trainerData = true
    editor = true
    saves = true
    otherData = "Other data"
    note = "Note"
    return this
}

/**
 * Updates game fields.
 *
 * @return updated game
 */
fun Game.updated(): Game {
    return copy(
        name = "Name",
        wikiEn = "enWiki",
        wikiCz = "czWiki",
        mediaCount = 1,
        format = "STEAM",
        crack = true,
        serialKey = true,
        patch = true,
        trainer = true,
        trainerData = true,
        editor = true,
        saves = true,
        otherData = "Other data",
        note = "Note"
    )
}

/**
 * A class represents utility class for games.
 *
 * @author Vladimir Hromada
 */
object GameUtils {

    /**
     * Count of games
     */
    const val GAMES_COUNT = 3

    /**
     * Returns games.
     *
     * @return games
     */
    fun getDomainGames(): List<com.github.vhromada.catalog.domain.Game> {
        val games = mutableListOf<com.github.vhromada.catalog.domain.Game>()
        for (i in 1..GAMES_COUNT) {
            games.add(getDomainGame(index = i))
        }

        return games
    }

    /**
     * Returns games.
     *
     * @return games
     */
    fun getGames(): List<Game> {
        val games = mutableListOf<Game>()
        for (i in 1..GAMES_COUNT) {
            games.add(getGame(index = i))
        }

        return games
    }

    /**
     * Returns game for index.
     *
     * @param index index
     * @return game for index
     */
    fun getDomainGame(index: Int): com.github.vhromada.catalog.domain.Game {
        val name = "Game $index name"
        val game = com.github.vhromada.catalog.domain.Game(
            id = index,
            uuid = getUuid(index = index),
            name = name,
            normalizedName = name,
            wikiEn = if (index != 1) "Game $index English Wikipedia" else null,
            wikiCz = if (index != 3) "Game $index Czech Wikipedia" else null,
            mediaCount = index,
            format = getFormat(index = index),
            cheat = if (index == 1) null else CheatUtils.getDomainCheat(index = index - 1),
            crack = index != 1,
            serialKey = index != 1,
            patch = index != 1,
            trainer = index != 1,
            trainerData = index == 3,
            editor = index == 3,
            saves = index == 3,
            otherData = if (index == 3) "Game $index other data" else null,
            note = if (index == 3) "Game $index note" else null
        ).fillAudit(audit = AuditUtils.getAudit())
        game.cheat?.game = game
        return game
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "e7598ddd-c378-4e61-b603-f9e2c928fac8"
            2 -> "f43a586b-011b-461c-ad10-483284775fda"
            3 -> "cba17f52-7770-4dd6-9747-48c20a52ebc5"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns format for index.
     *
     * @param index index
     * @return format for index
     */
    private fun getFormat(index: Int): String {
        return when (index) {
            1 -> "ISO"
            2 -> "STEAM"
            3 -> "BINARY"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns game.
     *
     * @param entityManager entity manager
     * @param id            game ID
     * @return game
     */
    fun getDomainGame(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Game? {
        return entityManager.find(com.github.vhromada.catalog.domain.Game::class.java, id)
    }

    /**
     * Returns game for index.
     *
     * @param index index
     * @return game for index
     */
    fun getGame(index: Int): Game {
        return Game(
            uuid = getUuid(index = index),
            name = "Game $index name",
            wikiEn = if (index != 1) "Game $index English Wikipedia" else null,
            wikiCz = if (index != 3) "Game $index Czech Wikipedia" else null,
            mediaCount = index,
            format = getFormat(index = index),
            crack = index != 1,
            serialKey = index != 1,
            patch = index != 1,
            trainer = index != 1,
            trainerData = index == 3,
            editor = index == 3,
            saves = index == 3,
            otherData = if (index == 3) "Game $index other data" else null,
            note = if (index == 3) "Game $index note" else null,
            cheat = index != 1
        )
    }

    /**
     * Returns statistics for games.
     *
     * @return statistics for games
     */
    fun getDomainStatistics(): GameStatistics {
        return GameStatistics(count = GAMES_COUNT.toLong(), mediaCount = 6L)
    }

    /**
     * Returns statistics for games.
     *
     * @return statistics for games
     */
    fun getStatistics(): com.github.vhromada.catalog.entity.GameStatistics {
        return com.github.vhromada.catalog.entity.GameStatistics(count = GAMES_COUNT, mediaCount = 6)
    }

    /**
     * Returns count of games.
     *
     * @param entityManager entity manager
     * @return count of games
     */
    fun getGamesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(g.id) FROM Game g", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns game.
     *
     * @param id ID
     * @return game
     */
    fun newDomainGame(id: Int?): com.github.vhromada.catalog.domain.Game {
        return com.github.vhromada.catalog.domain.Game(
            id = id,
            uuid = TestConstants.UUID,
            name = "",
            normalizedName = "",
            wikiEn = null,
            wikiCz = null,
            mediaCount = 0,
            format = "STEAM",
            cheat = null,
            crack = false,
            serialKey = false,
            patch = false,
            trainer = false,
            trainerData = false,
            editor = false,
            saves = false,
            otherData = null,
            note = null
        ).updated()
    }

    /**
     * Returns game.
     *
     * @return game
     */
    fun newGame(): Game {
        return Game(
            uuid = TestConstants.UUID,
            name = "",
            wikiEn = null,
            wikiCz = null,
            mediaCount = 0,
            format = "STEAM",
            crack = false,
            serialKey = false,
            patch = false,
            trainer = false,
            trainerData = false,
            editor = false,
            saves = false,
            otherData = null,
            note = null,
            cheat = false
        ).updated()
    }

    /**
     * Returns request for changing game.
     *
     * @return request for changing game
     */
    fun newRequest(): ChangeGameRequest {
        return ChangeGameRequest(
            name = "Name",
            wikiEn = "enWiki",
            wikiCz = "czWiki",
            mediaCount = 1,
            format = "STEAM",
            crack = true,
            serialKey = true,
            patch = true,
            trainer = true,
            trainerData = true,
            editor = true,
            saves = true,
            otherData = "Other data",
            note = "Note"
        )
    }

    /**
     * Asserts list of games deep equals.
     *
     * @param expected expected list of games
     * @param actual   actual list of games
     */
    fun assertDomainGamesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Game>, actual: List<com.github.vhromada.catalog.domain.Game>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertGameDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts game deep equals.
     *
     * @param expected   expected game
     * @param actual     actual game
     * @param checkCheat true if cheat should be checked
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertGameDeepEquals(expected: com.github.vhromada.catalog.domain.Game?, actual: com.github.vhromada.catalog.domain.Game?, checkCheat: Boolean = true, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.name).isEqualTo(expected.name)
                it.assertThat(actual.normalizedName).isEqualTo(expected.normalizedName)
                it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
                it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
                it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
                it.assertThat(actual.format).isEqualTo(expected.format)
                it.assertThat(actual.crack).isEqualTo(expected.crack)
                it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
                it.assertThat(actual.patch).isEqualTo(expected.patch)
                it.assertThat(actual.trainer).isEqualTo(expected.trainer)
                it.assertThat(actual.trainerData).isEqualTo(expected.trainerData)
                it.assertThat(actual.editor).isEqualTo(expected.editor)
                it.assertThat(actual.saves).isEqualTo(expected.saves)
                it.assertThat(actual.otherData).isEqualTo(expected.otherData)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            if (checkCheat) {
                if (expected.cheat == null) {
                    assertThat(actual.cheat).isNull()
                } else {
                    CheatUtils.assertCheatDeepEquals(expected = expected.cheat, actual = actual.cheat!!)
                }
            }
        }
    }

    /**
     * Asserts list of games deep equals.
     *
     * @param expected expected list of games
     * @param actual   actual list of games
     */
    fun assertGamesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Game>, actual: List<Game>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertGameDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts game deep equals.
     *
     * @param expected   expected game
     * @param actual     actual game
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertGameDeepEquals(expected: com.github.vhromada.catalog.domain.Game, actual: Game, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.crack).isEqualTo(expected.crack)
            it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
            it.assertThat(actual.patch).isEqualTo(expected.patch)
            it.assertThat(actual.trainer).isEqualTo(expected.trainer)
            it.assertThat(actual.trainerData).isEqualTo(expected.trainerData)
            it.assertThat(actual.editor).isEqualTo(expected.editor)
            it.assertThat(actual.saves).isEqualTo(expected.saves)
            it.assertThat(actual.otherData).isEqualTo(expected.otherData)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.cheat).isEqualTo(expected.cheat !== null)
        }
    }

    /**
     * Asserts list of games deep equals.
     *
     * @param expected expected list of games
     * @param actual   actual list of games
     */
    fun assertGameListDeepEquals(expected: List<Game>, actual: List<Game>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertGameDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts game deep equals.
     *
     * @param expected   expected game
     * @param actual     actual game
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertGameDeepEquals(expected: Game, actual: Game, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.crack).isEqualTo(expected.crack)
            it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
            it.assertThat(actual.patch).isEqualTo(expected.patch)
            it.assertThat(actual.trainer).isEqualTo(expected.trainer)
            it.assertThat(actual.trainerData).isEqualTo(expected.trainerData)
            it.assertThat(actual.editor).isEqualTo(expected.editor)
            it.assertThat(actual.saves).isEqualTo(expected.saves)
            it.assertThat(actual.otherData).isEqualTo(expected.otherData)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.cheat).isEqualTo(expected.cheat)
        }
    }

    /**
     * Asserts request and game deep equals.
     *
     * @param expected expected request for changing game
     * @param actual   actual game
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeGameRequest, actual: com.github.vhromada.catalog.domain.Game, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.normalizedName).isEqualTo(expected.name)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.cheat).isNull()
            it.assertThat(actual.crack).isEqualTo(expected.crack)
            it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
            it.assertThat(actual.otherData).isEqualTo(expected.otherData)
            it.assertThat(actual.patch).isEqualTo(expected.patch)
            it.assertThat(actual.trainer).isEqualTo(expected.trainer)
            it.assertThat(actual.trainerData).isEqualTo(expected.trainerData)
            it.assertThat(actual.editor).isEqualTo(expected.editor)
            it.assertThat(actual.saves).isEqualTo(expected.saves)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

    /**
     * Asserts filter deep equals.
     *
     * @param expected expected filter
     * @param actual   actual filter
     */
    fun assertFilterDeepEquals(expected: NameFilter, actual: GameFilter) {
        assertThat(actual.name).isEqualTo(expected.name)
    }

    /**
     * Asserts statistics for games deep equals.
     *
     * @param expected expected statistics for games
     * @param actual   actual statistics for games
     */
    fun assertStatisticsDeepEquals(expected: GameStatistics, actual: GameStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
        }
    }

    /**
     * Asserts statistics for games deep equals.
     *
     * @param expected expected statistics for games
     * @param actual   actual statistics for games
     */
    fun assertStatisticsDeepEquals(expected: GameStatistics, actual: com.github.vhromada.catalog.entity.GameStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            if (expected.mediaCount == null) {
                it.assertThat(actual.mediaCount).isZero
            } else {
                it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount!!.toInt())
            }
        }
    }

}
