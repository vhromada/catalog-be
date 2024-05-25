package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

/**
 * An interface represents repository for accounts.
 *
 * @author Vladimir Hromada
 */
interface AccountRepository : JpaRepository<Account, Int>, JpaSpecificationExecutor<Account> {

    /**
     * Finds account by UUID.
     *
     * @param uuid UUID
     * @return account
     */
    fun findByUuid(uuid: String): Optional<Account>

    /**
     * Finds account by username.
     *
     * @param username username
     * @return account
     */
    fun findByUsername(username: String): Optional<Account>

}
