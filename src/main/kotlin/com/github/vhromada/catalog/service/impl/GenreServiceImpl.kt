package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.GenreRepository
import com.github.vhromada.catalog.service.GenreService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for genres.
 *
 * @author Vladimir Hromada
 */
@Service("genreService")
class GenreServiceImpl(

    /**
     * Repository for genres
     */
    private val repository: GenreRepository,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : GenreService {

    override fun search(filter: GenreFilter, pageable: Pageable): Page<Genre> {
        if (filter.isEmpty()) {
            return repository.findAll(pageable)
        }
        return repository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Genre {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "GENRE_NOT_EXIST", message = "Genre doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(genre: Genre): Genre {
        return repository.save(genre)
    }

    @Transactional
    override fun remove(genre: Genre) {
        repository.delete(genre)
    }

    @Transactional
    override fun duplicate(genre: Genre): Genre {
        return repository.save(genre.copy(id = null, uuid = uuidProvider.getUuid()))
    }

    override fun getCount(): Long {
        return repository.count()
    }

}
