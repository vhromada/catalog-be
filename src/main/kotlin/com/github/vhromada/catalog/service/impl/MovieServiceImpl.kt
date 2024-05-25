package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Movie
import com.github.vhromada.catalog.domain.filter.MovieFilter
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.MovieMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.MovieRepository
import com.github.vhromada.catalog.service.MovieService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for movies.
 *
 * @author Vladimir Hromada
 */
@Service("movieService")
class MovieServiceImpl(

    /**
     * Repository for movies
     */
    private val repository: MovieRepository,

    /**
     * Mapper for movies
     */
    private val mapper: MovieMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : MovieService {

    override fun search(filter: MovieFilter, pageable: Pageable): Page<Movie> {
        if (filter.isEmpty()) {
            return repository.findAll(pageable)
        }
        return repository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Movie {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "MOVIE_NOT_EXIST", message = "Movie doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(movie: Movie): Movie {
        return repository.save(movie)
    }

    @Transactional
    override fun remove(movie: Movie) {
        repository.delete(movie)
    }

    @Transactional
    override fun duplicate(movie: Movie): Movie {
        val copy = movie.copy(
            id = null,
            uuid = uuidProvider.getUuid(),
            languages = movie.languages.map { it }.toMutableList(),
            subtitles = movie.subtitles.map { it }.toMutableList(),
            media = movie.media.map { it.copy(id = null) }.toMutableList(),
            genres = movie.genres.map { it }.toMutableList()
        )
        return repository.save(copy)
    }

    override fun getStatistics(): MovieStatistics {
        val count = repository.count()
        val mediaStatistics = repository.getMediaStatistics()
        return mapper.mapStatistics(count = count, mediaStatistics = mediaStatistics)
    }

}
