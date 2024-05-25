package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Account
import com.github.vhromada.catalog.entity.AccountStatistics
import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.entity.filter.AccountFilter
import com.github.vhromada.catalog.entity.io.ChangeRolesRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.AccountFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents controller for accounts.
 *
 * @author Vladimir Hromada
 */
@RestController("accountController")
@RequestMapping("rest/accounts")
@Tag(name = "Accounts")
class AccountController(

    /**
     * Facade for accounts
     */
    private val facade: AccountFacade

) {

    /**
     * Returns page of accounts for filter.
     *
     * @param filter filter
     * @return page of accounts for filter
     */
    @GetMapping
    fun search(filter: AccountFilter): Page<Account> {
        return facade.search(filter = filter)
    }

    /**
     * Returns account.
     * <br></br>
     * Validation errors:
     *
     *  * Account doesn't exist in data storage
     *
     * @param uuid UUID
     * @return account
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable("uuid") uuid: String): Account {
        return facade.get(uuid = uuid)
    }

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
     */
    @PutMapping("{uuid}/credentials")
    @ResponseStatus(HttpStatus.CREATED)
    fun updateCredentials(@PathVariable("uuid") uuid: String, @RequestBody credentials: Credentials): Account {
        return facade.updateCredentials(uuid = uuid, credentials = credentials)
    }

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
    @PutMapping("{uuid}/roles")
    fun updateRoles(@PathVariable("uuid") uuid: String, @RequestBody request: ChangeRolesRequest): Account {
        return facade.updateRoles(uuid = uuid, request = request)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): AccountStatistics {
        return facade.getStatistics()
    }

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
     */
    @PostMapping("credentials")
    @ResponseStatus(HttpStatus.CREATED)
    fun addCredentials(@RequestBody credentials: Credentials): Account {
        return facade.addCredentials(credentials = credentials)
    }

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
     */
    @PostMapping("credentials/check")
    fun checkCredentials(@RequestBody credentials: Credentials): Account {
        return facade.checkCredentials(credentials = credentials)
    }

}
