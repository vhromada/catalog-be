package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Author
import com.github.vhromada.catalog.domain.filter.AuthorFilter
import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest
import com.github.vhromada.catalog.mapper.AuthorMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for authors.
 *
 * @author Vladimir Hromada
 */
@Component("authorMapper")
class AuthorMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : AuthorMapper {

    override fun mapAuthor(source: Author): com.github.vhromada.catalog.entity.Author {
        return com.github.vhromada.catalog.entity.Author(
            uuid = source.uuid,
            firstName = source.firstName,
            middleName = source.middleName,
            lastName = source.lastName
        )
    }

    override fun mapAuthors(source: List<Author>): List<com.github.vhromada.catalog.entity.Author> {
        return source.map { mapAuthor(source = it) }
    }

    override fun mapRequest(source: ChangeAuthorRequest): Author {
        return Author(
            id = null,
            uuid = uuidProvider.getUuid(),
            firstName = source.firstName!!,
            normalizedFirstName = normalizerService.normalize(source = source.firstName),
            middleName = source.middleName,
            normalizedMiddleName = if (source.middleName.isNullOrBlank()) null else normalizerService.normalize(source = source.middleName),
            lastName = source.lastName!!,
            normalizedLastName = normalizerService.normalize(source = source.lastName)
        )
    }

    override fun mapFilter(source: com.github.vhromada.catalog.entity.filter.AuthorFilter): AuthorFilter {
        return AuthorFilter(
            firstName = source.firstName,
            middleName = source.middleName,
            lastName = source.lastName
        )
    }

}
