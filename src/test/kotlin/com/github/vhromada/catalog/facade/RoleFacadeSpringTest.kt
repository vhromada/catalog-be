package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.RoleUtils
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [RoleFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class RoleFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [RoleFacade]
     */
    @Autowired
    private lateinit var facade: RoleFacade

    /**
     * Test method for [RoleFacade.getAll].
     */
    @Test
    fun getAll() {
        val result = facade.getAll()

        RoleUtils.assertRolesDeepEquals(expected = RoleUtils.getRoles(), actual = result)

        assertSoftly {
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        }
    }

}
