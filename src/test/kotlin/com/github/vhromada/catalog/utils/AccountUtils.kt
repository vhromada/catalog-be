package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Role
import com.github.vhromada.catalog.entity.Account
import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.entity.filter.AccountFilter
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates account fields.
 *
 * @return updated account
 */
fun com.github.vhromada.catalog.domain.Account.updated(): com.github.vhromada.catalog.domain.Account {
    username = AccountUtils.USERNAME
    password = AccountUtils.PASSWORD
    locked = false
    return this
}

/**
 * A class represents utility class for accounts.
 *
 * @author Vladimir Hromada
 */
object AccountUtils {

    /**
     * Count of accounts
     */
    const val ACCOUNTS_COUNT = 3

    /**
     * Username
     */
    const val USERNAME = "username"

    /**
     * Password
     */
    const val PASSWORD = "password"

    /**
     * Returns accounts.
     *
     * @return accounts
     */
    fun getDomainAccounts(): List<com.github.vhromada.catalog.domain.Account> {
        val accounts = mutableListOf<com.github.vhromada.catalog.domain.Account>()
        for (i in 1..ACCOUNTS_COUNT) {
            accounts.add(getDomainAccount(index = i))
        }

        return accounts
    }

    /**
     * Returns accounts.
     *
     * @return accounts
     */
    fun getAccounts(): List<Account> {
        val accounts = mutableListOf<Account>()
        for (i in 1..ACCOUNTS_COUNT) {
            accounts.add(getAccount(index = i))
        }

        return accounts
    }

    /**
     * Returns account for index.
     *
     * @param index index
     * @return account for index
     */
    fun getDomainAccount(index: Int): com.github.vhromada.catalog.domain.Account {
        if (index == 1) {
            return getAdminDomainAccount()
        }
        val uuid: String
        val roles = mutableListOf<Role>()
        when (index) {
            2 -> {
                uuid = "d53b2577-a3de-4df7-a8dd-2e6d9e5c1014"
                roles.add(RoleUtils.getDomainRole(index = 2))
            }

            3 -> {
                uuid = "0998ab47-0d27-4538-b551-ee7a471cfcf1"
                roles.add(RoleUtils.getDomainRole(index = 1))
                roles.add(RoleUtils.getDomainRole(index = 2))
            }

            else -> throw IllegalArgumentException("Bad index")
        }

        return com.github.vhromada.catalog.domain.Account(
            id = index,
            uuid = uuid,
            username = "Account ${index - 1} username",
            password = "Account ${index - 1} password",
            locked = index == 2,
            roles = roles
        )
    }

    /**
     * Returns admin account.
     *
     * @return admin account
     */
    private fun getAdminDomainAccount(): com.github.vhromada.catalog.domain.Account {
        return com.github.vhromada.catalog.domain.Account(
            id = 1,
            uuid = "dc0d73bc-e19e-4c91-b818-192907def7ec",
            username = "admin",
            password = "\$2a\$10\$CKwbyaXtgmTIFJj07XGPPOR3Qn8zCNUHN97/C9tm1oEGv.hJNEJU.",
            locked = false,
            roles = mutableListOf(RoleUtils.getDomainRole(index = 1))
        )
    }

    /**
     * Returns account.
     *
     * @param entityManager entity manager
     * @param id            game ID
     * @return account
     */
    fun getDomainAccount(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Account? {
        return entityManager.find(com.github.vhromada.catalog.domain.Account::class.java, id)
    }

    /**
     * Returns account for index.
     *
     * @param index index
     * @return account for index
     */
    fun getAccount(index: Int): Account {
        if (index == 1) {
            return getAdminAccount()
        }
        val uuid: String
        val roles = mutableListOf<String>()
        when (index) {
            2 -> {
                uuid = "d53b2577-a3de-4df7-a8dd-2e6d9e5c1014"
                roles.add(RoleUtils.getRole(index = 2))
            }

            3 -> {
                uuid = "0998ab47-0d27-4538-b551-ee7a471cfcf1"
                roles.add(RoleUtils.getRole(index = 1))
                roles.add(RoleUtils.getRole(index = 2))
            }

            else -> throw IllegalArgumentException("Bad index")
        }

        return Account(
            uuid = uuid,
            username = "Account ${index - 1} username",
            locked = index == 2,
            roles = roles
        )
    }

    /**
     * Returns admin account.
     *
     * @return admin account
     */
    fun getAdminAccount(): Account {
        return Account(
            uuid = "dc0d73bc-e19e-4c91-b818-192907def7ec",
            username = "admin",
            locked = false,
            roles = listOf(RoleUtils.getRole(index = 1))
        )
    }

    /**
     * Returns admin credentials.
     *
     * @return admin credentials
     */
    fun getAdminCredentials(): Credentials {
        val admin = getAdminDomainAccount()
        return Credentials(
            username = admin.username,
            password = admin.password
        )
    }

    /**
     * Returns count of accounts.
     *
     * @param entityManager entity manager
     * @return count of accounts
     */
    fun getAccountsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(a.id) FROM Account a", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns account.
     *
     * @param id ID
     * @return account
     */
    fun newDomainAccount(id: Int?): com.github.vhromada.catalog.domain.Account {
        return com.github.vhromada.catalog.domain.Account(
            id = id,
            uuid = TestConstants.UUID,
            username = USERNAME,
            password = PASSWORD,
            locked = false,
            roles = mutableListOf()
        )
    }

    /**
     * Returns filter.
     *
     * @return filter
     */
    fun newDomainFilter(): com.github.vhromada.catalog.domain.filter.AccountFilter {
        return com.github.vhromada.catalog.domain.filter.AccountFilter(
            uuid = TestConstants.UUID,
            username = USERNAME
        )
    }

    /**
     * Returns account.
     *
     * @return account
     */
    fun newAccount(): Account {
        return Account(
            uuid = TestConstants.UUID,
            username = USERNAME,
            locked = false,
            roles = listOf(RoleUtils.getDomainRole(index = 1).name)
        )
    }

    /**
     * Returns credentials.
     *
     * @return credentials
     */
    fun newCredentials(): Credentials {
        return Credentials(
            username = USERNAME,
            password = PASSWORD
        )
    }

    /**
     * Returns filter.
     *
     * @return filter
     */
    fun newFilter(): AccountFilter {
        return AccountFilter(
            uuid = TestConstants.UUID,
            username = USERNAME,
            usernameOperation = FieldOperation.LIKE
        )
    }

    /**
     * Asserts accounts deep equals.
     *
     * @param expected expected accounts
     * @param actual   actual accounts
     */
    fun assertDomainAccountsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Account>, actual: List<com.github.vhromada.catalog.domain.Account>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertAccountDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts account deep equals.
     *
     * @param expected   expected account
     * @param actual     actual account
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertAccountDeepEquals(expected: com.github.vhromada.catalog.domain.Account?, actual: com.github.vhromada.catalog.domain.Account?, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.username).isEqualTo(expected.username)
                it.assertThat(actual.password).isEqualTo(expected.password)
                it.assertThat(actual.locked).isEqualTo(expected.locked)
            }
            RoleUtils.assertDomainRolesDeepEquals(expected = expected.roles!!, actual = actual!!.roles!!)
        }
    }

    /**
     * Asserts accounts deep equals.
     *
     * @param expected expected accounts
     * @param actual   actual accounts
     */
    fun assertAccountsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Account>, actual: List<Account>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertAccountDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts account deep equals.
     *
     * @param expected   expected account
     * @param actual     actual account
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertAccountDeepEquals(expected: com.github.vhromada.catalog.domain.Account, actual: Account, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.locked).isEqualTo(expected.locked)
        }
        RoleUtils.assertRolesDeepEquals(expected = expected.roles!!, actual = actual.roles)
    }

    /**
     * Asserts accounts deep equals.
     *
     * @param expected expected accounts
     * @param actual   actual accounts
     */
    fun assertAccountListDeepEquals(expected: List<Account>, actual: List<Account>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertAccountDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts account deep equals.
     *
     * @param expected   expected account
     * @param actual     actual account
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertAccountDeepEquals(expected: Account, actual: Account, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.locked).isEqualTo(expected.locked)
            it.assertThat(actual.roles).isEqualTo(expected.roles)
        }
    }

    /**
     * Asserts credentials and account deep equals.
     *
     * @param expected expected credentials
     * @param actual   actual account
     * @param uuid     UUID
     */
    fun assertCredentialsDeepEquals(expected: Credentials, actual: com.github.vhromada.catalog.domain.Account, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.password).isEqualTo(expected.password)
            it.assertThat(actual.locked).isEqualTo(false)
            it.assertThat(actual.roles).isEmpty()
        }
    }

    /**
     * Asserts filter deep equals.
     *
     * @param expected expected filter for accounts
     * @param actual   actual filter for accounts
     */
    fun assertFilterDeepEquals(expected: AccountFilter, actual: com.github.vhromada.catalog.domain.filter.AccountFilter) {
        assertSoftly {
            it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            it.assertThat(actual.username).isEqualTo(expected.username)
            it.assertThat(actual.usernameOperation).isEqualTo(expected.usernameOperation)
        }
    }

}
