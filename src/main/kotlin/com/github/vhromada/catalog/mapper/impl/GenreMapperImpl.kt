package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.mapper.GenreMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for genres.
 *
 * @author Vladimir Hromada
 */
@Component("genreMapper")
class GenreMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : GenreMapper {

    override fun mapGenre(source: Genre): com.github.vhromada.catalog.entity.Genre {
        return com.github.vhromada.catalog.entity.Genre(
            uuid = source.uuid,
            name = source.name
        )
    }

    override fun mapGenres(source: List<Genre>): List<com.github.vhromada.catalog.entity.Genre> {
        return source.map { mapGenre(source = it) }
    }

    override fun mapRequest(source: ChangeGenreRequest): Genre {
        return Genre(
            id = null,
            uuid = uuidProvider.getUuid(),
            name = source.name!!,
            normalizedName = normalizerService.normalize(source = source.name)
        )
    }

    override fun mapFilter(source: NameFilter): GenreFilter {
        return GenreFilter(name = source.name)
    }

}
