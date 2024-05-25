package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Game
import com.github.vhromada.catalog.domain.filter.GameFilter
import com.github.vhromada.catalog.entity.GameStatistics
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.GameMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.service.GameService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for games.
 *
 * @author Vladimir Hromada
 */
@Service("gameService")
class GameServiceImpl(

    /**
     * Repository for games
     */
    private val repository: GameRepository,

    /**
     * Mapper for games
     */
    private val mapper: GameMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : GameService {

    override fun search(filter: GameFilter, pageable: Pageable): Page<Game> {
        if (filter.isEmpty()) {
            return repository.findAll(pageable)
        }
        return repository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Game {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "GAME_NOT_EXIST", message = "Game doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(game: Game): Game {
        return repository.save(game)
    }

    @Transactional
    override fun remove(game: Game) {
        repository.delete(game)
    }

    @Transactional
    override fun duplicate(game: Game): Game {
        return repository.save(game.copy(id = null, uuid = uuidProvider.getUuid(), cheat = null))
    }

    override fun getStatistics(): GameStatistics {
        return mapper.mapStatistics(source = repository.getStatistics())
    }

}
