package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Account
import com.github.vhromada.catalog.domain.filter.AccountFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.AccountRepository
import com.github.vhromada.catalog.service.impl.AccountServiceImpl
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [AccountService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class AccountServiceTest {

    /**
     * Instance of [AccountRepository]
     */
    @Mock
    private lateinit var repository: AccountRepository

    /**
     * Instance of [AccountService]
     */
    private lateinit var service: AccountService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = AccountServiceImpl(repository = repository)
    }

    /**
     * Test method for [AccountService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(AccountUtils.getDomainAccount(index = 1)))
        whenever(repository.findAll(any<Specification<Account>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = AccountUtils.newDomainFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(any<Specification<Account>>(), eq(pageable))
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(AccountUtils.getDomainAccount(index = 1)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = AccountFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(pageable)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.find].
     */
    @Test
    fun find() {
        val account = AccountUtils.getDomainAccount(index = 1)
        whenever(repository.findOne(any<Specification<Account>>())).thenReturn(Optional.of(account))

        val result = service.find(filter = AccountUtils.newDomainFilter())

        assertThat(result).isEqualTo(Optional.of(account))
        verify(repository).findOne(any<Specification<Account>>())
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.find] with empty filter.
     */
    @Test
    fun findEmptyFilter() {
        assertThatThrownBy { service.find(filter = AccountFilter()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("EMPTY_FILTER")
            .hasMessageContaining("Filter cannot be empty.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
        verifyNoInteractions(repository)
    }

    /**
     * Test method for [AccountService.get].
     */
    @Test
    fun get() {
        val account = AccountUtils.getDomainAccount(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(account))

        val result = service.get(uuid = account.uuid!!)

        assertThat(result).isEqualTo(account)
        verify(repository).findByUuid(uuid = account.uuid!!)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ACCOUNT_NOT_EXIST")
            .hasMessageContaining("Account doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.store].
     */
    @Test
    fun store() {
        val account = AccountUtils.getDomainAccount(index = 1)
        whenever(repository.save(anyAccount())).thenAnswer { it.arguments[0] }

        val result = service.store(account = account)

        assertThat(result).isEqualTo(account)
        verify(repository).save(account)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.checkUsername].
     */
    @Test
    fun checkUsername() {
        whenever(repository.findByUsername(username = any())).thenReturn(Optional.empty())

        service.checkUsername(username = AccountUtils.USERNAME)

        verify(repository).findByUsername(username = AccountUtils.USERNAME)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.checkUsername] with existing username.
     */
    @Test
    fun checkUsernameExisting() {
        val account = AccountUtils.getDomainAccount(index = 1)
        whenever(repository.findByUsername(username = any())).thenReturn(Optional.of(account))

        assertThatThrownBy { service.checkUsername(username = account.username!!) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ACCOUNT_USERNAME_ALREADY_EXIST")
            .hasMessageContaining("Username already exists.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
        verify(repository).findByUsername(username = account.username!!)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [AccountService.getCount].
     */
    @Test
    fun getCount() {
        whenever(repository.count()).thenReturn(AccountUtils.ACCOUNTS_COUNT.toLong())

        val result = service.getCount()

        assertThat(result).isEqualTo(AccountUtils.ACCOUNTS_COUNT.toLong())
        verify(repository).count()
        verifyNoMoreInteractions(repository)
    }

    /**
     * Returns any mock for account.
     *
     * @return any mock for account
     */
    private fun anyAccount(): Account {
        return any()
    }

}
