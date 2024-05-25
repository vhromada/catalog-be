package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.io.MediaStatistics
import com.github.vhromada.catalog.entity.Medium
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * A class represents utility class for media.
 *
 * @author Vladimir Hromada
 */
object MediumUtils {

    /**
     * Count of media
     */
    const val MEDIA_COUNT = 4

    /**
     * Returns medium for index.
     *
     * @param index index
     * @return medium for index
     */
    fun getDomainMedium(index: Int): com.github.vhromada.catalog.domain.Medium {
        val lengthMultiplier = 100

        return com.github.vhromada.catalog.domain.Medium(
            id = index,
            number = if (index < 4) 1 else 2,
            length = index * lengthMultiplier
        ).fillAudit(AuditUtils.getAudit())
    }

    /**
     * Returns medium for index.
     *
     * @param index medium index
     * @return medium for index
     */
    fun getMedium(index: Int): Medium {
        val lengthMultiplier = 100

        return Medium(
            number = if (index < 4) 1 else 2,
            length = index * lengthMultiplier
        )
    }

    /**
     * Returns statistics for media.
     *
     * @return statistics for media
     */
    fun getStatistics(): MediaStatistics {
        return MediaStatistics(count = MEDIA_COUNT.toLong(), length = 1000L)
    }

    /**
     * Returns count of media.
     *
     * @param entityManager entity manager
     * @return count of media
     */
    fun getMediaCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(m.id) FROM Medium m", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns medium.
     *
     * @param id ID
     * @return medium
     */
    fun newDomainMedium(id: Int?): com.github.vhromada.catalog.domain.Medium {
        return com.github.vhromada.catalog.domain.Medium(
            id = id,
            number = 1,
            length = 10
        )
    }

    /**
     * Returns medium.
     *
     * @return medium
     */
    fun newMedium(): Medium {
        return Medium(
            number = 1,
            length = 10
        )
    }

    /**
     * Asserts list of medium deep equals.
     *
     * @param expected expected list of medium
     * @param actual   actual list of medium
     */
    fun assertDomainMediumDeepEquals(expected: List<com.github.vhromada.catalog.domain.Medium>, actual: List<com.github.vhromada.catalog.domain.Medium>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMediumDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts medium deep equals.
     *
     * @param expected expected medium
     * @param actual   actual medium
     */
    private fun assertMediumDeepEquals(expected: com.github.vhromada.catalog.domain.Medium?, actual: com.github.vhromada.catalog.domain.Medium?) {
        if (expected == null) {
            assertThat(actual).isNull()
        } else {
            assertThat(actual).isNotNull
            assertSoftly {
                it.assertThat(actual!!.id).isEqualTo(expected.id)
                it.assertThat(actual.number).isEqualTo(expected.number)
                it.assertThat(actual.length).isEqualTo(expected.length)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
        }
    }

    /**
     * Asserts list of medium deep equals.
     *
     * @param expected expected list of medium
     * @param actual   actual list of medium
     */
    fun assertMediumDeepEquals(expected: List<com.github.vhromada.catalog.domain.Medium>, actual: List<Medium>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMediumDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts medium deep equals.
     *
     * @param expected expected medium
     * @param actual   actual medium
     */
    private fun assertMediumDeepEquals(expected: com.github.vhromada.catalog.domain.Medium, actual: Medium) {
        assertSoftly {
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

    /**
     * Asserts list of medium deep equals.
     *
     * @param expected expected list of medium
     * @param actual   actual list of medium
     */
    fun assertMediumListDeepEquals(expected: List<Medium>, actual: List<Medium>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMediumDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts medium deep equals.
     *
     * @param expected expected medium
     * @param actual   actual medium
     */
    private fun assertMediumDeepEquals(expected: Medium, actual: Medium) {
        assertSoftly {
            it.assertThat(actual.number).isEqualTo(expected.number)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

    /**
     * Asserts statistics for media deep equals.
     *
     * @param expected expected statistics for media
     * @param actual   actual statistics for media
     */
    fun assertStatisticsDeepEquals(expected: MediaStatistics, actual: MediaStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

}
