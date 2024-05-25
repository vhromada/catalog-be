package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.Role
import com.github.vhromada.catalog.entity.io.ChangeRolesRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * A class represents utility class for roles.
 *
 * @author Vladimir Hromada
 */
object RoleUtils {

    /**
     * Count of roles
     */
    const val ROLES_COUNT = 2

    /**
     * Returns roles.
     *
     * @return roles
     */
    fun getRoles(): List<Role> {
        val roles = mutableListOf<Role>()
        for (i in 1..ROLES_COUNT) {
            roles.add(getDomainRole(index = i))
        }

        return roles
    }

    /**
     * Returns role for index.
     *
     * @param index index
     * @return role for index
     */
    fun getDomainRole(index: Int): Role {
        val name = when (index) {
            1 -> "ADMIN"
            2 -> "USER"
            else -> throw IllegalArgumentException("Bad index")
        }

        return Role(
            id = index,
            name = "ROLE_$name"
        )
    }

    /**
     * Returns role for index.
     *
     * @param index index
     * @return role for index
     */
    fun getRole(index: Int): String {
        return when (index) {
            1 -> "ROLE_ADMIN"
            2 -> "ROLE_USER"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns count of roles.
     *
     * @param entityManager entity manager
     * @return count of roles
     */
    fun getRolesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(r.id) FROM Role r", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns request for changing roles.
     *
     * @return request for changing roles
     */
    fun newRequest(): ChangeRolesRequest {
        return ChangeRolesRequest(
            roles = listOf(getDomainRole(index = 2).name)
        )
    }

    /**
     * Asserts roles deep equals.
     *
     * @param expected expected roles
     * @param actual   actual roles
     */
    fun assertDomainRolesDeepEquals(expected: List<Role>, actual: List<Role>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertRoleDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts role deep equals.
     *
     * @param expected expected role
     * @param actual   actual role
     */
    fun assertRoleDeepEquals(expected: Role, actual: Role) {
        assertSoftly {
            it.assertThat(actual.id).isEqualTo(expected.id)
            it.assertThat(actual.name).isEqualTo(expected.name)
        }
    }

    /**
     * Asserts roles deep equals.
     *
     * @param expected expected roles
     * @param actual   actual roles
     */
    fun assertRolesDeepEquals(expected: List<Role>, actual: List<String>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertThat(actual[i]).isEqualTo(expected[i].name)
            }
        }
    }

}
