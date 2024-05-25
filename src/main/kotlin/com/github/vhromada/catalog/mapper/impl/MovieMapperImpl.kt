package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.domain.Movie
import com.github.vhromada.catalog.domain.filter.MovieFilter
import com.github.vhromada.catalog.domain.io.MediaStatistics
import com.github.vhromada.catalog.entity.MovieStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeMovieRequest
import com.github.vhromada.catalog.mapper.GenreMapper
import com.github.vhromada.catalog.mapper.MediumMapper
import com.github.vhromada.catalog.mapper.MovieMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for movies.
 *
 * @author Vladimir Hromada
 */
@Component("movieMapper")
class MovieMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Mapper for media
     */
    private val mediumMapper: MediumMapper,

    /**
     * Mapper for genres
     */
    private val genreMapper: GenreMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : MovieMapper {

    override fun mapMovie(source: Movie): com.github.vhromada.catalog.entity.Movie {
        return com.github.vhromada.catalog.entity.Movie(
            uuid = source.uuid,
            czechName = source.czechName,
            originalName = source.originalName,
            year = source.year,
            languages = source.languages,
            subtitles = source.subtitles,
            media = mediumMapper.mapMedia(source = source.media),
            csfd = source.csfd,
            imdbCode = source.imdbCode,
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            picture = null,
            note = source.note,
            genres = genreMapper.mapGenres(source = source.genres)
        )
    }

    override fun mapMovies(source: List<Movie>): List<com.github.vhromada.catalog.entity.Movie> {
        return source.map { mapMovie(source = it) }
    }

    override fun mapRequest(source: ChangeMovieRequest): Movie {
        return Movie(
            id = null,
            uuid = uuidProvider.getUuid(),
            czechName = source.czechName!!,
            normalizedCzechName = normalizerService.normalize(source = source.czechName),
            originalName = source.originalName!!,
            normalizedOriginalName = normalizerService.normalize(source = source.originalName),
            year = source.year!!,
            languages = source.languages!!.filterNotNull().toMutableList(),
            subtitles = source.subtitles!!.filterNotNull().toMutableList(),
            media = mutableListOf(),
            csfd = source.csfd,
            imdbCode = source.imdbCode,
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            picture = null,
            note = source.note,
            genres = mutableListOf()
        )
    }

    override fun mapFilter(source: MultipleNameFilter): MovieFilter {
        return MovieFilter(
            czechName = source.czechName,
            originalName = source.originalName
        )
    }

    override fun mapStatistics(count: Long, mediaStatistics: MediaStatistics): MovieStatistics {
        return MovieStatistics(
            count = count.toInt(),
            mediaCount = mediaStatistics.count.toInt(),
            length = Time(length = if (mediaStatistics.length == null) 0 else mediaStatistics.length.toInt()).toString()
        )
    }

}
