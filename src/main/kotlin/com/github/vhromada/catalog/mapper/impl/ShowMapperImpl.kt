package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.domain.Show
import com.github.vhromada.catalog.domain.filter.ShowFilter
import com.github.vhromada.catalog.domain.io.EpisodeStatistics
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import com.github.vhromada.catalog.mapper.GenreMapper
import com.github.vhromada.catalog.mapper.ShowMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for shows.
 *
 * @author Vladimir Hromada
 */
@Component("showMapper")
class ShowMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Mapper for genres
     */
    private val genreMapper: GenreMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : ShowMapper {

    override fun mapShow(source: Show): com.github.vhromada.catalog.entity.Show {
        return com.github.vhromada.catalog.entity.Show(
            uuid = source.uuid,
            czechName = source.czechName,
            originalName = source.originalName,
            csfd = source.csfd,
            imdbCode = source.imdbCode,
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            picture = null,
            note = source.note,
            genres = genreMapper.mapGenres(source = source.genres),
            seasonsCount = source.seasons.size,
            episodesCount = source.seasons.sumOf { it.episodes.size },
            length = source.seasons.sumOf { it.episodes.sumOf { episode -> episode.length } }
        )
    }

    override fun mapShows(source: List<Show>): List<com.github.vhromada.catalog.entity.Show> {
        return source.map { mapShow(source = it) }
    }

    override fun mapRequest(source: ChangeShowRequest): Show {
        return Show(
            id = null,
            uuid = uuidProvider.getUuid(),
            czechName = source.czechName!!,
            normalizedCzechName = normalizerService.normalize(source = source.czechName),
            originalName = source.originalName!!,
            normalizedOriginalName = normalizerService.normalize(source = source.originalName),
            csfd = source.csfd,
            imdbCode = source.imdbCode,
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            picture = null,
            note = source.note,
            genres = mutableListOf(),
            seasons = mutableListOf()
        )
    }

    override fun mapFilter(source: MultipleNameFilter): ShowFilter {
        return ShowFilter(
            czechName = source.czechName,
            originalName = source.originalName
        )
    }

    override fun mapStatistics(showsCount: Long, seasonCount: Long, episodeStatistics: EpisodeStatistics): ShowStatistics {
        return ShowStatistics(
            count = showsCount.toInt(),
            seasonsCount = seasonCount.toInt(),
            episodesCount = episodeStatistics.count.toInt(),
            length = Time(length = if (episodeStatistics.length == null) 0 else episodeStatistics.length.toInt()).toString()
        )
    }

}
