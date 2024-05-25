package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Account
import com.github.vhromada.catalog.entity.AccountStatistics
import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.entity.filter.AccountFilter
import com.github.vhromada.catalog.entity.io.ChangeRolesRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountFacade {

    /**
     * Returns page of accounts for filter.
     *
     * @param filter filter
     * @return page of accounts for filter
     */
    fun search(filter: AccountFilter): Page<Account>

    /**
     * Returns account.
     *
     * @param uuid UUID
     * @return account
     * @throws InputException if account doesn't exist in data storage
     */
    fun get(uuid: String): Account

    /**
     * Updates credentials.
     * <br></br>
     * Validation errors:
     *
     *  * Username is null
     *  * Username is empty string
     *  * Password is null
     *  * Password is empty string
     *  * Account doesn't exist in data storage
     *  * Username exists in data storage
     *
     * @param uuid        UUID
     * @param credentials credentials
     * @return updated account
     * @throws InputException if credentials aren't valid
     */
    fun updateCredentials(uuid: String, credentials: Credentials): Account

    /**
     * Updates roles.
     * <br></br>
     * Validation errors:
     *
     *  * Roles is null
     *  * Roles is empty
     *  * Roles contains null
     *  * Account doesn't exist in data storage
     *  * Role doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing roles
     * @return updated account
     * @throws InputException if request for changing roles isn't valid
     */
    fun updateRoles(uuid: String, request: ChangeRolesRequest): Account

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): AccountStatistics

    /**
     * Adds credentials.
     * <br></br>
     * Validation errors:
     *
     *  * Username is null
     *  * Username is empty string
     *  * Password is null
     *  * Password is empty string
     *  * Username exists in data storage
     *
     * @param credentials credentials
     * @return created account
     * @throws InputException if credentials aren't valid
     */
    fun addCredentials(credentials: Credentials): Account

    /**
     * Checks credentials.
     * <br></br>
     * Validation errors:
     *
     *  * Username is null
     *  * Username is empty string
     *  * Password is null
     *  * Password is empty string
     *  * Account doesn't exist in data storage
     *  * Credentials aren't valid
     *
     * @param credentials credentials
     * @return account
     * @throws InputException if credentials aren't valid
     */
    fun checkCredentials(credentials: Credentials): Account

}
