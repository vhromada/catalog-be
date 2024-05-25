package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Account
import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.entity.filter.AccountFilter
import com.github.vhromada.catalog.mapper.AccountMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for accounts.
 *
 * @author Vladimir Hromada
 */
@Component("accountMapper")
class AccountMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider,

    /**
     * Password encoder
     */
    private val passwordEncoder: PasswordEncoder

) : AccountMapper {

    override fun mapAccount(source: Account): com.github.vhromada.catalog.entity.Account {
        return com.github.vhromada.catalog.entity.Account(
            uuid = source.uuid!!,
            username = source.username!!,
            locked = source.locked!!,
            roles = source.roles!!.map { it.name }
        )
    }

    override fun mapAccounts(source: List<Account>): List<com.github.vhromada.catalog.entity.Account> {
        return source.map { mapAccount(source = it) }
    }

    override fun mapFilter(source: AccountFilter): com.github.vhromada.catalog.domain.filter.AccountFilter {
        return com.github.vhromada.catalog.domain.filter.AccountFilter(
            uuid = source.uuid,
            username = source.username,
            usernameOperation = source.usernameOperation
        )
    }

    override fun mapCredentials(source: Credentials): Account {
        return Account(
            id = null,
            uuid = uuidProvider.getUuid(),
            username = source.username!!,
            password = passwordEncoder.encode(source.password),
            locked = false,
            roles = mutableListOf()
        )
    }

}
