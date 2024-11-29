package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Game
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * A class represents filter for games.
 *
 * @author Vladimir Hromada
 */
data class GameFilter(

    /**
     * Name
     */
    val name: String? = null

) : JpaFilter<Game>() {

    override fun isEmpty(): Boolean {
        return name.isNullOrBlank()
    }

    override fun process(root: Root<Game>, query: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder): List<Predicate> {
        val result = mutableListOf<Predicate>()
        getPredicate(criteriaBuilder, root, "name", name, FieldOperation.LIKE)?.let { result.add(it) }
        return result.toList()
    }

}
