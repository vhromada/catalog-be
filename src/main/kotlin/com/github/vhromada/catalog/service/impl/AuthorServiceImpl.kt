package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Author
import com.github.vhromada.catalog.domain.filter.AuthorFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.AuthorRepository
import com.github.vhromada.catalog.service.AuthorService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for authors.
 *
 * @author Vladimir Hromada
 */
@Service("authorService")
class AuthorServiceImpl(

    /**
     * Repository for authors
     */
    private val repository: AuthorRepository,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : AuthorService {

    override fun search(filter: AuthorFilter, pageable: Pageable): Page<Author> {
        if (filter.isEmpty()) {
            return repository.findAll(pageable)
        }
        return repository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Author {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "AUTHOR_NOT_EXIST", message = "Author doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(author: Author): Author {
        return repository.save(author)
    }

    @Transactional
    override fun remove(author: Author) {
        repository.delete(author)
    }

    @Transactional
    override fun duplicate(author: Author): Author {
        return repository.save(author.copy(id = null, uuid = uuidProvider.getUuid()))
    }

    override fun getCount(): Long {
        return repository.count()
    }

}
