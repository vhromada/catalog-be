package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Account
import com.github.vhromada.catalog.domain.filter.AccountFilter
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.Optional

/**
 * An interface represents service for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountService {

    /**
     * Returns page of accounts for filter.
     *
     * @param filter filter
     * @param pageable paging information
     * @return page of accounts for filter
     */
    fun search(filter: AccountFilter, pageable: Pageable): Page<Account>

    /**
     * Returns account for filter.
     *
     * @param filter filter
     * @return account for filter
     * @throws InputException if filter is empty
     */
    fun find(filter: AccountFilter): Optional<Account>

    /**
     * Returns account.
     *
     * @param uuid UUID
     * @return account
     * @throws InputException if account doesn't exist in data storage
     */
    fun get(uuid: String): Account

    /**
     * Stores account.
     *
     * @param account account
     * @return stored account
     */
    fun store(account: Account): Account

    /**
     * Checks username.
     *
     * @param username username
     * @throws InputException if username exists in data storage
     */
    fun checkUsername(username: String)

    /**
     * Returns count of accounts.
     *
     * @return count of accounts
     */
    fun getCount(): Long

}
