package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.domain.Role
import com.github.vhromada.catalog.entity.Account
import com.github.vhromada.catalog.entity.AccountStatistics
import com.github.vhromada.catalog.entity.Credentials
import com.github.vhromada.catalog.entity.filter.AccountFilter
import com.github.vhromada.catalog.entity.io.ChangeRolesRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.AccountFacade
import com.github.vhromada.catalog.mapper.AccountMapper
import com.github.vhromada.catalog.service.AccountService
import com.github.vhromada.catalog.service.RoleService
import com.github.vhromada.catalog.validator.AccountValidator
import com.github.vhromada.catalog.validator.RoleValidator
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for accounts.
 *
 * @author Vladimir Hromada
 */
@Component("accountFacade")
class AccountFacadeImpl(

    /**
     * Service for accounts
     */
    private val accountService: AccountService,

    /**
     * Service for roles
     */
    private val roleService: RoleService,

    /**
     * Mapper for accounts
     */
    private val mapper: AccountMapper,

    /**
     * Validator for accounts
     */
    private val accountValidator: AccountValidator,

    /**
     * Validator for roles
     */
    private val roleValidator: RoleValidator,

    /**
     * Password encoder
     */
    private val encoder: PasswordEncoder

) : AccountFacade {

    override fun search(filter: AccountFilter): Page<Account> {
        val accounts = accountService.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("id")))
        return Page(data = mapper.mapAccounts(source = accounts.content), page = accounts)
    }

    override fun get(uuid: String): Account {
        return mapper.mapAccount(source = accountService.get(uuid = uuid))
    }

    override fun updateCredentials(uuid: String, credentials: Credentials): Account {
        accountValidator.validateCredentials(credentials = credentials)
        val account = accountService.get(uuid = uuid)
        if (account.username != credentials.username) {
            accountService.checkUsername(username = credentials.username!!)
        }
        account.merge(mapper.mapCredentials(source = credentials))
        return mapper.mapAccount(source = accountService.store(account = account))
    }

    override fun updateRoles(uuid: String, request: ChangeRolesRequest): Account {
        roleValidator.validateRequest(request = request)
        val account = accountService.get(uuid = uuid)
        account.changeRoles(roleList = getRoles(roles = request.roles))
        return mapper.mapAccount(source = accountService.store(account = account))
    }

    override fun getStatistics(): AccountStatistics {
        return AccountStatistics(count = accountService.getCount().toInt())
    }

    override fun addCredentials(credentials: Credentials): Account {
        accountValidator.validateCredentials(credentials = credentials)
        accountService.checkUsername(username = credentials.username!!)
        val account = mapper.mapCredentials(source = credentials)
        account.changeRoles(getRoles(roles = listOf("ROLE_USER")))
        return mapper.mapAccount(source = accountService.store(account = account))
    }

    override fun checkCredentials(credentials: Credentials): Account {
        accountValidator.validateCredentials(credentials = credentials)
        val account = accountService.find(filter = com.github.vhromada.catalog.domain.filter.AccountFilter(username = credentials.username))
            .orElseThrow { InputException(key = "INVALID_CREDENTIALS", message = "Credentials aren't valid.") }
        val valid = encoder.matches(credentials.password, account.password)
        if (!valid) {
            throw InputException(key = "INVALID_CREDENTIALS", message = "Credentials aren't valid.")
        }
        return mapper.mapAccount(account)
    }

    /**
     * Returns roles.
     *
     * @param roles role names
     * @return roles
     * @throws InputException if role doesn't exist in data storage
     */
    private fun getRoles(roles: List<String?>?): List<Role> {
        return roles!!.filterNotNull().map { roleService.get(name = it) }
    }

}
