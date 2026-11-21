package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Handbook
import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.HandbookFacade
import com.github.vhromada.catalog.mapper.HandbookMapper
import com.github.vhromada.catalog.service.GameService
import com.github.vhromada.catalog.service.HandbookService
import com.github.vhromada.catalog.validator.HandbookValidator
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for handbooks.
 *
 * @author Vladimir Hromada
 */
@Component("handbookFacade")
class HandbookFacadeImpl(

    /**
     * Service for handbooks
     */
    private val handbookService: HandbookService,

    /**
     * Service for games
     */
    private val gameService: GameService,

    /**
     * Mapper for handbooks
     */
    private val mapper: HandbookMapper,

    /**
     * Validator for handbooks
     */
    private val validator: HandbookValidator

) : HandbookFacade {

    override fun find(game: String): Handbook {
        return mapper.mapHandbook(source = handbookService.getByGame(game = gameService.get(uuid = game).id!!))
    }

    override fun get(game: String, uuid: String): Handbook {
        gameService.get(uuid = game)
        return mapper.mapHandbook(source = handbookService.getByUuid(uuid = uuid))
    }

    override fun add(game: String, request: ChangeHandbookRequest): Handbook {
        val domainGame = gameService.get(uuid = game)
        validator.validateRequest(request = request)
        if (domainGame.handbook != null) {
            throw InputException(key = "HANDBOOK_EXISTS", message = "Handbook already exists.")
        }
        val handbook = mapper.mapRequest(source = request)
        handbook.game = domainGame
        domainGame.handbook = handbook
        val result = mapper.mapHandbook(source = handbookService.store(handbook = handbook))
        gameService.store(game = domainGame)
        return result
    }

    override fun update(game: String, uuid: String, request: ChangeHandbookRequest): Handbook {
        gameService.get(uuid = game)
        validator.validateRequest(request = request)
        val handbook = handbookService.getByUuid(uuid = uuid)
        handbook.merge(handbook = mapper.mapRequest(source = request))
        return mapper.mapHandbook(source = handbookService.store(handbook = handbook))
    }

    override fun remove(game: String, uuid: String) {
        gameService.get(uuid = game)
        handbookService.remove(handbook = handbookService.getByUuid(uuid = uuid))
    }

}
