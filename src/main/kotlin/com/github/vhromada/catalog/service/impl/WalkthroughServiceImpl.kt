package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Walkthrough
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.repository.WalkthroughRepository
import com.github.vhromada.catalog.service.WalkthroughService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for walkthroughs.
 *
 * @author Vladimir Hromada
 */
@Service("walkthroughService")
class WalkthroughServiceImpl(

    /**
     * Repository for walkthroughs
     */
    private val walkthroughRepository: WalkthroughRepository,

    /**
     * Repository for games
     */
    private val gameRepository: GameRepository

) : WalkthroughService {

    override fun getByGame(game: Int): Walkthrough {
        return walkthroughRepository.findByGameId(id = game)
            .orElseThrow { InputException(key = "WALKTHROUGH_NOT_EXIST", message = "Walkthrough doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    override fun getByUuid(uuid: String): Walkthrough {
        return walkthroughRepository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "WALKTHROUGH_NOT_EXIST", message = "Walkthrough doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(walkthrough: Walkthrough): Walkthrough {
        return walkthroughRepository.save(walkthrough)
    }

    @Transactional
    override fun remove(walkthrough: Walkthrough) {
        val game = walkthrough.game!!
        game.walkthrough = null
        gameRepository.save(game)
    }

}
