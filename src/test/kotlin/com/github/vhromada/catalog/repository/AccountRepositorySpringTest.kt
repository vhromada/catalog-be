package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.AccountFilter
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.RoleUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [AccountRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class AccountRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [AccountRepository]
     */
    @Autowired
    private lateinit var repository: AccountRepository

    /**
     * Test method for get accounts.
     */
    @Test
    fun getAccounts() {
        val accounts = repository.findAll()

        AccountUtils.assertDomainAccountsDeepEquals(expected = AccountUtils.getDomainAccounts(), actual = accounts)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for get account.
     */
    @Test
    fun getAccount() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val account = repository.findById(i).orElse(null)

            AccountUtils.assertAccountDeepEquals(expected = AccountUtils.getDomainAccount(index = i), actual = account)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for add account.
     */
    @Test
    @DirtiesContext
    fun add() {
        val account = AccountUtils.newDomainAccount(id = null)
        val expectedAccount = AccountUtils.newDomainAccount(id = AccountUtils.ACCOUNTS_COUNT + 1)

        repository.saveAndFlush(account)

        assertThat(account.id).isEqualTo(AccountUtils.ACCOUNTS_COUNT + 1)
        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = AccountUtils.getDomainAccount(entityManager, id = AccountUtils.ACCOUNTS_COUNT + 1))

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT + 1)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for update account.
     */
    @Test
    @DirtiesContext
    fun update() {
        val account = AccountUtils.getDomainAccount(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedAccount = AccountUtils.getDomainAccount(index = 1)
            .updated()

        repository.saveAndFlush(account)

        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = AccountUtils.getDomainAccount(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for search accounts by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val account = AccountUtils.getDomainAccount(index = i)
            val filter = AccountFilter(uuid = account.uuid!!, username = account.username!!)

            val result = repository.findAll(filter.toSpecification())

            AccountUtils.assertDomainAccountsDeepEquals(expected = listOf(account), actual = result.toList())
        }

        assertThat(repository.findAll(AccountFilter(username = AccountUtils.USERNAME).toSpecification())).isEmpty()

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for find account by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val account = AccountUtils.getDomainAccount(index = i)

            val result = repository.findByUuid(uuid = account.uuid!!).orElse(null)

            AccountUtils.assertAccountDeepEquals(expected = account, actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for find account by username.
     */
    @Test
    fun findByUsername() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val account = AccountUtils.getDomainAccount(index = i)

            val result = repository.findByUsername(username = account.username!!).orElse(null)

            AccountUtils.assertAccountDeepEquals(expected = account, actual = result)
        }

        assertThat(repository.findByUsername(username = AccountUtils.USERNAME)).isNotPresent

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

}
