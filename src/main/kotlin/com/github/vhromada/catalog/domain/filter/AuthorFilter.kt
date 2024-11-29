package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Author
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * A class represents filter for authors.
 *
 * @author Vladimir Hromada
 */
data class AuthorFilter(

    /**
     * First name
     */
    val firstName: String? = null,

    /**
     * Middle name
     */
    val middleName: String? = null,

    /**
     * Last name
     */
    val lastName: String? = null

) : JpaFilter<Author>() {

    override fun isEmpty(): Boolean {
        return firstName.isNullOrBlank() && middleName.isNullOrBlank() && lastName.isNullOrBlank()
    }

    override fun process(root: Root<Author>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): List<Predicate> {
        val result = mutableListOf<Predicate>()
        getPredicate(criteriaBuilder, root, "firstName", firstName, FieldOperation.LIKE)?.let { result.add(it) }
        getPredicate(criteriaBuilder, root, "middleName", middleName, FieldOperation.LIKE)?.let { result.add(it) }
        getPredicate(criteriaBuilder, root, "lastName", lastName, FieldOperation.LIKE)?.let { result.add(it) }
        return result.toList()
    }

}
