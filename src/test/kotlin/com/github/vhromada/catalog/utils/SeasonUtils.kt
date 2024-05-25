package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.Season
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates season fields.
 *
 * @return updated season
 */
fun com.github.vhromada.catalog.domain.Season.updated(): com.github.vhromada.catalog.domain.Season {
    number = 2
    startYear = SeasonUtils.START_YEAR
    endYear = SeasonUtils.START_YEAR + 1
    language = "SK"
    subtitles.clear()
    subtitles.add("CZ")
    note = "Note"
    return this
}

/**
 * Updates season fields.
 *
 * @return updated season
 */
fun Season.updated(): Season {
    return copy(
        number = 2,
        startYear = SeasonUtils.START_YEAR,
        endYear = SeasonUtils.START_YEAR + 1,
        language = "SK",
        subtitles = listOf("CZ"),
        note = "Note"
    )
}

/**
 * A class represents utility class for seasons.
 *
 * @author Vladimir Hromada
 */
object SeasonUtils {

    /**
     * Count of seasons
     */
    const val SEASONS_COUNT = 9

    /**
     * Count of seasons in show
     */
    const val SEASONS_PER_SHOW_COUNT = 3

    /**
     * Start year
     */
    const val START_YEAR = 2000

    /**
     * Year
     */
    private const val YEAR = 1980

    /**
     * Returns seasons.
     *
     * @param show show ID
     * @return seasons
     */
    fun getDomainSeasons(show: Int): MutableList<com.github.vhromada.catalog.domain.Season> {
        val seasons = mutableListOf<com.github.vhromada.catalog.domain.Season>()
        for (i in 1..SEASONS_PER_SHOW_COUNT) {
            seasons.add(getDomainSeason(showIndex = show, seasonIndex = i))
        }

        return seasons
    }

    /**
     * Returns seasons.
     *
     * @param show show ID
     * @return seasons
     */
    fun getSeasons(show: Int): List<Season> {
        val seasons = mutableListOf<Season>()
        for (i in 1..SEASONS_PER_SHOW_COUNT) {
            seasons.add(getSeason(showIndex = show, seasonIndex = i))
        }

        return seasons
    }

    /**
     * Returns season for indexes.
     *
     * @param showIndex   show index
     * @param seasonIndex season index
     * @return season for indexes
     */
    private fun getDomainSeason(showIndex: Int, seasonIndex: Int): com.github.vhromada.catalog.domain.Season {
        val season = com.github.vhromada.catalog.domain.Season(
            id = (showIndex - 1) * SEASONS_PER_SHOW_COUNT + seasonIndex,
            uuid = getUuid(index = (showIndex - 1) * SEASONS_PER_SHOW_COUNT + seasonIndex),
            number = seasonIndex,
            startYear = YEAR + seasonIndex,
            endYear = YEAR + if (seasonIndex == 3) 4 else 2,
            language = getLanguage(seasonIndex = seasonIndex),
            subtitles = getSubtitles(seasonIndex = seasonIndex),
            note = if (seasonIndex == 2) "Show $showIndex Season $seasonIndex note" else null,
            episodes = EpisodeUtils.getDomainEpisodes(show = showIndex, season = seasonIndex)
        ).fillAudit(audit = AuditUtils.getAudit())
        season.episodes.forEach { it.season = season }
        return season
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "40747bf4-6d09-40d6-90b4-b1457750c3f6"
            2 -> "566e6f76-cdbf-4859-9974-ebc1283b3923"
            3 -> "ce6ad908-1f7f-4eab-8792-77549c37caaa"
            4 -> "90dc1a3f-db32-4cd2-a6a7-9c2ceb126d5f"
            5 -> "cd3b50ae-7999-4731-9405-4b16a0a68cd2"
            6 -> "ddf6f872-cdf1-4ee9-8fb9-3197b4d415df"
            7 -> "6f7ea24a-706d-48ce-9d18-53c19b9e7591"
            8 -> "98324db3-caee-41f5-b465-e941c8711789"
            9 -> "23323afa-c26b-4d41-b705-fdf1b1b67d00"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns language for season index.
     *
     * @param seasonIndex season index
     * @return language for season index
     */
    private fun getLanguage(seasonIndex: Int): String {
        return when (seasonIndex) {
            1 -> "EN"
            2 -> "FR"
            3 -> "JP"
            else -> throw IllegalArgumentException("Bad season index")
        }
    }

    /**
     * Returns subtitles for season index.
     *
     * @param seasonIndex season index
     * @return subtitles for season index
     */
    private fun getSubtitles(seasonIndex: Int): MutableList<String> {
        val subtitles = mutableListOf<String>()
        when (seasonIndex) {
            1 -> {
                subtitles.add("CZ")
                subtitles.add("EN")
            }

            2 -> {
            }

            3 -> {
                subtitles.add("EN")
            }

            else -> throw IllegalArgumentException("Bad season index")
        }
        return subtitles
    }

    /**
     * Returns season.
     *
     * @param entityManager entity manager
     * @param id            season ID
     * @return season
     */
    fun getDomainSeason(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Season? {
        return entityManager.find(com.github.vhromada.catalog.domain.Season::class.java, id)
    }

    /**
     * Returns season for index.
     *
     * @param index season index
     * @return season for index
     */
    fun getSeason(index: Int): Season {
        val showNumber = (index - 1) / SEASONS_PER_SHOW_COUNT + 1
        val seasonNumber = (index - 1) % SEASONS_PER_SHOW_COUNT + 1

        return getSeason(showIndex = showNumber, seasonIndex = seasonNumber)
    }

    /**
     * Returns season for indexes.
     *
     * @param showIndex show index
     * @param seasonIndex  season index
     * @return season for indexes
     */
    private fun getSeason(showIndex: Int, seasonIndex: Int): Season {
        return Season(
            uuid = getUuid(index = (showIndex - 1) * SEASONS_PER_SHOW_COUNT + seasonIndex),
            number = seasonIndex,
            startYear = YEAR + seasonIndex,
            endYear = YEAR + if (seasonIndex == 3) 4 else 2,
            language = getLanguage(seasonIndex = seasonIndex),
            subtitles = getSubtitles(seasonIndex = seasonIndex),
            note = if (seasonIndex == 2) "Show $showIndex Season $seasonIndex note" else null,
            episodesCount = EpisodeUtils.EPISODES_PER_SEASON_COUNT,
            length = EpisodeUtils.getEpisodes(show = showIndex, season = seasonIndex).sumOf { it.length }
        )
    }

    /**
     * Returns count of seasons.
     *
     * @param entityManager entity manager
     * @return count of seasons
     */
    fun getSeasonsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(s.id) FROM Season s", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns season.
     *
     * @param id ID
     * @return season
     */
    fun newDomainSeason(id: Int?): com.github.vhromada.catalog.domain.Season {
        return com.github.vhromada.catalog.domain.Season(
            id = id,
            uuid = TestConstants.UUID,
            number = 0,
            startYear = 0,
            endYear = 0,
            language = "JP",
            subtitles = mutableListOf(),
            note = null,
            episodes = mutableListOf()
        ).updated()
    }

    /**
     * Returns season.
     *
     * @return season
     */
    fun newSeason(): Season {
        return Season(
            uuid = TestConstants.UUID,
            number = 0,
            startYear = 0,
            endYear = 0,
            language = "JP",
            subtitles = emptyList(),
            note = null,
            episodesCount = 0,
            length = 0
        ).updated()
    }

    /**
     * Returns request for changing season.
     *
     * @return request for changing season
     */
    fun newRequest(): ChangeSeasonRequest {
        return ChangeSeasonRequest(
            number = 2,
            startYear = START_YEAR,
            endYear = START_YEAR + 1,
            language = "SK",
            subtitles = listOf("CZ"),
            note = "Note"
        )
    }

    /**
     * Asserts list of seasons deep equals.
     *
     * @param expected   expected list of seasons
     * @param actual     actual list of seasons
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertDomainSeasonsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Season>, actual: List<com.github.vhromada.catalog.domain.Season>, ignoreUuid: Boolean = false) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertSeasonDeepEquals(expected = expected[i], actual = actual[i], ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts season deep equals.
     *
     * @param expected      expected season
     * @param actual        actual season
     * @param checkEpisodes true if episodes should be checked
     * @param ignoreUuid    true if UUID should be ignored
     */
    fun assertSeasonDeepEquals(expected: com.github.vhromada.catalog.domain.Season?, actual: com.github.vhromada.catalog.domain.Season?, checkEpisodes: Boolean = true, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.startYear).isEqualTo(expected.startYear)
                it.assertThat(actual.endYear).isEqualTo(expected.endYear)
                it.assertThat(actual.language).isEqualTo(expected.language)
                it.assertThat(actual.subtitles)
                    .hasSameSizeAs(expected.subtitles)
                    .hasSameElementsAs(expected.subtitles)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            if (checkEpisodes) {
                EpisodeUtils.assertDomainEpisodesDeepEquals(expected = expected.episodes, actual = actual.episodes, ignoreUuid = ignoreUuid)
            }
            if (expected.show != null) {
                assertThat(actual.show).isNotNull
                assertThat(actual.show!!.seasons).hasSameSizeAs(expected.show!!.seasons)
                ShowUtils.assertShowDeepEquals(expected = expected.show!!, actual = actual.show!!, checkSeasons = false, ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts list of seasons deep equals.
     *
     * @param expected expected list of seasons
     * @param actual   actual list of seasons
     */
    fun assertSeasonsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Season>, actual: List<Season>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertSeasonDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts season deep equals.
     *
     * @param expected   expected season
     * @param actual     actual season
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertSeasonDeepEquals(expected: com.github.vhromada.catalog.domain.Season, actual: Season, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.startYear).isEqualTo(expected.startYear)
            it.assertThat(actual.endYear).isEqualTo(expected.endYear)
            it.assertThat(actual.language).isEqualTo(expected.language)
            it.assertThat(actual.subtitles)
                .hasSameSizeAs(expected.subtitles)
                .hasSameElementsAs(expected.subtitles)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.episodesCount).isEqualTo(expected.episodes.size)
            it.assertThat(actual.length).isEqualTo(expected.episodes.sumOf { episode -> episode.length })
        }
    }

    /**
     * Asserts list of seasons deep equals.
     *
     * @param expected expected list of seasons
     * @param actual   actual list of seasons
     */
    fun assertSeasonListDeepEquals(expected: List<Season>, actual: List<Season>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertSeasonDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts season deep equals.
     *
     * @param expected   expected season
     * @param actual     actual season
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertSeasonDeepEquals(expected: Season, actual: Season, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.startYear).isEqualTo(expected.startYear)
            it.assertThat(actual.endYear).isEqualTo(expected.endYear)
            it.assertThat(actual.language).isEqualTo(expected.language)
            it.assertThat(actual.subtitles)
                .hasSameSizeAs(expected.subtitles)
                .hasSameElementsAs(expected.subtitles)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.episodesCount).isEqualTo(expected.episodesCount)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

    /**
     * Asserts request and season deep equals.
     *
     * @param expected expected request for changing season
     * @param actual   actual season
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeSeasonRequest, actual: com.github.vhromada.catalog.domain.Season, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.startYear).isEqualTo(expected.startYear)
            it.assertThat(actual.endYear).isEqualTo(expected.endYear)
            it.assertThat(actual.language).isEqualTo(expected.language)
            it.assertThat(actual.subtitles)
                .hasSameSizeAs(expected.subtitles)
                .hasSameElementsAs(expected.subtitles)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.episodes).isEmpty()
            it.assertThat(actual.show).isNull()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

}
