package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Episode
import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest

/**
 * An interface represents mapper for episodes.
 *
 * @author Vladimir Hromada
 */
interface EpisodeMapper {

    /**
     * Maps episode.
     *
     * @param source episode
     * @return mapped episode
     */
    fun mapEpisode(source: Episode): com.github.vhromada.catalog.entity.Episode

    /**
     * Maps list of episodes.
     *
     * @param source list of episodes
     * @return mapped list of episodes
     */
    fun mapEpisodes(source: List<Episode>): List<com.github.vhromada.catalog.entity.Episode>

    /**
     * Maps request for changing episode.
     *
     * @param source request for changing episode
     * @return mapped episode
     */
    fun mapRequest(source: ChangeEpisodeRequest): Episode

}
