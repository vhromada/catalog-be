package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Account
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * A class represents filter for accounts.
 *
 * @author Vladimir Hromada
 */
data class AccountFilter(

    /**
     * UUID
     */
    val uuid: String? = null,

    /**
     * Username
     */
    val username: String? = null,

    /**
     * Operation for username
     */
    val usernameOperation: FieldOperation? = null

) : JpaFilter<Account>() {

    override fun isEmpty(): Boolean {
        return uuid.isNullOrBlank() && username.isNullOrBlank()
    }

    override fun process(root: Root<Account>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): List<Predicate> {
        val result = mutableListOf<Predicate>()
        getPredicate(criteriaBuilder, root, "uuid", uuid)?.let { result.add(it) }
        getPredicate(criteriaBuilder, root, "username", username, usernameOperation)?.let { result.add(it) }
        return result.toList()
    }

}
