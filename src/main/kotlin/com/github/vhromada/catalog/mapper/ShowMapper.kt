package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Show
import com.github.vhromada.catalog.domain.filter.ShowFilter
import com.github.vhromada.catalog.domain.io.EpisodeStatistics
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeShowRequest

/**
 * An interface represents mapper for shows.
 *
 * @author Vladimir Hromada
 */
interface ShowMapper {

    /**
     * Maps show.
     *
     * @param source show
     * @return mapped show
     */
    fun mapShow(source: Show): com.github.vhromada.catalog.entity.Show

    /**
     * Maps list of shows.
     *
     * @param source list of shows
     * @return mapped list of shows
     */
    fun mapShows(source: List<Show>): List<com.github.vhromada.catalog.entity.Show>

    /**
     * Maps request for changing show.
     *
     * @param source request for changing show
     * @return mapped show
     */
    fun mapRequest(source: ChangeShowRequest): Show

    /**
     * Maps filter for shows.
     *
     * @param source filter for multiple names
     * @return mapped filter for shows
     */
    fun mapFilter(source: MultipleNameFilter): ShowFilter

    /**
     * Maps statistics.
     *
     * @param showsCount        count of shows
     * @param seasonCount       count of seasons
     * @param episodeStatistics statistics for songs
     * @return statistics for shows
     */
    fun mapStatistics(showsCount: Long, seasonCount: Long, episodeStatistics: EpisodeStatistics): ShowStatistics

}
