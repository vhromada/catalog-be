package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.domain.Season
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest
import com.github.vhromada.catalog.mapper.SeasonMapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for seasons.
 *
 * @author Vladimir Hromada
 */
@Component("seasonMapper")
class SeasonMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : SeasonMapper {

    override fun mapSeason(source: Season): com.github.vhromada.catalog.entity.Season {
        return com.github.vhromada.catalog.entity.Season(
            uuid = source.uuid,
            number = source.number,
            startYear = source.startYear,
            endYear = source.endYear,
            language = source.language,
            subtitles = source.subtitles,
            note = source.note,
            episodesCount = source.episodes.size,
            length = source.episodes.sumOf { it.length }
        )
    }

    override fun mapSeasons(source: List<Season>): List<com.github.vhromada.catalog.entity.Season> {
        return source.map { mapSeason(source = it) }
    }

    override fun mapRequest(source: ChangeSeasonRequest): Season {
        return Season(
            id = null,
            uuid = uuidProvider.getUuid(),
            number = source.number!!,
            startYear = source.startYear!!,
            endYear = source.endYear!!,
            language = source.language!!,
            subtitles = source.subtitles!!.filterNotNull().toMutableList(),
            note = source.note,
            episodes = mutableListOf()
        )
    }

}
