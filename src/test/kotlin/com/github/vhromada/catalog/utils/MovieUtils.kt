package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.domain.filter.MovieFilter
import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.Medium
import com.github.vhromada.catalog.entity.Movie
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates movie fields.
 *
 * @return updated movie
 */
fun com.github.vhromada.catalog.domain.Movie.updated(): com.github.vhromada.catalog.domain.Movie {
    czechName = "czName"
    normalizedCzechName = "czName"
    originalName = "origName"
    normalizedOriginalName = "origName"
    year = MovieUtils.START_YEAR
    languages.clear()
    languages.add("EN")
    subtitles.clear()
    subtitles.add("CZ")
    csfd = "Csfd"
    imdbCode = 1000
    wikiEn = "enWiki"
    wikiCz = "czWiki"
    note = "Note"
    return this
}

/**
 * Updates movie fields.
 *
 * @return updated movie
 */
fun Movie.updated(): Movie {
    return copy(
        czechName = "czName",
        originalName = "origName",
        year = MovieUtils.START_YEAR,
        languages = listOf("EN"),
        subtitles = listOf("CZ"),
        csfd = "Csfd",
        imdbCode = 1000,
        wikiEn = "enWiki",
        wikiCz = "czWiki",
        note = "Note"
    )
}

/**
 * A class represents utility class for movies.
 *
 * @author Vladimir Hromada
 */
object MovieUtils {

    /**
     * Count of movies
     */
    const val MOVIES_COUNT = 3

    /**
     * Start year
     */
    const val START_YEAR = 2000

    /**
     * Returns movies.
     *
     * @return movies
     */
    fun getDomainMovies(): List<com.github.vhromada.catalog.domain.Movie> {
        val movies = mutableListOf<com.github.vhromada.catalog.domain.Movie>()
        for (i in 1..MOVIES_COUNT) {
            movies.add(getDomainMovie(index = i))
        }

        return movies
    }

    /**
     * Returns movies.
     *
     * @return movies
     */
    fun getMovies(): List<Movie> {
        val movies = mutableListOf<Movie>()
        for (i in 1..MOVIES_COUNT) {
            movies.add(getMovie(index = i))
        }

        return movies
    }

    /**
     * Returns movie for index.
     *
     * @param index index
     * @return movie for index
     */
    fun getDomainMovie(index: Int): com.github.vhromada.catalog.domain.Movie {
        val czechName = "Movie $index czech name"
        val originalName = "Movie $index original name"
        return com.github.vhromada.catalog.domain.Movie(
            id = index,
            uuid = getUuid(index = index),
            czechName = czechName,
            normalizedCzechName = czechName,
            originalName = originalName,
            normalizedOriginalName = originalName,
            year = START_YEAR + index,
            languages = getLanguages(index = index),
            subtitles = getSubtitles(index = index),
            media = getDomainMedia(index = index),
            csfd = if (index != 1) "Movie $index CSFD" else null,
            imdbCode = if (index == 1) index else null,
            wikiEn = if (index != 2) "Movie $index English Wikipedia" else null,
            wikiCz = if (index != 3) "Movie $index Czech Wikipedia" else null,
            picture = index,
            note = if (index != 2) "Movie $index note" else null,
            genres = getDomainGenres(index = index)
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
            1 -> "8fe0ecc8-517a-46c0-a1e6-920df8ccfa62"
            2 -> "8ce484b6-8365-4c93-94ad-db1057c32737"
            3 -> "966eae88-04ff-4f1c-b7b7-9808e9058bd8"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns languages for index.
     *
     * @param index index
     * @return languages for index
     */
    private fun getLanguages(index: Int): MutableList<String> {
        val languages = mutableListOf<String>()
        when (index) {
            1 -> {
                languages.add("CZ")
            }

            2 -> {
                languages.add("JP")
            }

            3 -> {
                languages.add("FR")
                languages.add("SK")
            }

            else -> throw IllegalArgumentException("Bad index")
        }
        return languages
    }

    /**
     * Returns subtitles for index.
     *
     * @param index index
     * @return subtitles for index
     */
    private fun getSubtitles(index: Int): MutableList<String> {
        val subtitles = mutableListOf<String>()
        when (index) {
            1 -> {
                subtitles.add("EN")
            }

            2 -> {
            }

            3 -> {
                subtitles.add("CZ")
                subtitles.add("EN")
            }

            else -> throw IllegalArgumentException("Bad index")
        }
        return subtitles
    }

    /**
     * Returns media for index.
     *
     * @param index index
     * @return media for index
     */
    private fun getDomainMedia(index: Int): MutableList<com.github.vhromada.catalog.domain.Medium> {
        val media = mutableListOf<com.github.vhromada.catalog.domain.Medium>()
        media.add(MediumUtils.getDomainMedium(index = index))
        if (index == 3) {
            media.add(MediumUtils.getDomainMedium(index = 4))
        }
        return media
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
     * Returns movie.
     *
     * @param entityManager entity manager
     * @param id            movie ID
     * @return movie
     */
    fun getDomainMovie(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Movie? {
        return entityManager.find(com.github.vhromada.catalog.domain.Movie::class.java, id)
    }

    /**
     * Returns movie for index.
     *
     * @param index index
     * @return movie for index
     */
    fun getMovie(index: Int): Movie {
        return Movie(
            uuid = getUuid(index = index),
            czechName = "Movie $index czech name",
            originalName = "Movie $index original name",
            year = START_YEAR + index,
            languages = getLanguages(index = index),
            subtitles = getSubtitles(index = index),
            media = getMedia(index = index),
            csfd = if (index != 1) "Movie $index CSFD" else null,
            imdbCode = if (index == 1) index else null,
            wikiEn = if (index != 2) "Movie $index English Wikipedia" else null,
            wikiCz = if (index != 3) "Movie $index Czech Wikipedia" else null,
            picture = PictureUtils.getPicture(index = index).uuid,
            note = if (index != 2) "Movie $index note" else null,
            genres = getGenres(index = index)
        )
    }

    /**
     * Returns media for index.
     *
     * @param index index
     * @return media for index
     */
    private fun getMedia(index: Int): MutableList<Medium> {
        val media = mutableListOf<Medium>()
        media.add(MediumUtils.getMedium(index = index))
        if (index == 3) {
            media.add(MediumUtils.getMedium(index = 4))
        }
        return media
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
     * Returns statistics for movies.
     *
     * @return statistics for movies
     */
    fun getStatistics(): MovieStatistics {
        return MovieStatistics(count = MOVIES_COUNT, mediaCount = MediumUtils.MEDIA_COUNT, length = Time(length = 1000).toString())
    }

    /**
     * Returns count of movies.
     *
     * @param entityManager entity manager
     * @return count of movies
     */
    fun getMoviesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(m.id) FROM Movie m", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns movie.
     *
     * @param id ID
     * @return movie
     */
    fun newDomainMovie(id: Int?): com.github.vhromada.catalog.domain.Movie {
        return com.github.vhromada.catalog.domain.Movie(
            id = id,
            uuid = TestConstants.UUID,
            czechName = "",
            normalizedCzechName = "",
            originalName = "",
            normalizedOriginalName = "",
            year = 0,
            languages = mutableListOf("JP"),
            subtitles = mutableListOf(),
            media = mutableListOf(MediumUtils.newDomainMedium(id = id)),
            csfd = null,
            imdbCode = null,
            wikiEn = null,
            wikiCz = null,
            picture = id,
            note = null,
            genres = mutableListOf(GenreUtils.getDomainGenre(index = 1))
        ).updated()
    }

    /**
     * Returns movie.
     *
     * @return movie
     */
    fun newMovie(): Movie {
        return Movie(
            uuid = TestConstants.UUID,
            czechName = "",
            originalName = "",
            year = 0,
            languages = mutableListOf("JP"),
            subtitles = emptyList(),
            media = listOf(MediumUtils.newMedium()),
            csfd = null,
            imdbCode = null,
            wikiEn = null,
            wikiCz = null,
            picture = PictureUtils.getPicture(index = 1).uuid,
            note = null,
            genres = listOf(GenreUtils.getGenre(index = 1)),
        ).updated()
    }

    /**
     * Returns request for changing movie.
     *
     * @return request for changing movie
     */
    fun newRequest(): ChangeMovieRequest {
        return ChangeMovieRequest(
            czechName = "czName",
            originalName = "origName",
            year = START_YEAR,
            languages = listOf("EN"),
            subtitles = listOf("CZ"),
            media = listOf(10),
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
     * Asserts list of movies deep equals.
     *
     * @param expected expected list of movies
     * @param actual   actual list of movies
     */
    fun assertDomainMoviesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Movie>, actual: List<com.github.vhromada.catalog.domain.Movie>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMovieDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts movie deep equals.
     *
     * @param expected   expected movie
     * @param actual     actual movie
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertMovieDeepEquals(expected: com.github.vhromada.catalog.domain.Movie?, actual: com.github.vhromada.catalog.domain.Movie?, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.year).isEqualTo(expected.year)
                it.assertThat(actual.languages)
                    .hasSameSizeAs(expected.languages)
                    .hasSameElementsAs(expected.languages)
                it.assertThat(actual.subtitles)
                    .hasSameSizeAs(expected.subtitles)
                    .hasSameElementsAs(expected.subtitles)
                it.assertThat(actual.csfd).isEqualTo(expected.csfd)
                it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
                it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
                it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
                it.assertThat(actual.picture).isEqualTo(expected.picture)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            MediumUtils.assertDomainMediumDeepEquals(expected = expected.media, actual = actual.media)
            GenreUtils.assertDomainGenresDeepEquals(expected = expected.genres, actual = actual.genres)
        }
    }

    /**
     * Asserts list of movies deep equals.
     *
     * @param expected expected list of movies
     * @param actual   actual list of movies
     */
    fun assertMoviesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Movie>, actual: List<Movie>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMovieDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts movie deep equals.
     *
     * @param expected   expected movie
     * @param actual     actual movie
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertMovieDeepEquals(expected: com.github.vhromada.catalog.domain.Movie, actual: Movie, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.year).isEqualTo(expected.year)
            it.assertThat(actual.languages)
                .hasSameSizeAs(expected.languages)
                .hasSameElementsAs(expected.languages)
            it.assertThat(actual.subtitles)
                .hasSameSizeAs(expected.subtitles)
                .hasSameElementsAs(expected.subtitles)
            it.assertThat(actual.csfd).isEqualTo(expected.csfd)
            it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.picture).isNull()
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
        MediumUtils.assertMediumDeepEquals(expected = expected.media, actual = actual.media)
        GenreUtils.assertGenresDeepEquals(expected = expected.genres, actual = actual.genres)
    }

    /**
     * Asserts list of movies deep equals.
     *
     * @param expected expected list of movies
     * @param actual   actual list of movies
     */
    fun assertMovieListDeepEquals(expected: List<Movie>, actual: List<Movie>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMovieDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts movie deep equals.
     *
     * @param expected   expected movie
     * @param actual     actual movie
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertMovieDeepEquals(expected: Movie, actual: Movie, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.year).isEqualTo(expected.year)
            it.assertThat(actual.languages)
                .hasSameSizeAs(expected.languages)
                .hasSameElementsAs(expected.languages)
            it.assertThat(actual.subtitles)
                .hasSameSizeAs(expected.subtitles)
                .hasSameElementsAs(expected.subtitles)
            it.assertThat(actual.csfd).isEqualTo(expected.csfd)
            it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.picture).isEqualTo(expected.picture)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
        MediumUtils.assertMediumListDeepEquals(expected = expected.media, actual = actual.media)
        GenreUtils.assertGenreListDeepEquals(expected = expected.genres, actual = actual.genres)
    }

    /**
     * Asserts request and movie deep equals.
     *
     * @param expected expected request for changing movie
     * @param actual   actual movie
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeMovieRequest, actual: com.github.vhromada.catalog.domain.Movie, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.normalizedCzechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
            it.assertThat(actual.normalizedOriginalName).isEqualTo(expected.originalName)
            it.assertThat(actual.year).isEqualTo(expected.year)
            it.assertThat(actual.languages)
                .hasSameSizeAs(expected.languages)
                .hasSameElementsAs(expected.languages)
            it.assertThat(actual.subtitles)
                .hasSameSizeAs(expected.subtitles)
                .hasSameElementsAs(expected.subtitles)
            it.assertThat(actual.media).isEmpty()
            it.assertThat(actual.csfd).isEqualTo(expected.csfd)
            it.assertThat(actual.imdbCode).isEqualTo(expected.imdbCode)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.picture).isNull()
            it.assertThat(actual.note).isEqualTo(expected.note)
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
    fun assertFilterDeepEquals(expected: MultipleNameFilter, actual: MovieFilter) {
        assertSoftly {
            it.assertThat(actual.czechName).isEqualTo(expected.czechName)
            it.assertThat(actual.originalName).isEqualTo(expected.originalName)
        }
    }

    /**
     * Asserts statistics for games deep equals.
     *
     * @param expected expected statistics for games
     * @param actual   actual statistics for games
     */
    fun assertStatisticsDeepEquals(expected: MovieStatistics, actual: MovieStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

}
