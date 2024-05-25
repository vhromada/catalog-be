package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.entity.filter.AccountFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.RoleUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [AccountFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class AccountFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [AccountFacade]
     */
    @Autowired
    private lateinit var facade: AccountFacade

    /**
     * Test method for [AccountFacade.search].
     */
    @Test
    fun search() {
        val filter = AccountFilter()
        filter.page = 1
        filter.limit = AccountUtils.ACCOUNTS_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        AccountUtils.assertAccountListDeepEquals(expected = AccountUtils.getAccounts(), actual = result.data)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = AccountFilter()
        filter.page = 2
        filter.limit = 1

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
        }
        AccountUtils.assertAccountListDeepEquals(expected = listOf(AccountUtils.getAccount(index = 2)), actual = result.data)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = AccountFilter()
        filter.page = 2
        filter.limit = AccountUtils.ACCOUNTS_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val account = AccountUtils.getAccount(index = i)
            val filter = AccountFilter(uuid = account.uuid, username = account.username)
            filter.page = 1
            filter.limit = AccountUtils.ACCOUNTS_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            AccountUtils.assertAccountListDeepEquals(expected = listOf(account), actual = result.data)
        }

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.search] with filter with operation.
     */
    @Test
    fun searchFilterOperation() {
        val filter = AccountFilter(username = "account", usernameOperation = FieldOperation.LIKE)
        filter.page = 1
        filter.limit = AccountUtils.ACCOUNTS_COUNT

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        AccountUtils.assertAccountListDeepEquals(expected = AccountUtils.getAccounts().subList(1, AccountUtils.ACCOUNTS_COUNT), actual = result.data)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.search] with not existing filter.
     */
    @Test
    fun searchNotExisting() {
        val filter = AccountFilter(uuid = TestConstants.UUID, username = AccountUtils.USERNAME)

        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..AccountUtils.ACCOUNTS_COUNT) {
            val account = AccountUtils.getDomainAccount(index = i)

            val result = facade.get(uuid = account.uuid!!)

            AccountUtils.assertAccountDeepEquals(expected = account, actual = result)
        }

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ACCOUNT_NOT_EXIST")
            .hasMessageContaining("Account doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateCredentials].
     */
    @Test
    @DirtiesContext
    fun updateCredentials() {
        val credentials = AccountUtils.newCredentials()
        val expectedAccount = AccountUtils.newAccount()
            .copy(uuid = AccountUtils.getAccount(index = 1).uuid)
        val expectedDomainAccount = AccountUtils.getDomainAccount(index = 1)
            .updated()
            .copy(roles = mutableListOf(RoleUtils.getDomainRole(index = 1)))

        val result = facade.updateCredentials(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, credentials = credentials)

        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = result)
        AccountUtils.assertAccountDeepEquals(expected = expectedDomainAccount, actual = AccountUtils.getDomainAccount(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateCredentials] with request with null username.
     */
    @Test
    fun updateCredentialsNullUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = null)

        assertThatThrownBy { facade.updateCredentials(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_NULL")
            .hasMessageContaining("Username mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateCredentials] with request with empty username.
     */
    @Test
    fun updateCredentialsEmptyUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = "")

        assertThatThrownBy { facade.updateCredentials(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_EMPTY")
            .hasMessageContaining("Username mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateCredentials] with request with null password.
     */
    @Test
    fun updateCredentialsNullPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = null)

        assertThatThrownBy { facade.updateCredentials(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_NULL")
            .hasMessageContaining("Password mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateCredentials] with request with empty password.
     */
    @Test
    fun updateCredentialsEmptyPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = "")

        assertThatThrownBy { facade.updateCredentials(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_EMPTY")
            .hasMessageContaining("Password mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateCredentials] with request with existing username.
     */
    @Test
    fun updateCredentialsExistingUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = AccountUtils.getDomainAccount(index = 2).username)

        assertThatThrownBy { facade.updateCredentials(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ACCOUNT_USERNAME_ALREADY_EXIST")
            .hasMessageContaining("Username already exists.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateCredentials] with not existing UUID.
     */
    @Test
    fun updateCredentialsNotExisting() {
        assertThatThrownBy { facade.updateCredentials(uuid = TestConstants.UUID, credentials = AccountUtils.newCredentials()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ACCOUNT_NOT_EXIST")
            .hasMessageContaining("Account doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateRoles].
     */
    @Test
    @DirtiesContext
    fun updateRoles() {
        val request = RoleUtils.newRequest()
        val expectedAccount = AccountUtils.getAccount(index = 1)
            .copy(roles = mutableListOf(RoleUtils.getRole(index = 2)))
        val expectedDomainAccount = AccountUtils.getDomainAccount(index = 1)
            .copy(roles = mutableListOf(RoleUtils.getDomainRole(index = 2)))

        val result = facade.updateRoles(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, request = request)

        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = result)
        AccountUtils.assertAccountDeepEquals(expected = expectedDomainAccount, actual = AccountUtils.getDomainAccount(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateRoles] with request with null roles.
     */
    @Test
    fun updateRolesNullRoles() {
        val request = RoleUtils.newRequest()
            .copy(roles = null)

        assertThatThrownBy { facade.updateRoles(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_ROLES_NULL")
            .hasMessageContaining("Roles mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateRoles] with request with empty roles.
     */
    @Test
    fun updateRolesEmptyRoles() {
        val request = RoleUtils.newRequest()
            .copy(roles = emptyList())

        assertThatThrownBy { facade.updateRoles(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_ROLES_EMPTY")
            .hasMessageContaining("Roles mustn't be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateRoles] with request with roles with null value.
     */
    @Test
    fun updateRolesNullRole() {
        val request = RoleUtils.newRequest()
            .copy(roles = listOf(null))

        assertThatThrownBy { facade.updateRoles(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_ROLES_CONTAIN_NULL")
            .hasMessageContaining("Roles mustn't contain null value.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateRoles] with request with not existing role.
     */
    @Test
    fun updateRolesNotExistingRole() {
        val request = RoleUtils.newRequest()
            .copy(roles = listOf("role"))

        assertThatThrownBy { facade.updateRoles(uuid = AccountUtils.getDomainAccount(index = 1).uuid!!, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_NOT_EXIST")
            .hasMessageContaining("Role doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.updateRoles] with not existing UUID.
     */
    @Test
    fun updateRolesNotExisting() {
        assertThatThrownBy { facade.updateRoles(uuid = TestConstants.UUID, request = RoleUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ACCOUNT_NOT_EXIST")
            .hasMessageContaining("Account doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        assertThat(facade.getStatistics().count).isEqualTo(AccountUtils.ACCOUNTS_COUNT)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.addCredentials].
     */
    @Test
    @DirtiesContext
    fun addCredentials() {
        val credentials = AccountUtils.newCredentials()
        val expectedAccount = AccountUtils.newAccount()
            .copy(roles = listOf(RoleUtils.getRole(index = 2)))
        val expectedDomainAccount = AccountUtils.newDomainAccount(id = AccountUtils.ACCOUNTS_COUNT + 1)
            .copy(roles = mutableListOf(RoleUtils.getDomainRole(index = 2)))

        val result = facade.addCredentials(credentials = credentials)
        entityManager.flush()

        AccountUtils.assertAccountDeepEquals(expected = expectedAccount, actual = result, ignoreUuid = true)
        AccountUtils.assertAccountDeepEquals(
            expected = expectedDomainAccount,
            actual = AccountUtils.getDomainAccount(entityManager = entityManager, id = AccountUtils.ACCOUNTS_COUNT + 1),
            ignoreUuid = true
        )

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT + 1)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.addCredentials] with credentials with null username.
     */
    @Test
    fun addCredentialsNullUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = null)

        assertThatThrownBy { facade.addCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_NULL")
            .hasMessageContaining("Username mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.addCredentials] with credentials with empty username.
     */
    @Test
    fun addCredentialsEmptyUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = "")

        assertThatThrownBy { facade.addCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_EMPTY")
            .hasMessageContaining("Username mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.addCredentials] with credentials with null password.
     */
    @Test
    fun addCredentialsNullPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = null)

        assertThatThrownBy { facade.addCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_NULL")
            .hasMessageContaining("Password mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.addCredentials] with credentials with empty password.
     */
    @Test
    fun addCredentialsEmptyPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = "")

        assertThatThrownBy { facade.addCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_EMPTY")
            .hasMessageContaining("Password mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.addCredentials] with credentials with existing username.
     */
    @Test
    fun addCredentialsExistingUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = AccountUtils.getDomainAccount(index = 2).username)

        assertThatThrownBy { facade.addCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ACCOUNT_USERNAME_ALREADY_EXIST")
            .hasMessageContaining("Username already exists.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.checkCredentials].
     */
    @Test
    fun checkCredentials() {
        val result = facade.checkCredentials(credentials = AccountUtils.getAdminCredentials())

        AccountUtils.assertAccountDeepEquals(expected = AccountUtils.getAdminAccount(), actual = result)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.checkCredentials] with credentials with null username.
     */
    @Test
    fun checkCredentialsNullUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = null)

        assertThatThrownBy { facade.checkCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_NULL")
            .hasMessageContaining("Username mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.checkCredentials] with credentials with empty username.
     */
    @Test
    fun checkCredentialsEmptyUsername() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = "")

        assertThatThrownBy { facade.checkCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_USERNAME_EMPTY")
            .hasMessageContaining("Username mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.checkCredentials] with credentials with null password.
     */
    @Test
    fun checkCredentialsNullPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = null)

        assertThatThrownBy { facade.checkCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_NULL")
            .hasMessageContaining("Password mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.checkCredentials] with credentials with empty password.
     */
    @Test
    fun checkCredentialsEmptyPassword() {
        val credentials = AccountUtils.newCredentials()
            .copy(password = "")

        assertThatThrownBy { facade.checkCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CREDENTIALS_PASSWORD_EMPTY")
            .hasMessageContaining("Password mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.checkCredentials] with credentials with not existing account.
     */
    @Test
    fun checkCredentialsNotExisting() {
        val credentials = AccountUtils.newCredentials()
            .copy(username = AccountUtils.USERNAME)

        assertThatThrownBy { facade.checkCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("INVALID_CREDENTIALS")
            .hasMessageContaining("Credentials aren't valid.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

    /**
     * Test method for [AccountFacade.checkCredentials] with credentials with invalid credentials.
     */
    @Test
    fun checkCredentialsInvalidCredentials() {
        val credentials = AccountUtils.getAdminCredentials()
            .copy(password = AccountUtils.PASSWORD)

        assertThatThrownBy { facade.checkCredentials(credentials = credentials) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("INVALID_CREDENTIALS")
            .hasMessageContaining("Credentials aren't valid.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(AccountUtils.getAccountsCount(entityManager = entityManager)).isEqualTo(AccountUtils.ACCOUNTS_COUNT)
            it.assertThat(RoleUtils.getRolesCount(entityManager = entityManager)).isEqualTo(RoleUtils.ROLES_COUNT)
        }
    }

}
