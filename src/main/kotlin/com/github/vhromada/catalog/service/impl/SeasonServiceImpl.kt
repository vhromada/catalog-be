package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.domain.Season
import com.github.vhromada.catalog.repository.SeasonRepository
import com.github.vhromada.catalog.repository.ShowRepository
import com.github.vhromada.catalog.service.SeasonService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for seasons.
 *
 * @author Vladimir Hromada
 */
@Service("seasonService")
class SeasonServiceImpl(

    /**
     * Repository for seasons
     */
    private val seasonRepository: SeasonRepository,

    /**
     * Repository for shows
     */
    private val showRepository: ShowRepository,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : SeasonService {

    override fun search(show: Int, pageable: Pageable): Page<Season> {
        return seasonRepository.findAllByShowId(id = show, pageable = pageable)
    }

    override fun get(uuid: String): Season {
        return seasonRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "SEASON_NOT_EXIST", message = "Season doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(season: Season): Season {
        return seasonRepository.save(season)
    }

    @Transactional
    override fun remove(season: Season) {
        val show = season.show!!
        show.seasons.remove(season)
        showRepository.save(show)
    }

    @Transactional
    override fun duplicate(season: Season): Season {
        val copy = season.copy(id = null, uuid = uuidProvider.getUuid(), subtitles = season.subtitles.map { it }.toMutableList(), episodes = mutableListOf())
        copy.episodes.addAll(season.episodes.map { it.copy(id = null, uuid = uuidProvider.getUuid(), season = copy) })
        copy.show!!.seasons.add(copy)
        return seasonRepository.save(copy)
    }

}
