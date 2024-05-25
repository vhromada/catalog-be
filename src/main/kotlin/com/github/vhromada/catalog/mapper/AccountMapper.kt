package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Account
import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.entity.filter.AccountFilter

/**
 * An interface class represents mapper for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountMapper {

    /**
     * Maps accounts.
     *
     * @param source account
     * @return mapped account
     */
    fun mapAccount(source: Account): com.github.vhromada.catalog.entity.Account

    /**
     * Maps list of accounts.
     *
     * @param source list of account
     * @return mapped list of account
     */
    fun mapAccounts(source: List<Account>): List<com.github.vhromada.catalog.entity.Account>

    /**
     * Maps filter for accounts.
     *
     * @param source filter for accounts
     * @return mapped filter for accounts
     */
    fun mapFilter(source: AccountFilter): com.github.vhromada.catalog.domain.filter.AccountFilter

    /**
     * Maps credentials.
     *
     * @param source credentials
     * @return mapped account
     */
    fun mapCredentials(source: Credentials): Account

}
