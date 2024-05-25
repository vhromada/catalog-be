package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.domain.Medium
import com.github.vhromada.catalog.entity.Movie
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.MovieFacade
import com.github.vhromada.catalog.mapper.MovieMapper
import com.github.vhromada.catalog.service.GenreService
import com.github.vhromada.catalog.service.MovieService
import com.github.vhromada.catalog.service.PictureService
import com.github.vhromada.catalog.service.RegisterService
import com.github.vhromada.catalog.validator.MovieValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import kotlin.math.min

/**
 * A class represents implementation of facade for movies.
 *
 * @author Vladimir Hromada
 */
@Component("movieFacade")
class MovieFacadeImpl(

    /**
     * Service for movies
     */
    private val movieService: MovieService,

    /**
     * Service for genres
     */
    private val genreService: GenreService,

    /**
     * Service for pictures
     */
    private val pictureService: PictureService,

    /**
     * Service for registers
     */
    private val registerService: RegisterService,

    /**
     * Mapper for movies
     */
    private val mapper: MovieMapper,

    /**
     * Validator for movies
     */
    private val validator: MovieValidator

) : MovieFacade {

    override fun search(filter: MultipleNameFilter): Page<Movie> {
        val movies = movieService.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedCzechName", "id")))
        val data = movies.content.map {
            mapper.mapMovie(source = it)
                .copy(picture = getPicture(movie = it)?.uuid)
        }
        return Page(data = data, page = movies)
    }

    override fun get(uuid: String): Movie {
        val movie = movieService.get(uuid = uuid)
        return mapper.mapMovie(source = movie)
            .copy(picture = getPicture(movie = movie)?.uuid)
    }

    override fun add(request: ChangeMovieRequest): Movie {
        validator.validateRequest(request = request)
        request.languages!!.filterNotNull().forEach { registerService.checkValue(type = RegisterType.LANGUAGES, code = it) }
        request.subtitles!!.filterNotNull().forEach { registerService.checkValue(type = RegisterType.SUBTITLES, code = it) }
        val movie = mapper.mapRequest(source = request)
        movie.media.addAll(request.media!!.filterNotNull().mapIndexed { index, medium -> Medium(id = null, number = index + 1, length = medium) })
        movie.picture = getPicture(request = request)?.id
        movie.genres.addAll(getGenres(request = request))
        val addedMovie = movieService.store(movie = movie)
        return mapper.mapMovie(source = addedMovie)
            .copy(picture = getPicture(movie = addedMovie)?.uuid)
    }

    @Suppress("DuplicatedCode")
    override fun update(uuid: String, request: ChangeMovieRequest): Movie {
        validator.validateRequest(request = request)
        request.languages!!.filterNotNull().forEach { registerService.checkValue(type = RegisterType.LANGUAGES, code = it) }
        request.subtitles!!.filterNotNull().forEach { registerService.checkValue(type = RegisterType.SUBTITLES, code = it) }
        val movie = movieService.get(uuid = uuid)
        movie.merge(movie = mapper.mapRequest(source = request))
        val updatedMedia = getUpdatedMedia(originalMedia = movie.media, updatedMedia = request.media!!.filterNotNull())
        movie.media.clear()
        movie.media.addAll(updatedMedia)
        movie.picture = getPicture(request = request)?.id
        movie.genres.clear()
        movie.genres.addAll(getGenres(request = request))
        val updatedMovie = movieService.store(movie = movie)
        return mapper.mapMovie(source = updatedMovie)
            .copy(picture = getPicture(movie = updatedMovie)?.uuid)
    }

    override fun remove(uuid: String) {
        movieService.remove(movie = movieService.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Movie {
        val duplicatedMovie = movieService.duplicate(movie = movieService.get(uuid = uuid))
        return mapper.mapMovie(source = duplicatedMovie)
            .copy(picture = getPicture(movie = duplicatedMovie)?.uuid)
    }

    override fun getStatistics(): MovieStatistics {
        return movieService.getStatistics()
    }

    /**
     * Returns picture.
     *
     * @param movie movie
     * @returns picture
     */
    private fun getPicture(movie: com.github.vhromada.catalog.domain.Movie): com.github.vhromada.catalog.domain.Picture? {
        return if (movie.picture == null) null else pictureService.getById(id = movie.picture!!)
    }

    /**
     * Returns picture.
     *
     * @param request request for changing movie
     * @returns picture
     * @throws InputException if picture doesn't exist in data storage
     */
    private fun getPicture(request: ChangeMovieRequest): com.github.vhromada.catalog.domain.Picture? {
        return if (request.picture == null) null else pictureService.getByUuid(uuid = request.picture)
    }

    /**
     * Returns genres.
     *
     * @param request request for changing movie
     * @returns genres
     * @throws InputException if genre doesn't exist in data storage
     */
    private fun getGenres(request: ChangeMovieRequest): List<com.github.vhromada.catalog.domain.Genre> {
        return request.genres!!.filterNotNull().map { genreService.get(uuid = it) }
    }

    /**
     * Updates media.
     *
     * @param originalMedia original media
     * @param updatedMedia  updated media
     * @return updated media
     */
    private fun getUpdatedMedia(originalMedia: List<Medium>, updatedMedia: List<Int>): List<Medium> {
        val result = mutableListOf<Medium>()

        var index = 0
        val max = min(originalMedia.size, updatedMedia.size)
        while (index < max) {
            val medium = originalMedia[index]
            medium.number = index + 1
            medium.length = updatedMedia[index]
            result.add(medium)
            index++
        }
        while (index < updatedMedia.size) {
            val medium = Medium(id = null, number = index + 1, length = updatedMedia[index])
            result.add(medium)
            index++
        }

        return result
    }

}
