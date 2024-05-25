package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Author
import com.github.vhromada.catalog.entity.AuthorStatistics
import com.github.vhromada.catalog.entity.filter.AuthorFilter
import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.AuthorFacade
import com.github.vhromada.catalog.mapper.AuthorMapper
import com.github.vhromada.catalog.service.AuthorService
import com.github.vhromada.catalog.validator.AuthorValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for authors.
 *
 * @author Vladimir Hromada
 */
@Component("authorFacade")
class AuthorFacadeImpl(

    /**
     * Service for authors
     */
    private val service: AuthorService,

    /**
     * Mapper for authors
     */
    private val mapper: AuthorMapper,

    /**
     * Validator for authors
     */
    private val validator: AuthorValidator

) : AuthorFacade {

    override fun search(filter: AuthorFilter): Page<Author> {
        val authors = service.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedLastName", "normalizedFirstName", "id")))
        return Page(data = mapper.mapAuthors(source = authors.content), page = authors)
    }

    override fun get(uuid: String): Author {
        return mapper.mapAuthor(source = service.get(uuid = uuid))
    }

    override fun add(request: ChangeAuthorRequest): Author {
        validator.validateRequest(request = request)
        return mapper.mapAuthor(source = service.store(author = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangeAuthorRequest): Author {
        validator.validateRequest(request = request)
        val author = service.get(uuid = uuid)
        author.merge(author = mapper.mapRequest(source = request))
        return mapper.mapAuthor(source = service.store(author = author))
    }

    override fun remove(uuid: String) {
        service.remove(author = service.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Author {
        return mapper.mapAuthor(source = service.duplicate(author = service.get(uuid = uuid)))
    }

    override fun getStatistics(): AuthorStatistics {
        return AuthorStatistics(count = service.getCount().toInt())
    }

}
