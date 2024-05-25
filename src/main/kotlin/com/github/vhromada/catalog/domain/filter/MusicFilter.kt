package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.domain.Music
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root

/**
 * A class represents filter for music.
 *
 * @author Vladimir Hromada
 */
data class MusicFilter(

    /**
     * Name
     */
    val name: String? = null

) : JpaFilter<Music>() {

    override fun isEmpty(): Boolean {
        return name.isNullOrBlank()
    }

    override fun process(root: Root<Music>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): List<Predicate> {
        val result = mutableListOf<Predicate>()
        getPredicate(criteriaBuilder, root, "name", name, FieldOperation.LIKE)?.let { result.add(it) }
        return result.toList()
    }

}
