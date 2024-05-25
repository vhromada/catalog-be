package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Episode
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.EpisodeFacade
import com.github.vhromada.catalog.mapper.EpisodeMapper
import com.github.vhromada.catalog.service.EpisodeService
import com.github.vhromada.catalog.service.SeasonService
import com.github.vhromada.catalog.service.ShowService
import com.github.vhromada.catalog.validator.EpisodeValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for episodes.
 *
 * @author Vladimir Hromada
 */
@Component("episodeFacade")
class EpisodeFacadeImpl(

    /**
     * Service for episodes
     */
    private val episodeService: EpisodeService,

    /**
     * Service for shows
     */
    private val showService: ShowService,

    /**
     * Service for seasons
     */
    private val seasonService: SeasonService,

    /**
     * Mapper for episodes
     */
    private val mapper: EpisodeMapper,

    /**
     * Validator for episodes
     */
    private val validator: EpisodeValidator

) : EpisodeFacade {

    override fun findAll(show: String, season: String, filter: PagingFilter): Page<Episode> {
        showService.get(uuid = show)
        val episodes = episodeService.search(season = seasonService.get(uuid = season).id!!, pageable = filter.toPageable(sort = Sort.by("number")))
        return Page(data = mapper.mapEpisodes(episodes.content), page = episodes)
    }

    override fun get(show: String, season: String, uuid: String): Episode {
        showService.get(uuid = show)
        seasonService.get(uuid = season)
        return mapper.mapEpisode(source = episodeService.get(uuid = uuid))
    }

    override fun add(show: String, season: String, request: ChangeEpisodeRequest): Episode {
        showService.get(uuid = show)
        val domainSeason = seasonService.get(uuid = season)
        validator.validateRequest(request = request)
        return mapper.mapEpisode(source = episodeService.store(episode = mapper.mapRequest(source = request).copy(season = domainSeason)))
    }

    override fun update(show: String, season: String, uuid: String, request: ChangeEpisodeRequest): Episode {
        showService.get(uuid = show)
        seasonService.get(uuid = season)
        validator.validateRequest(request = request)
        val episode = episodeService.get(uuid = uuid)
        episode.merge(episode = mapper.mapRequest(source = request))
        return mapper.mapEpisode(source = episodeService.store(episode = episode))
    }

    override fun remove(show: String, season: String, uuid: String) {
        showService.get(uuid = show)
        seasonService.get(uuid = season)
        episodeService.remove(episode = episodeService.get(uuid = uuid))
    }

    override fun duplicate(show: String, season: String, uuid: String): Episode {
        showService.get(uuid = show)
        seasonService.get(uuid = season)
        return mapper.mapEpisode(source = episodeService.duplicate(episode = episodeService.get(uuid = uuid)))
    }

}
