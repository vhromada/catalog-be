package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.RoleUtils
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [RoleRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class RoleRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [RoleRepository]
     */
    @Autowired
    private lateinit var repository: RoleRepository

    /**
     * Test method for get roles.
     */
    @Test
    fun getRoles() {
        val roles = repository.findAll()

        RoleUtils.assertDomainRolesDeepEquals(expected = RoleUtils.getRoles(), actual = roles)

        assertSoftly {
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        }
    }

    /**
     * Test method for get role.
     */
    @Test
    fun getRole() {
        for (i in 1..RoleUtils.ROLES_COUNT) {
            val role = repository.findById(i).orElse(null)

            RoleUtils.assertRoleDeepEquals(expected = RoleUtils.getDomainRole(index = i), actual = role)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        }
    }

    /**
     * Test method for find account by name.
     */
    @Test
    fun findByName() {
        for (i in 1..RoleUtils.ROLES_COUNT) {
            val role = RoleUtils.getDomainRole(index = i)

            val result = repository.findByName(name = role.name).orElse(null)

            RoleUtils.assertRoleDeepEquals(expected = role, actual = result)
        }

        assertThat(repository.findByName(name = "name")).isNotPresent

        assertSoftly {
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        }
    }

}
