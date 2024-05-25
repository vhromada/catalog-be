package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Season
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest

/**
 * An interface represents mapper for seasons.
 *
 * @author Vladimir Hromada
 */
interface SeasonMapper {

    /**
     * Maps season.
     *
     * @param source season
     * @return mapped season
     */
    fun mapSeason(source: Season): com.github.vhromada.catalog.entity.Season

    /**
     * Maps list of seasons.
     *
     * @param source list of seasons
     * @return mapped list of seasons
     */
    fun mapSeasons(source: List<Season>): List<com.github.vhromada.catalog.entity.Season>

    /**
     * Maps request for changing season.
     *
     * @param source request for changing season
     * @return mapped season
     */
    fun mapRequest(source: ChangeSeasonRequest): Season

}
