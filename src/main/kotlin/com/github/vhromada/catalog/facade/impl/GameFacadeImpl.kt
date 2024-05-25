package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Game
import com.github.vhromada.catalog.entity.GameStatistics
import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGameRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.GameFacade
import com.github.vhromada.catalog.mapper.GameMapper
import com.github.vhromada.catalog.service.GameService
import com.github.vhromada.catalog.service.RegisterService
import com.github.vhromada.catalog.validator.GameValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for games.
 *
 * @author Vladimir Hromada
 */
@Component("gameFacade")
class GameFacadeImpl(

    /**
     * Service for games
     */
    private val gameService: GameService,

    /**
     * Service for registers
     */
    private val registerService: RegisterService,

    /**
     * Mapper for games
     */
    private val mapper: GameMapper,

    /**
     * Validator for games
     */
    private val validator: GameValidator

) : GameFacade {

    override fun search(filter: NameFilter): Page<Game> {
        val games = gameService.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedName", "id")))
        return Page(data = mapper.mapGames(source = games.content), page = games)
    }

    override fun get(uuid: String): Game {
        return mapper.mapGame(source = gameService.get(uuid = uuid))
    }

    override fun add(request: ChangeGameRequest): Game {
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.PROGRAM_FORMATS, code = request.format!!)
        return mapper.mapGame(source = gameService.store(game = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangeGameRequest): Game {
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.PROGRAM_FORMATS, code = request.format!!)
        val game = gameService.get(uuid = uuid)
        game.merge(game = mapper.mapRequest(source = request))
        return mapper.mapGame(source = gameService.store(game = game))
    }

    override fun remove(uuid: String) {
        gameService.remove(game = gameService.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Game {
        return mapper.mapGame(source = gameService.duplicate(game = gameService.get(uuid = uuid)))
    }

    override fun getStatistics(): GameStatistics {
        return gameService.getStatistics()
    }

}
