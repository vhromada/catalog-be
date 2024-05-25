package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.AccountMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * A class represents test for class [AccountMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class AccountMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [PasswordEncoder]
     */
    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    /**
     * Instance of [AccountMapper]
     */
    private lateinit var mapper: AccountMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = AccountMapperImpl(uuidProvider = uuidProvider, passwordEncoder = passwordEncoder)
    }

    /**
     * Test method for [AccountMapper.mapAccount].
     */
    @Test
    fun mapAccount() {
        val account = AccountUtils.getDomainAccount(index = 1)

        val result = mapper.mapAccount(source = account)

        AccountUtils.assertAccountDeepEquals(expected = account, actual = result)
        verifyNoInteractions(uuidProvider, passwordEncoder)
    }

    /**
     * Test method for [AccountMapper.mapAccounts].
     */
    @Test
    fun mapAccounts() {
        val account = AccountUtils.getDomainAccount(index = 1)

        val result = mapper.mapAccounts(source = listOf(account))

        AccountUtils.assertAccountsDeepEquals(expected = listOf(account), actual = result)
        verifyNoInteractions(uuidProvider, passwordEncoder)
    }

    /**
     * Test method for [AccountMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val filter = AccountUtils.newFilter()

        val result = mapper.mapFilter(source = filter)

        AccountUtils.assertFilterDeepEquals(expected = filter, actual = result)
        verifyNoInteractions(uuidProvider, passwordEncoder)
    }

    /**
     * Test method for [AccountMapper.mapCredentials].
     */
    @Test
    fun mapCredentials() {
        val credentials = AccountUtils.newCredentials()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)
        whenever(passwordEncoder.encode(any())).thenReturn(credentials.password)

        val result = mapper.mapCredentials(source = credentials)

        AccountUtils.assertCredentialsDeepEquals(expected = credentials, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verify(passwordEncoder).encode(credentials.password)
        verifyNoMoreInteractions(uuidProvider, passwordEncoder)
    }

}
