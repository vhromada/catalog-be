package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.domain.CheatData
import com.github.vhromada.catalog.entity.Cheat
import com.github.vhromada.catalog.entity.io.ChangeCheatData
import com.github.vhromada.catalog.entity.io.ChangeCheatRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.CheatFacade
import com.github.vhromada.catalog.mapper.CheatDataMapper
import com.github.vhromada.catalog.mapper.CheatMapper
import com.github.vhromada.catalog.service.CheatService
import com.github.vhromada.catalog.service.GameService
import com.github.vhromada.catalog.validator.CheatValidator
import org.springframework.stereotype.Component
import kotlin.math.min

/**
 * A class represents implementation of facade for cheats.
 *
 * @author Vladimir Hromada
 */
@Component("cheatFacade")
class CheatFacadeImpl(

    /**
     * Service for cheats
     */
    private val cheatService: CheatService,

    /**
     * Service for games
     */
    private val gameService: GameService,

    /**
     * Mapper for cheats
     */
    private val cheatMapper: CheatMapper,

    /**
     * Mapper for cheat's data
     */
    private val cheatDataMapper: CheatDataMapper,

    /**
     * Validator for cheats
     */
    private val validator: CheatValidator

) : CheatFacade {

    override fun find(game: String): Cheat {
        return cheatMapper.mapCheat(source = cheatService.getByGame(game = gameService.get(uuid = game).id!!))
    }

    override fun get(game: String, uuid: String): Cheat {
        gameService.get(uuid = game)
        return cheatMapper.mapCheat(source = cheatService.getByUuid(uuid = uuid))
    }

    override fun add(game: String, request: ChangeCheatRequest): Cheat {
        val domainGame = gameService.get(uuid = game)
        validator.validateRequest(request = request)
        if (domainGame.cheat != null) {
            throw InputException(key = "CHEAT_EXISTS", message = "Cheat already exists.")
        }
        val cheat = cheatMapper.mapRequest(source = request)
        cheat.game = domainGame
        domainGame.cheat = cheat
        val result = cheatMapper.mapCheat(source = cheatService.store(cheat = cheat))
        gameService.store(game = domainGame)
        return result
    }

    override fun update(game: String, uuid: String, request: ChangeCheatRequest): Cheat {
        gameService.get(uuid = game)
        validator.validateRequest(request = request)
        val cheat = cheatService.getByUuid(uuid = uuid)
        cheat.merge(cheat = cheatMapper.mapRequest(source = request))
        val updatedCheatData = getUpdatedCheatData(originalCheatData = cheat.data, updatedCheatData = request.data!!.filterNotNull())
        cheat.data.clear()
        cheat.data.addAll(updatedCheatData)
        return cheatMapper.mapCheat(source = cheatService.store(cheat = cheat))
    }

    override fun remove(game: String, uuid: String) {
        gameService.get(uuid = game)
        cheatService.remove(cheat = cheatService.getByUuid(uuid = uuid))
    }

    /**
     * Updates cheat's data.
     *
     * @param originalCheatData original cheat's data.
     * @param updatedCheatData  updated cheat's data.
     * @return updated cheat's data
     */
    private fun getUpdatedCheatData(originalCheatData: List<CheatData>, updatedCheatData: List<ChangeCheatData>): List<CheatData> {
        val result = mutableListOf<CheatData>()

        var index = 0
        val max = min(originalCheatData.size, updatedCheatData.size)
        while (index < max) {
            val cheatData = originalCheatData[index]
            cheatData.merge(cheatDataMapper.mapRequest(source = updatedCheatData[index]))
            result.add(cheatData)
            index++
        }
        while (index < updatedCheatData.size) {
            result.add(cheatDataMapper.mapRequest(source = updatedCheatData[index]))
            index++
        }

        return result
    }

}
