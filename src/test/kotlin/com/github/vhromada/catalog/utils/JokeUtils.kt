package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.entity.Joke
import com.github.vhromada.catalog.entity.io.ChangeJokeRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates joke fields.
 *
 * @return updated joke
 */
fun com.github.vhromada.catalog.domain.Joke.updated(): com.github.vhromada.catalog.domain.Joke {
    content = "Content"
    return this
}

/**
 * Updates joke fields.
 *
 * @return updated joke
 */
fun Joke.updated(): Joke {
    return copy(content = "Content")
}

/**
 * A class represents utility class for jokes.
 *
 * @author Vladimir Hromada
 */
object JokeUtils {

    /**
     * Count of jokes
     */
    const val JOKES_COUNT = 3

    /**
     * Returns jokes.
     *
     * @return jokes
     */
    fun getDomainJokes(): List<com.github.vhromada.catalog.domain.Joke> {
        val jokes = mutableListOf<com.github.vhromada.catalog.domain.Joke>()
        for (i in 1..JOKES_COUNT) {
            jokes.add(getDomainJoke(index = i))
        }

        return jokes
    }

    /**
     * Returns jokes.
     *
     * @return jokes
     */
    fun getJokes(): List<Joke> {
        val jokes = mutableListOf<Joke>()
        for (i in 1..JOKES_COUNT) {
            jokes.add(getJoke(index = i))
        }

        return jokes
    }

    /**
     * Returns joke for index.
     *
     * @param index index
     * @return joke for index
     */
    fun getDomainJoke(index: Int): com.github.vhromada.catalog.domain.Joke {
        val content = "Joke $index content"
        return com.github.vhromada.catalog.domain.Joke(
            id = index,
            uuid = getUuid(index = index),
            content = content
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
            1 -> "27cbf5b8-6e4b-4446-acba-aab6604da4d7"
            2 -> "ae2e3dfe-f12a-4d18-a7e1-367a06f36dfe"
            3 -> "83f95208-50e2-40bc-a279-8645363fb388"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns joke.
     *
     * @param entityManager entity manager
     * @param id            joke ID
     * @return joke
     */
    fun getDomainJoke(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Joke? {
        return entityManager.find(com.github.vhromada.catalog.domain.Joke::class.java, id)
    }

    /**
     * Returns joke for index.
     *
     * @param index index
     * @return joke for index
     */
    fun getJoke(index: Int): Joke {
        return Joke(
            uuid = getUuid(index = index),
            content = "Joke $index content"
        )
    }

    /**
     * Returns count of jokes.
     *
     * @param entityManager entity manager
     * @return count of jokes
     */
    fun getJokesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(j.id) FROM Joke j", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns joke.
     *
     * @param id ID
     * @return joke
     */
    fun newDomainJoke(id: Int?): com.github.vhromada.catalog.domain.Joke {
        return com.github.vhromada.catalog.domain.Joke(
            id = id,
            uuid = TestConstants.UUID,
            content = ""
        ).updated()
    }

    /**
     * Returns joke.
     *
     * @return joke
     */
    fun newJoke(): Joke {
        return Joke(
            uuid = TestConstants.UUID,
            content = "",
        ).updated()
    }

    /**
     * Returns request for changing joke.
     *
     * @return request for changing joke
     */
    fun newRequest(): ChangeJokeRequest {
        return ChangeJokeRequest(
            content = "Content"
        )
    }

    /**
     * Asserts list of jokes deep equals.
     *
     * @param expected expected list of jokes
     * @param actual   actual list of jokes
     */
    fun assertDomainJokesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Joke>, actual: List<com.github.vhromada.catalog.domain.Joke>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertJokeDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts joke deep equals.
     *
     * @param expected   expected joke
     * @param actual     actual joke
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertJokeDeepEquals(expected: com.github.vhromada.catalog.domain.Joke?, actual: com.github.vhromada.catalog.domain.Joke?, ignoreUuid: Boolean = false) {
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
        }
    }

    /**
     * Asserts list of jokes deep equals.
     *
     * @param expected expected list of jokes
     * @param actual   actual list of jokes
     */
    fun assertJokesDeepEquals(expected: List<com.github.vhromada.catalog.domain.Joke>, actual: List<Joke>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertJokeDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts joke deep equals.
     *
     * @param expected   expected joke
     * @param actual     actual joke
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertJokeDeepEquals(expected: com.github.vhromada.catalog.domain.Joke, actual: Joke, ignoreUuid: Boolean = false) {
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
     * Asserts list of jokes deep equals.
     *
     * @param expected expected list of jokes
     * @param actual   actual list of jokes
     */
    fun assertJokeListDeepEquals(expected: List<Joke>, actual: List<Joke>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertJokeDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts joke deep equals.
     *
     * @param expected   expected joke
     * @param actual     actual joke
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertJokeDeepEquals(expected: Joke, actual: Joke, ignoreUuid: Boolean = false) {
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
     * Asserts request and joke deep equals.
     *
     * @param expected expected request for changing joke
     * @param actual   actual joke
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeJokeRequest, actual: com.github.vhromada.catalog.domain.Joke, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.content).isEqualTo(expected.content)
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

}
