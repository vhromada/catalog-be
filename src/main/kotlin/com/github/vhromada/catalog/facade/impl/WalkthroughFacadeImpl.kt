package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Walkthrough
import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.WalkthroughFacade
import com.github.vhromada.catalog.mapper.WalkthroughMapper
import com.github.vhromada.catalog.service.GameService
import com.github.vhromada.catalog.service.WalkthroughService
import com.github.vhromada.catalog.validator.WalkthroughValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for walkthroughs.
 *
 * @author Vladimir Hromada
 */
@Component("walkthroughFacade")
class WalkthroughFacadeImpl(

    /**
     * Service for walkthroughs
     */
    private val walkthroughService: WalkthroughService,

    /**
     * Service for games
     */
    private val gameService: GameService,

    /**
     * Mapper for walkthroughs
     */
    private val mapper: WalkthroughMapper,

    /**
     * Validator for walkthroughs
     */
    private val validator: WalkthroughValidator

) : WalkthroughFacade {

    override fun find(game: String): Walkthrough {
        return mapper.mapWalkthrough(source = walkthroughService.getByGame(game = gameService.get(uuid = game).id!!))
    }

    override fun get(game: String, uuid: String): Walkthrough {
        gameService.get(uuid = game)
        return mapper.mapWalkthrough(source = walkthroughService.getByUuid(uuid = uuid))
    }

    override fun add(game: String, request: ChangeWalkthroughRequest): Walkthrough {
        val domainGame = gameService.get(uuid = game)
        validator.validateRequest(request = request)
        if (domainGame.walkthrough != null) {
            throw InputException(key = "WALKTHROUGH_EXISTS", message = "Walkthrough already exists.")
        }
        val walkthrough = mapper.mapRequest(source = request)
        walkthrough.game = domainGame
        domainGame.walkthrough = walkthrough
        val result = mapper.mapWalkthrough(source = walkthroughService.store(walkthrough = walkthrough))
        gameService.store(game = domainGame)
        return result
    }

    override fun update(game: String, uuid: String, request: ChangeWalkthroughRequest): Walkthrough {
        gameService.get(uuid = game)
        validator.validateRequest(request = request)
        val walkthrough = walkthroughService.getByUuid(uuid = uuid)
        walkthrough.merge(walkthrough = mapper.mapRequest(source = request))
        return mapper.mapWalkthrough(source = walkthroughService.store(walkthrough = walkthrough))
    }

    override fun remove(game: String, uuid: String) {
        gameService.get(uuid = game)
        walkthroughService.remove(walkthrough = walkthroughService.getByUuid(uuid = uuid))
    }

}
