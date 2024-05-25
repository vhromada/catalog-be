package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.domain.filter.ShowFilter
import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.Show
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates show fields.
 *
 * @return updated show
 */
fun com.github.vhromada.catalog.domain.Show.updated(): com.github.vhromada.catalog.domain.Show {
    czechName = "czName"
    normalizedCzechName = "czName"
    originalName = "origName"
    normalizedOriginalName = "origName"
    csfd = "Csfd"
    imdbCode = 1000
    wikiEn = "enWiki"
    wikiCz = "czWiki"
    note = "Note"
    return this
}

/**
 * Updates show fields.
 *
 * @return updated show
 */
fun Show.updated(): Show {
    return copy(
        czechName = "czName",
        originalName = "origName",
        csfd = "Csfd",
        imdbCode = 1000,
        wikiEn = "enWiki",
        wikiCz = "czWiki",
        note = "Note"
    )
}

/**
 * A class represents utility class for shows.
 *
 * @author Vladimir Hromada
 */
object ShowUtils {

    /**
     * Count of shows
     */
    const val SHOWS_COUNT = 3

    /**
     * IMDB multiplier
     */
    private const val IMDB_MULTIPLIER = 100

    /**
     * Returns shows.
     *
     * @return shows
     */
    fun getDomainShows(): List<com.github.vhromada.catalog.domain.Show> {
        val shows = mutableListOf<com.github.vhromada.catalog.domain.Show>()
        for (i in 1..SHOWS_COUNT) {
            shows.add(getDomainShow(index = i))
        }

        return shows
    }

    /**
     * Returns shows.
     *
     * @return shows
     */
    fun getShows(): List<Show> {
        val shows = mutableListOf<Show>()
        for (i in 1..SHOWS_COUNT) {
            shows.add(getShow(index = i))
        }

        return shows
    }

    /**
     * Returns show for index.
     *
     * @param index index
     * @return show for index
     */
    fun getDomainShow(index: Int): com.github.vhromada.catalog.domain.Show {
        val czechName = "Show $index czech name"
        val originalName = "Show $index original name"
        val show = com.github.vhromada.catalog.domain.Show(
            id = index,
            uuid = getUuid(index = index),
            czechName = czechName,
            normalizedCzechName = czechName,
            originalName = originalName,
            normalizedOriginalName = originalName,
            csfd = if (index != 2) "Show $index CSFD" else null,
            imdbCode = if (index != 3) index * IMDB_MULTIPLIER else null,
            wikiEn = if (index != 1) "Show $index English Wikipedia" else null,
            wikiCz = if (index != 1) "Show $index Czech Wikipedia" else null,
            picture = SHOWS_COUNT + index,
            note = if (index == 3) "Show $index note" else null,
            genres = getDomainGenres(index = index),
            seasons = SeasonUtils.getDomainSeasons(show = index)
        ).fillAudit(audit = AuditUtils.getAudit())
        show.seasons.forEach { it.show = show }
        return show
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "07b38096-d66b-40f1-978f-e3b07ecb4d92"
            2 -> "c606aac0-6520-488a-8c77-d171bac036c7"
            3 -> "49d4a237-4b92-43c4-8cd5-a8cd20a0c0de"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns genres for index.
     *
     * @param index index
     * @return genres for index
     */
    private fun getDomainGenres(index: Int): MutableList<com.github.vhromada.catalog.domain.Genre> {
        val genres = mutableListOf<com.github.vhromada.catalog.domain.Genre>()
        genres.add(GenreUtils.getDomainGenre(index = index))
        if (index == 3) {
            genres.add(GenreUtils.getDomainGenre(index = 4))
        }
        return genres
    }

    /**
     * Returns show.
     *
     * @param entityManager entity manager
     * @param id            show ID
     * @return show
     */
    fun getDomainShow(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Show? {
        return entityManager.find(com.github.vhromada.catalog.domain.Show::class.java, id)
    }

    /**
     * Returns show for index.
     *
     * @param index index
     * @return show for index
     */
    fun getShow(index: Int): Show {
        return Show(
            uuid = getUuid(index = index),
            czechName = "Show $index czech name",
            originalName = "Show $index original name",
            csfd = if (index != 2) "Show $index CSFD" else null,
            imdbCode = if (index != 3) index * IMDB_MULTIPLIER else null,
            wikiEn = if (index != 1) "Show $index English Wikipedia" else null,
            wikiCz = if (index != 1) "Show $index Czech Wikipedia" else null,
            picture = PictureUtils.getPicture(index = SHOWS_COUNT + index).uuid,
            note = if (index == 3) "Show $index note" else null,
            genres = getGenres(index = index),
            seasonsCount = SeasonUtils.SEASONS_PER_SHOW_COUNT,
            episodesCount = EpisodeUtils.EPISODES_PER_SHOW_COUNT,
            length = SeasonUtils.getDomainSeasons(show = index).sumOf { it.episodes.sumOf { episode -> episode.length } }
        )
    }

    /**
     * Returns genres for index.
     *
     * @param index index
     * @return genres for index
     */
    private fun getGenres(index: Int): MutableList<Genre> {
        val genres = mutableListOf<Genre>()
        genres.add(GenreUtils.getGenre(index = index))
        if (index == 3) {
            genres.add(GenreUtils.getGenre(index = 4))
        }
        return genres
    }

    /**
     * Returns statistics for shows.
     *
     * @return statistics for shows
     */
    fun getStatistics(): ShowStatistics {
        return ShowStatistics(count = SHOWS_COUNT, seasonsCount = SeasonUtils.SEASONS_COUNT, episodesCount = EpisodeUtils.EPISODES_COUNT, length = Time(length = 1998).toString())
    }

    /**
     * Returns count of shows.
     *
     * @param entityManager entity manager
     * @return count of shows
     */
    fun getShowsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(s.id) FROM Show s", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns show.
     *
     * @param id ID
     * @return show
     */
    fun newDomainShow(id: Int?): com.github.vhromada.catalog.domain.Show {
        return com.github.vhromada.catalog.domain.Show(
            id = id,
            uuid = TestConstants.UUID,
            czechName = "",
            normalizedCzechName = "",
            originalName = "",
            normalizedOriginalName = "",
            csfd = null,
            imdbCode = null,
            wikiEn = null,
            wikiCz = null,
            picture = id,
            note = null,
            genres = mutableListOf(GenreUtils.getDomainGenre(index = 1)),
            seasons = mutableListOf()
        ).updated()
    }

    /**
     * Returns show.
     *
     * @return show
     */
    fun newShow(): Show {
        return Show(
            uuid = TestConstants.UUID,
            czechName = "",
            originalName = "",
            csfd = null,
            imdbCode = null,
            wikiEn = null,
            wikiCz = null,
            picture = PictureUtils.getPicture(index = 1).uuid,
            note = null,
            genres = listOf(GenreUtils.getGenre(index = 1)),
            seasonsCount = 0,
            episodesCount = 0,
            length = 0
        ).updated()
    }

    /**
     * Returns request for changing show.
     *
     * @return request for changing show
     */
    fun newRequest(): ChangeShowRequest {
        return ChangeShowRequest(
            czechName = "czName",
            originalName = "origName",
            csfd = "Csfd",
            imdbCode = 1000,
            wikiEn = "enWiki",
            wikiCz = "czWiki",
            picture = PictureUtils.getPicture(index = 1).uuid,
            note = "Note",
            genres = listOf(GenreUtils.getGenre(index = 1).uuid)
        )
    }

    /**
     * Asserts list of shows deep equals.
     *
     * @param expected expected list of shows
     * @param actual   actual list of shows
     */
    fun assertDomainShowsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Show>, actual: List<com.github.vhromada.catalog.domain.Show>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertShowDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts show deep equals.
     *
     * @param expected     expected show
     * @param actual       actual show
     * @param checkSeasons true if seasons should be checked
     * @param ignoreUuid   true if UUID should be ignored
     */
    fun assertShowDeepEquals(expected: com.github.vhromada.catalog.domain.Show?, actual: com.github.vhromada.catalog.domain.Show?, checkSeasons: Boolean = true, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.czechName).isEqualTo(expected.czechName)
                it.assertThat(actual.normalizedCzechName).isEqualTo(expected.normalizedCzechName)
                it.assertThat(actual.originalName).isEqualTo(expected.originalName)
                it.assertThat(actual.normalizedOriginalName).isEqualTo(expected.normalizedOriginalName)
                it.assertThat(actual.csfd).isEqualTo(expected.csfd)
                it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
                it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
                it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
                it.assertThat(actual.picture).isEqualTo(expected.picture)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            GenreUtils.assertDomainGenresDeepEquals(expected = expected.genres, actual = actual.genres)
            if (checkSeasons) {
                SeasonUtils.assertDomainSeasonsDeepEquals(expected = expected.seasons, actual = actual.seasons, ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts list of shows deep equals.
     *
     * @param expected expected list of shows
     * @param actual   actual list of shows
     */
    fun assertShowsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Show>, actual: List<Show>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertShowDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts show deep equals.
     *
     * @param expected   expected show
     * @param actual     actual show
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertShowDeepEquals(expected: com.github.vhromada.catalog.domain.Show, actual: Show, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.csfd).isEqualTo(expected.csfd)
            it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.picture).isNull()
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.seasonsCount).isEqualTo(expected.seasons.size)
            it.assertThat(actual.episodesCount).isEqualTo(expected.seasons.sumOf { season -> season.episodes.size })
            it.assertThat(actual.length).isEqualTo(expected.seasons.sumOf { season -> season.episodes.sumOf { episode -> episode.length } })
        }
        GenreUtils.assertGenresDeepEquals(expected = expected.genres, actual = actual.genres)
    }

    /**
     * Asserts list of shows deep equals.
     *
     * @param expected expected list of shows
     * @param actual   actual list of shows
     */
    fun assertShowListDeepEquals(expected: List<Show>, actual: List<Show>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertShowDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts show deep equals.
     *
     * @param expected   expected show
     * @param actual     actual show
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertShowDeepEquals(expected: Show, actual: Show, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.csfd).isEqualTo(expected.csfd)
            it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.picture).isEqualTo(expected.picture)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.seasonsCount).isEqualTo(expected.seasonsCount)
            it.assertThat(actual.episodesCount).isEqualTo(expected.episodesCount)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
        GenreUtils.assertGenreListDeepEquals(expected = expected.genres, actual = actual.genres)
    }

    /**
     * Asserts request and show deep equals.
     *
     * @param expected expected request for changing show
     * @param actual   actual show
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeShowRequest, actual: com.github.vhromada.catalog.domain.Show, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.normalizedCzechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.normalizedOriginalName).isEqualTo(expected.originalName)
            it.assertThat(actual.csfd).isEqualTo(expected.csfd)
            it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.picture).isNull()
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.seasons).isEmpty()
            it.assertThat(actual.genres).isEmpty()
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
    fun assertFilterDeepEquals(expected: MultipleNameFilter, actual: ShowFilter) {
        assertSoftly {
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
        }
    }

    /**
     * Asserts statistics for shows deep equals.
     *
     * @param expected expected statistics for shows
     * @param actual   actual statistics for shows
     */
    fun assertStatisticsDeepEquals(expected: ShowStatistics, actual: ShowStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.seasonsCount).isEqualTo(expected.seasonsCount)
            it.assertThat(actual.episodesCount).isEqualTo(expected.episodesCount)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

}
