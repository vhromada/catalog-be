package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Genre
import com.github.vhromada.catalog.entity.GenreStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGenreRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.GenreFacade
import com.github.vhromada.catalog.mapper.GenreMapper
import com.github.vhromada.catalog.service.GenreService
import com.github.vhromada.catalog.validator.GenreValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for genres.
 *
 * @author Vladimir Hromada
 */
@Component("genreFacade")
class GenreFacadeImpl(

    /**
     * Service for genres
     */
    private val service: GenreService,

    /**
     * Mapper for genres
     */
    private val mapper: GenreMapper,

    /**
     * Validator for genres
     */
    private val validator: GenreValidator

) : GenreFacade {

    override fun search(filter: NameFilter): Page<Genre> {
        val genres = service.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedName", "id")))
        return Page(data = mapper.mapGenres(source = genres.content), page = genres)
    }

    override fun get(uuid: String): Genre {
        return mapper.mapGenre(source = service.get(uuid = uuid))
    }

    override fun add(request: ChangeGenreRequest): Genre {
        validator.validateRequest(request = request)
        return mapper.mapGenre(source = service.store(genre = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangeGenreRequest): Genre {
        validator.validateRequest(request = request)
        val genre = service.get(uuid = uuid)
        genre.merge(genre = mapper.mapRequest(source = request))
        return mapper.mapGenre(source = service.store(genre = genre))
    }

    override fun remove(uuid: String) {
        service.remove(genre = service.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Genre {
        return mapper.mapGenre(source = service.duplicate(genre = service.get(uuid = uuid)))
    }

    override fun getStatistics(): GenreStatistics {
        return GenreStatistics(count = service.getCount().toInt())
    }

}
