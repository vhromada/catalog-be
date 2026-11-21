package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Handbook
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.repository.HandbookRepository
import com.github.vhromada.catalog.service.HandbookService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for handbooks.
 *
 * @author Vladimir Hromada
 */
@Service("handbookService")
class HandbookServiceImpl(

    /**
     * Repository for handbooks
     */
    private val handbookRepository: HandbookRepository,

    /**
     * Repository for games
     */
    private val gameRepository: GameRepository

) : HandbookService {

    override fun getByGame(game: Int): Handbook {
        return handbookRepository.findByGameId(id = game)
            .orElseThrow { InputException(key = "HANDBOOK_NOT_EXIST", message = "Handbook doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    override fun getByUuid(uuid: String): Handbook {
        return handbookRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "HANDBOOK_NOT_EXIST", message = "Handbook doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(handbook: Handbook): Handbook {
        return handbookRepository.save(handbook)
    }

    @Transactional
    override fun remove(handbook: Handbook) {
        val game = handbook.game!!
        game.handbook = null
        gameRepository.save(game)
    }

}
