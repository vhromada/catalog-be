package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Episode
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.EpisodeRepository
import com.github.vhromada.catalog.repository.SeasonRepository
import com.github.vhromada.catalog.service.EpisodeService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for episodes.
 *
 * @author Vladimir Hromada
 */
@Service("episodeService")
class EpisodeServiceImpl(

    /**
     * Repository for episodes
     */
    private val episodeRepository: EpisodeRepository,

    /**
     * Repository for seasons
     */
    private val seasonRepository: SeasonRepository,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : EpisodeService {

    override fun search(season: Int, pageable: Pageable): Page<Episode> {
        return episodeRepository.findAllBySeasonId(id = season, pageable = pageable)
    }

    override fun get(uuid: String): Episode {
        return episodeRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "EPISODE_NOT_EXIST", message = "Episode doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(episode: Episode): Episode {
        return episodeRepository.save(episode)
    }

    @Transactional
    override fun remove(episode: Episode) {
        val season = episode.season!!
        season.episodes.remove(episode)
        seasonRepository.save(season)
    }

    @Transactional
    override fun duplicate(episode: Episode): Episode {
        val copy = episode.copy(id = null, uuid = uuidProvider.getUuid())
        copy.season!!.episodes.add(copy)
        return episodeRepository.save(copy)
    }

}
