package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.io.EpisodeStatistics
import com.github.vhromada.catalog.entity.Episode
import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates episode fields.
 *
 * @return updated episode
 */
fun com.github.vhromada.catalog.domain.Episode.updated(): com.github.vhromada.catalog.domain.Episode {
    number = 2
    name = "Name"
    length = 5
    note = "Note"
    return this
}

/**
 * Updates episode fields.
 *
 * @return updated episode
 */
fun Episode.updated(): Episode {
    return copy(
        number = 2,
        name = "Name",
        length = 5,
        note = "Note"
    )
}

/**
 * A class represents utility class for episodes.
 *
 * @author Vladimir Hromada
 */
object EpisodeUtils {

    /**
     * Count of episodes
     */
    const val EPISODES_COUNT = 27

    /**
     * Count of episodes in season
     */
    const val EPISODES_PER_SEASON_COUNT = 3

    /**
     * Count of episodes in show
     */
    const val EPISODES_PER_SHOW_COUNT = 9

    /**
     * Multipliers for length
     */
    private val LENGTH_MULTIPLIERS = intArrayOf(1, 10, 100)

    /**
     * Returns episodes.
     *
     * @param show   show ID
     * @param season season ID
     * @return episodes
     */
    fun getDomainEpisodes(show: Int, season: Int): MutableList<com.github.vhromada.catalog.domain.Episode> {
        val episodes = mutableListOf<com.github.vhromada.catalog.domain.Episode>()
        for (i in 1..EPISODES_PER_SEASON_COUNT) {
            episodes.add(getDomainEpisode(showIndex = show, seasonIndex = season, episodeIndex = i))
        }

        return episodes
    }

    /**
     * Returns episodes.
     *
     * @param show   show ID
     * @param season season ID
     * @return episodes
     */
    fun getEpisodes(show: Int, season: Int): List<Episode> {
        val episodes = mutableListOf<Episode>()
        for (i in 1..EPISODES_PER_SEASON_COUNT) {
            episodes.add(getEpisode(showIndex = show, seasonIndex = season, episodeIndex = i))
        }

        return episodes
    }

    /**
     * Returns episode for indexes.
     *
     * @param showIndex    show index
     * @param seasonIndex  season index
     * @param episodeIndex episode index
     * @return episode for indexes
     */
    private fun getDomainEpisode(showIndex: Int, seasonIndex: Int, episodeIndex: Int): com.github.vhromada.catalog.domain.Episode {
        return com.github.vhromada.catalog.domain.Episode(
            id = (showIndex - 1) * EPISODES_PER_SHOW_COUNT + (seasonIndex - 1) * EPISODES_PER_SEASON_COUNT + episodeIndex,
            uuid = getUuid(index = (showIndex - 1) * EPISODES_PER_SHOW_COUNT + (seasonIndex - 1) * EPISODES_PER_SEASON_COUNT + episodeIndex),
            number = episodeIndex,
            name = "Show $showIndex Season $seasonIndex Episode $episodeIndex",
            length = episodeIndex * LENGTH_MULTIPLIERS[seasonIndex - 1],
            note = if (episodeIndex != 3) "Show $showIndex Season $seasonIndex Episode $episodeIndex note" else null
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
            1 -> "29f94c0f-aa0e-48b2-a74d-68991778871b"
            2 -> "fa623148-896d-40be-9702-9cb2d30c4f3e"
            3 -> "b58bb43c-b391-4de2-8972-08e13b8ab059"
            4 -> "71a04e1f-3218-4797-b9c0-de68f16262bf"
            5 -> "b4fad1a0-3f67-435a-8dd9-e27ae090dd17"
            6 -> "806f59b5-5e3d-478f-aeb8-fc2c7158801f"
            7 -> "77661951-c3ec-431f-a3a4-b7db93009504"
            8 -> "2aea5877-cee1-42d2-a421-f048409baaf6"
            9 -> "ccff3e04-25d2-4141-8a66-b2fd0c8938ec"
            10 -> "724861fb-43ac-450e-9ca4-f6454d16bd5a"
            11 -> "40fdfeb7-04df-4a02-a23a-4a5fb5927671"
            12 -> "ee456688-196e-4389-9e54-3aa31e3d8e8e"
            13 -> "5c272380-3c38-43f1-9726-226a9f182942"
            14 -> "4269d91c-4d36-4fd8-9de3-1134f09f6e4e"
            15 -> "3b35f6e3-e8e0-4677-a79a-c3b89f43345a"
            16 -> "6b602097-5072-422c-9691-93b074fc6cee"
            17 -> "c77e013c-bea9-4679-8e4e-df9aa622caa1"
            18 -> "9b9d0874-a483-4677-b90f-7b604c07926b"
            19 -> "057f61ef-0e20-497e-a0c0-4fc964a2ca0c"
            20 -> "8be0b3cd-30cf-4d47-8d67-c39f0bf6eb04"
            21 -> "a8a29438-e615-4721-8857-f92aad24b3f5"
            22 -> "abe105ee-6d9d-4c1e-a900-087555da3fb5"
            23 -> "2333c9e3-c9dc-4e40-8a42-4a6a7a079078"
            24 -> "b7111ede-c022-4967-bd2a-55d9d73d4cf7"
            25 -> "418234ed-59e8-4277-90ea-0e4ba87685e9"
            26 -> "05fe7e5f-0b5b-4cd3-91b9-d3b1d5ec203d"
            27 -> "54e645db-697c-45b1-8335-e4d65a0fd547"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns episode.
     *
     * @param entityManager entity manager
     * @param id            episode ID
     * @return episode
     */
    fun getDomainEpisode(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Episode? {
        return entityManager.find(com.github.vhromada.catalog.domain.Episode::class.java, id)
    }

    /**
     * Returns episode for index.
     *
     * @param index episode index
     * @return episode for index
     */
    fun getEpisode(index: Int): Episode {
        val showNumber = (index - 1) / EPISODES_PER_SHOW_COUNT + 1
        val seasonNumber = (index - 1) % EPISODES_PER_SHOW_COUNT / EPISODES_PER_SEASON_COUNT + 1
        val episodeNumber = (index - 1) % EPISODES_PER_SEASON_COUNT + 1

        return getEpisode(showIndex = showNumber, seasonIndex = seasonNumber, episodeIndex = episodeNumber)
    }

    /**
     * Returns episode for indexes.
     *
     * @param showIndex    show index
     * @param seasonIndex  season index
     * @param episodeIndex episode index
     * @return episode for indexes
     */
    private fun getEpisode(showIndex: Int, seasonIndex: Int, episodeIndex: Int): Episode {
        return Episode(
            uuid = getUuid(index = (showIndex - 1) * EPISODES_PER_SHOW_COUNT + (seasonIndex - 1) * EPISODES_PER_SEASON_COUNT + episodeIndex),
            number = episodeIndex,
            name = "Show $showIndex Season $seasonIndex Episode $episodeIndex",
            length = episodeIndex * LENGTH_MULTIPLIERS[seasonIndex - 1],
            note = if (episodeIndex != 3) "Show $showIndex Season $seasonIndex Episode $episodeIndex note" else null,
        )
    }

    /**
     * Returns statistics for episodes.
     *
     * @return statistics for episodes
     */
    fun getStatistics(): EpisodeStatistics {
        return EpisodeStatistics(count = EPISODES_COUNT.toLong(), length = 1998L)
    }

    /**
     * Returns count of episodes.
     *
     * @param entityManager entity manager
     * @return count of episodes
     */
    fun getEpisodesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(e.id) FROM Episode e", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns episode.
     *
     * @param id ID
     * @return episode
     */
    fun newDomainEpisode(id: Int?): com.github.vhromada.catalog.domain.Episode {
        return com.github.vhromada.catalog.domain.Episode(
            id = id,
            uuid = TestConstants.UUID,
            number = 0,
            name = "",
            length = 0,
            note = null
        ).updated()
    }

    /**
     * Returns episode.
     *
     * @return episode
     */
    fun newEpisode(): Episode {
        return Episode(
            uuid = TestConstants.UUID,
            number = 0,
            name = "",
            length = 0,
            note = null
        ).updated()
    }

    /**
     * Returns request for changing episode.
     *
     * @return request for changing episode
     */
    fun newRequest(): ChangeEpisodeRequest {
        return ChangeEpisodeRequest(
            name = "Name",
            number = 2,
            length = 5,
            note = "Note"
        )
    }

    /**
     * Asserts list of episodes deep equals.
     *
     * @param expected   expected list of episodes
     * @param actual     actual list of episodes
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertDomainEpisodesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Episode>, actual: List<com.github.vhromada.catalog.domain.Episode>, ignoreUuid: Boolean = false) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertEpisodeDeepEquals(expected = expected[i], actual = actual[i], ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts episode deep equals.
     *
     * @param expected   expected episode
     * @param actual     actual episode
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertEpisodeDeepEquals(expected: com.github.vhromada.catalog.domain.Episode?, actual: com.github.vhromada.catalog.domain.Episode?, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.number).isEqualTo(expected.number)
                it.assertThat(actual.name).isEqualTo(expected.name)
                it.assertThat(actual.length).isEqualTo(expected.length)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            if (expected.season != null) {
                assertThat(actual.season).isNotNull
                assertThat(actual.season!!.episodes).hasSameSizeAs(expected.season!!.episodes)
                SeasonUtils.assertSeasonDeepEquals(expected = expected.season!!, actual = actual.season!!, checkEpisodes = false, ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts list of episodes deep equals.
     *
     * @param expected expected list of episodes
     * @param actual   actual list of episodes
     */
    fun assertEpisodesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Episode>, actual: List<Episode>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertEpisodeDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts episode deep equals.
     *
     * @param expected   expected episode
     * @param actual     actual episode
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertEpisodeDeepEquals(expected: com.github.vhromada.catalog.domain.Episode, actual: Episode, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.length).isEqualTo(expected.length)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts list of episodes deep equals.
     *
     * @param expected expected list of episodes
     * @param actual   actual list of episodes
     */
    fun assertEpisodeListDeepEquals(expected: List<Episode>, actual: List<Episode>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertEpisodeDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts episode deep equals.
     *
     * @param expected   expected episode
     * @param actual     actual episode
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertEpisodeDeepEquals(expected: Episode, actual: Episode, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.length).isEqualTo(expected.length)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts request and episode deep equals.
     *
     * @param expected expected request for changing episode
     * @param actual   actual episode
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeEpisodeRequest, actual: com.github.vhromada.catalog.domain.Episode, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.length).isEqualTo(expected.length)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.season).isNull()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

    /**
     * Asserts statistics for episodes deep equals.
     *
     * @param expected expected statistics for episodes
     * @param actual   actual statistics for episodes
     */
    fun assertStatisticsDeepEquals(expected: EpisodeStatistics, actual: EpisodeStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

}
