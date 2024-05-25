package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates genre fields.
 *
 * @return updated genre
 */
fun com.github.vhromada.catalog.domain.Genre.updated(): com.github.vhromada.catalog.domain.Genre {
    name = "Name"
    normalizedName = "Name"
    return this
}

/**
 * Updates genre fields.
 *
 * @return updated genre
 */
fun Genre.updated(): Genre {
    return copy(name = "Name")
}

/**
 * A class represents utility class for genres.
 *
 * @author Vladimir Hromada
 */
object GenreUtils {

    /**
     * Count of genres
     */
    const val GENRES_COUNT = 4

    /**
     * Returns genres.
     *
     * @return genres
     */
    fun getDomainGenres(): List<com.github.vhromada.catalog.domain.Genre> {
        val genres = mutableListOf<com.github.vhromada.catalog.domain.Genre>()
        for (i in 1..GENRES_COUNT) {
            genres.add(getDomainGenre(index = i))
        }

        return genres
    }

    /**
     * Returns genres.
     *
     * @return genres
     */
    fun getGenres(): List<Genre> {
        val genres = mutableListOf<Genre>()
        for (i in 1..GENRES_COUNT) {
            genres.add(getGenre(index = i))
        }

        return genres
    }

    /**
     * Returns genre for index.
     *
     * @param index index
     * @return genre for index
     */
    fun getDomainGenre(index: Int): com.github.vhromada.catalog.domain.Genre {
        val name = "Genre $index name"
        return com.github.vhromada.catalog.domain.Genre(
            id = index,
            uuid = getUuid(index = index),
            name = name,
            normalizedName = name
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
            1 -> "859dd213-d9f9-4223-a5a8-77abf5c7f4ca"
            2 -> "34797673-6f5f-455d-a348-7b76786911b6"
            3 -> "7833d261-77ce-427a-86f7-a59b2ec604f0"
            4 -> "daad5c2a-941a-449a-93ef-0724d4283b43"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns genre.
     *
     * @param entityManager entity manager
     * @param id            genre ID
     * @return genre
     */
    fun getDomainGenre(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Genre? {
        return entityManager.find(com.github.vhromada.catalog.domain.Genre::class.java, id)
    }

    /**
     * Returns genre for index.
     *
     * @param index index
     * @return genre for index
     */
    fun getGenre(index: Int): Genre {
        return Genre(
            uuid = getUuid(index = index),
            name = "Genre $index name"
        )
    }

    /**
     * Returns count of genres.
     *
     * @param entityManager entity manager
     * @return count of genres
     */
    fun getGenresCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(g.id) FROM Genre g", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns genre.
     *
     * @param id ID
     * @return genre
     */
    fun newDomainGenre(id: Int?): com.github.vhromada.catalog.domain.Genre {
        return com.github.vhromada.catalog.domain.Genre(
            id = id,
            uuid = TestConstants.UUID,
            name = "",
            normalizedName = ""
        ).updated()
    }

    /**
     * Returns genre.
     *
     * @return genre
     */
    fun newGenre(): Genre {
        return Genre(
            uuid = TestConstants.UUID,
            name = ""
        ).updated()
    }

    /**
     * Returns request for changing genre.
     *
     * @return request for changing genre
     */
    fun newRequest(): ChangeGenreRequest {
        return ChangeGenreRequest(name = "Name")
    }

    /**
     * Asserts list of genres deep equals.
     *
     * @param expected expected list of genres
     * @param actual   actual list of genres
     */
    fun assertDomainGenresDeepEquals(expected: List<com.github.vhromada.catalog.domain.Genre>, actual: List<com.github.vhromada.catalog.domain.Genre>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertGenreDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts genre deep equals.
     *
     * @param expected   expected genre
     * @param actual     actual genre
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertGenreDeepEquals(expected: com.github.vhromada.catalog.domain.Genre?, actual: com.github.vhromada.catalog.domain.Genre?, ignoreUuid: Boolean = false) {
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
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
        }
    }

    /**
     * Asserts list of genres deep equals.
     *
     * @param expected expected list of genres
     * @param actual   actual list of genres
     */
    fun assertGenresDeepEquals(expected: List<com.github.vhromada.catalog.domain.Genre>, actual: List<Genre>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertGenreDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts genre deep equals.
     *
     * @param expected   expected genre
     * @param actual     actual genre
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertGenreDeepEquals(expected: com.github.vhromada.catalog.domain.Genre, actual: Genre, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
        }
    }

    /**
     * Asserts list of genres deep equals.
     *
     * @param expected expected list of genres
     * @param actual   actual list of genres
     */
    fun assertGenreListDeepEquals(expected: List<Genre>, actual: List<Genre>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertGenreDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts genre deep equals.
     *
     * @param expected   expected genre
     * @param actual     actual genre
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertGenreDeepEquals(expected: Genre, actual: Genre, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
        }
    }

    /**
     * Asserts request and genre deep equals.
     *
     * @param expected expected request for changing genre
     * @param actual   actual genre
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeGenreRequest, actual: com.github.vhromada.catalog.domain.Genre, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.normalizedName).isEqualTo(expected.name)
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
    fun assertFilterDeepEquals(expected: NameFilter, actual: GenreFilter) {
        assertThat(actual.name).isEqualTo(expected.name)
    }

}
