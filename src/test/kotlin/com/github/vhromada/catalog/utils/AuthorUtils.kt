package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.filter.AuthorFilter
import com.github.vhromada.catalog.entity.Author
import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates author fields.
 *
 * @return updated author
 */
fun com.github.vhromada.catalog.domain.Author.updated(): com.github.vhromada.catalog.domain.Author {
    firstName = "firstName"
    normalizedFirstName = "firstName"
    middleName = "middleName"
    normalizedMiddleName = "middleName"
    lastName = "lastName"
    normalizedLastName = "lastName"
    return this
}

/**
 * Updates author fields.
 *
 * @return updated author
 */
fun Author.updated(): Author {
    return copy(
        firstName = "firstName",
        middleName = "middleName",
        lastName = "lastName"
    )
}

/**
 * A class represents utility class for authors.
 *
 * @author Vladimir Hromada
 */
object AuthorUtils {

    /**
     * Count of authors
     */
    const val AUTHORS_COUNT = 3

    /**
     * Returns authors.
     *
     * @return authors
     */
    fun getDomainAuthors(): List<com.github.vhromada.catalog.domain.Author> {
        val authors = mutableListOf<com.github.vhromada.catalog.domain.Author>()
        for (i in 1..AUTHORS_COUNT) {
            authors.add(getDomainAuthor(index = i))
        }

        return authors
    }

    /**
     * Returns authors.
     *
     * @return authors
     */
    fun getAuthors(): List<Author> {
        val authors = mutableListOf<Author>()
        for (i in 1..AUTHORS_COUNT) {
            authors.add(getAuthor(index = i))
        }

        return authors
    }

    /**
     * Returns author for index.
     *
     * @param index index
     * @return author for index
     */
    fun getDomainAuthor(index: Int): com.github.vhromada.catalog.domain.Author {
        val firstName = "Author $index first name"
        val middleName = if (index != 2) "Author $index middle name" else null
        val lastName = "Author $index last name"
        return com.github.vhromada.catalog.domain.Author(
            id = index,
            uuid = getUuid(index = index),
            firstName = firstName,
            normalizedFirstName = firstName,
            middleName = middleName,
            normalizedMiddleName = middleName,
            lastName = lastName,
            normalizedLastName = lastName
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
            1 -> "34358075-967b-45bd-a3f2-b1f7a9515c6d"
            2 -> "7d920a83-a80b-477a-bb3a-d7fea1352528"
            3 -> "089f33c2-8906-45c4-82b6-c9c8f492a3fb"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns author.
     *
     * @param entityManager entity manager
     * @param id            author ID
     * @return author
     */
    fun getDomainAuthor(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Author? {
        return entityManager.find(com.github.vhromada.catalog.domain.Author::class.java, id)
    }

    /**
     * Returns author for index.
     *
     * @param index index
     * @return author for index
     */
    fun getAuthor(index: Int): Author {
        return Author(
            uuid = getUuid(index = index),
            firstName = "Author $index first name",
            middleName = if (index != 2) "Author $index middle name" else null,
            lastName = "Author $index last name"
        )
    }

    /**
     * Returns count of authors.
     *
     * @param entityManager entity manager
     * @return count of authors
     */
    fun getAuthorsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(a.id) FROM Author a", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns author.
     *
     * @param id ID
     * @return author
     */
    fun newDomainAuthor(id: Int?): com.github.vhromada.catalog.domain.Author {
        return com.github.vhromada.catalog.domain.Author(
            id = id,
            uuid = TestConstants.UUID,
            firstName = "",
            normalizedFirstName = "",
            middleName = null,
            normalizedMiddleName = null,
            lastName = "",
            normalizedLastName = ""
        ).updated()
    }

    /**
     * Returns author.
     *
     * @return author
     */
    fun newAuthor(): Author {
        return Author(
            uuid = TestConstants.UUID,
            firstName = "",
            middleName = null,
            lastName = ""
        ).updated()
    }

    /**
     * Returns request for changing author.
     *
     * @return request for changing author
     */
    fun newRequest(): ChangeAuthorRequest {
        return ChangeAuthorRequest(
            firstName = "firstName",
            middleName = "middleName",
            lastName = "lastName"
        )
    }

    /**
     * Returns filter for authors.
     *
     * @return filter for authors.
     */
    fun newFilter(): com.github.vhromada.catalog.entity.filter.AuthorFilter {
        return com.github.vhromada.catalog.entity.filter.AuthorFilter(
            firstName = "firstName",
            middleName = "middleName",
            lastName = "lastName"
        )
    }

    /**
     * Asserts list of authors deep equals.
     *
     * @param expected expected list of authors
     * @param actual   actual list of authors
     */
    fun assertDomainAuthorsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Author>, actual: List<com.github.vhromada.catalog.domain.Author>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertAuthorDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts author deep equals.
     *
     * @param expected   expected author
     * @param actual     actual author
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertAuthorDeepEquals(expected: com.github.vhromada.catalog.domain.Author?, actual: com.github.vhromada.catalog.domain.Author?, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.firstName).isEqualTo(expected.firstName)
                it.assertThat(actual.normalizedFirstName).isEqualTo(expected.normalizedFirstName)
                it.assertThat(actual.middleName).isEqualTo(expected.middleName)
                it.assertThat(actual.normalizedMiddleName).isEqualTo(expected.normalizedMiddleName)
                it.assertThat(actual.lastName).isEqualTo(expected.lastName)
                it.assertThat(actual.normalizedLastName).isEqualTo(expected.normalizedLastName)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
        }
    }

    /**
     * Asserts list of authors deep equals.
     *
     * @param expected expected list of authors
     * @param actual   actual list of authors
     */
    fun assertAuthorsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Author>, actual: List<Author>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertAuthorDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts author deep equals.
     *
     * @param expected   expected author
     * @param actual     actual author
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertAuthorDeepEquals(expected: com.github.vhromada.catalog.domain.Author, actual: Author, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.firstName).isEqualTo(expected.firstName)
            it.assertThat(actual.middleName).isEqualTo(expected.middleName)
            it.assertThat(actual.lastName).isEqualTo(expected.lastName)
        }
    }

    /**
     * Asserts list of authors deep equals.
     *
     * @param expected expected list of authors
     * @param actual   actual list of authors
     */
    fun assertAuthorListDeepEquals(expected: List<Author>, actual: List<Author>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertAuthorDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts author deep equals.
     *
     * @param expected   expected author
     * @param actual     actual author
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertAuthorDeepEquals(expected: Author, actual: Author, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.firstName).isEqualTo(expected.firstName)
            it.assertThat(actual.middleName).isEqualTo(expected.middleName)
            it.assertThat(actual.lastName).isEqualTo(expected.lastName)
        }
    }

    /**
     * Asserts request and author deep equals.
     *
     * @param expected expected request for changing author
     * @param actual   actual author
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeAuthorRequest, actual: com.github.vhromada.catalog.domain.Author, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.firstName).isEqualTo(expected.firstName)
            it.assertThat(actual.normalizedFirstName).isEqualTo(expected.firstName)
            it.assertThat(actual.middleName).isEqualTo(expected.middleName)
            it.assertThat(actual.normalizedMiddleName).isEqualTo(expected.middleName)
            it.assertThat(actual.lastName).isEqualTo(expected.lastName)
            it.assertThat(actual.normalizedLastName).isEqualTo(expected.lastName)
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
    fun assertFilterDeepEquals(expected: com.github.vhromada.catalog.entity.filter.AuthorFilter, actual: AuthorFilter) {
        assertSoftly {
            it.assertThat(actual.firstName).isEqualTo(expected.firstName)
            it.assertThat(actual.middleName).isEqualTo(expected.middleName)
            it.assertThat(actual.lastName).isEqualTo(expected.lastName)
        }
    }

}
