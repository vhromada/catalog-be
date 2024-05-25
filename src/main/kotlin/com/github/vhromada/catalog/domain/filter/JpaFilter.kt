package com.github.vhromada.catalog.domain.filter

import com.github.vhromada.catalog.common.FieldOperation
import com.github.vhromada.catalog.exception.InputException
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus

/**
 * An abstract class represents filter for JPA.
 *
 * @param T type of data
 * @author Vladimir Hromada
 */
abstract class JpaFilter<T> {

    /**
     * Converts filter to specification.
     *
     * @return specification
     * @throws InputException if filter is empty
     */
    fun toSpecification(): Specification<T> {
        assertNotEmpty()
        return Specification { root, query, criteriaBuilder -> criteriaBuilder.and(*process(root, query, criteriaBuilder).toTypedArray()) }
    }

    /**
     * Returns true if filter is empty.
     *
     * @return true if filter is empty
     */
    abstract fun isEmpty(): Boolean

    /**
     * Creates specification from filter.
     */
    protected abstract fun process(root: Root<T>, query: CriteriaQuery<*>, criteriaBuilder: CriteriaBuilder): List<Predicate>

    /**
     * Checks if filter is not empty.
     *
     * @throws InputException if filter is empty
     */
    fun assertNotEmpty() {
        if (isEmpty()) {
            throw InputException("EMPTY_FILTER", "Filter cannot be empty.", HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }

    /**
     * Created predicate for field and operation.
     *
     * @param criteriaBuilder criteria builder
     * @param root root
     * @param path field's path
     * @param field field
     * @param operation operation
     * @return predicate
     */
    protected fun getPredicate(criteriaBuilder: CriteriaBuilder, root: Root<T>, path: String, field: String?, operation: FieldOperation? = null): Predicate? {
        return if (!field.isNullOrBlank()) {
            if (operation == null || operation == FieldOperation.EQ) {
                criteriaBuilder.equal(root.get<String>(path), field)
            } else {
                criteriaBuilder.like(criteriaBuilder.lower(root.get(path)), "%${field.lowercase()}%")
            }
        } else {
            null
        }
    }

}
