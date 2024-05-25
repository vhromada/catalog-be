package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Show
import com.github.vhromada.catalog.domain.filter.ShowFilter
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.ShowMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.EpisodeRepository
import com.github.vhromada.catalog.repository.SeasonRepository
import com.github.vhromada.catalog.repository.ShowRepository
import com.github.vhromada.catalog.service.ShowService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for shows.
 *
 * @author Vladimir Hromada
 */
@Service("showService")
class ShowServiceImpl(

    /**
     * Repository for shows
     */
    private val showRepository: ShowRepository,

    /**
     * Repository for seasons
     */
    private val seasonRepository: SeasonRepository,

    /**
     * Repository for episodes
     */
    private val episodeRepository: EpisodeRepository,

    /**
     * Mapper for shows
     */
    private val mapper: ShowMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : ShowService {

    override fun search(filter: ShowFilter, pageable: Pageable): Page<Show> {
        if (filter.isEmpty()) {
            return showRepository.findAll(pageable)
        }
        return showRepository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Show {
        return showRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "SHOW_NOT_EXIST", message = "Show doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(show: Show): Show {
        return showRepository.save(show)
    }

    @Transactional
    override fun remove(show: Show) {
        showRepository.delete(show)
    }

    @Transactional
    override fun duplicate(show: Show): Show {
        val copy = show.copy(id = null, uuid = uuidProvider.getUuid(), genres = show.genres.map { it }.toMutableList(), seasons = mutableListOf())
        copy.seasons.addAll(show.seasons.map {
            val season = it.copy(id = null, uuid = uuidProvider.getUuid(), subtitles = it.subtitles.map { subtitle -> subtitle }.toMutableList(), episodes = mutableListOf(), show = copy)
            season.episodes.addAll(it.episodes.map { episode -> episode.copy(id = null, uuid = uuidProvider.getUuid(), season = season) })
            season
        })
        return showRepository.save(copy)
    }

    override fun getStatistics(): ShowStatistics {
        val showsCount = showRepository.count()
        val seasonsCount = seasonRepository.count()
        val episodeStatistics = episodeRepository.getStatistics()
        return mapper.mapStatistics(showsCount = showsCount, seasonCount = seasonsCount, episodeStatistics = episodeStatistics)
    }

}
