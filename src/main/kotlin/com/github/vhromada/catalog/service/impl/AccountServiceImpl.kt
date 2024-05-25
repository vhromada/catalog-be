package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Account
import com.github.vhromada.catalog.domain.filter.AccountFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.AccountRepository
import com.github.vhromada.catalog.service.AccountService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * A class represents implementation of service for accounts.
 *
 * @author Vladimir Hromada
 */
@Service("accountService")
class AccountServiceImpl(

    /**
     * Repository for accounts
     */
    private val repository: AccountRepository

) : AccountService {

    override fun search(filter: AccountFilter, pageable: Pageable): Page<Account> {
        if (filter.isEmpty()) {
            return repository.findAll(pageable)
        }
        return repository.findAll(filter.toSpecification(), pageable)
    }

    override fun find(filter: AccountFilter): Optional<Account> {
        filter.assertNotEmpty()
        return repository.findOne(filter.toSpecification())
    }

    override fun get(uuid: String): Account {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "ACCOUNT_NOT_EXIST", message = "Account doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(account: Account): Account {
        return repository.save(account)
    }

    override fun checkUsername(username: String) {
        repository.findByUsername(username = username)
            .ifPresent { throw InputException(key = "ACCOUNT_USERNAME_ALREADY_EXIST", message = "Username already exists.") }
    }

    override fun getCount(): Long {
        return repository.count()
    }

}
