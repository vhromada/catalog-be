package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Movie
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * A class represents filter for movies.
 *
 * @author Vladimir Hromada
 */
data class MovieFilter(

    /**
     * Czech name
     */
    val czechName: String? = null,

    /**
     * Original name
     */
    val originalName: String? = null

) : JpaFilter<Movie>() {

    override fun isEmpty(): Boolean {
        return czechName.isNullOrBlank() && originalName.isNullOrBlank()
    }

    @Suppress("DuplicatedCode")
    override fun process(root: Root<Movie>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): List<Predicate> {
        val result = mutableListOf<Predicate>()
        getPredicate(criteriaBuilder, root, "czechName", czechName, FieldOperation.LIKE)?.let { result.add(it) }
        getPredicate(criteriaBuilder, root, "originalName", originalName, FieldOperation.LIKE)?.let { result.add(it) }
        return result.toList()
    }

}
