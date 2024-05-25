package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Cheat
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.CheatRepository
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.service.CheatService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for cheats.
 *
 * @author Vladimir Hromada
 */
@Service("cheatService")
class CheatServiceImpl(

    /**
     * Repository for cheats
     */
    private val cheatRepository: CheatRepository,

    /**
     * Repository for games
     */
    private val gameRepository: GameRepository

) : CheatService {

    override fun getByGame(game: Int): Cheat {
        return cheatRepository.findByGameId(id = game)
            .orElseThrow { InputException(key = "CHEAT_NOT_EXIST", message = "Cheat doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    override fun getByUuid(uuid: String): Cheat {
        return cheatRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "CHEAT_NOT_EXIST", message = "Cheat doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(cheat: Cheat): Cheat {
        return cheatRepository.save(cheat)
    }

    @Transactional
    override fun remove(cheat: Cheat) {
        val game = cheat.game!!
        game.cheat = null
        gameRepository.save(game)
    }

}
