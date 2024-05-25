package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Genre
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * A class represents filter for genres.
 *
 * @author Vladimir Hromada
 */
data class GenreFilter(

    /**
     * Name
     */
    val name: String? = null

) : JpaFilter<Genre>() {

    override fun isEmpty(): Boolean {
        return name.isNullOrBlank()
    }

    override fun process(root: Root<Genre>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): List<Predicate> {
        val result = mutableListOf<Predicate>()
        getPredicate(criteriaBuilder, root, "name", name, FieldOperation.LIKE)?.let { result.add(it) }
        return result.toList()
    }

}
