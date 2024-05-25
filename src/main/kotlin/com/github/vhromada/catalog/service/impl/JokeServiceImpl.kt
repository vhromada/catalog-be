package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Joke
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.JokeRepository
import com.github.vhromada.catalog.service.JokeService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for jokes.
 *
 * @author Vladimir Hromada
 */
@Service("jokeService")
class JokeServiceImpl(

    /**
     * Repository for jokes
     */
    private val repository: JokeRepository

) : JokeService {

    override fun search(pageable: Pageable): Page<Joke> {
        return repository.findAll(pageable)
    }

    override fun get(uuid: String): Joke {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "JOKE_NOT_EXIST", message = "Joke doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(joke: Joke): Joke {
        return repository.save(joke)
    }

    @Transactional
    override fun remove(joke: Joke) {
        repository.delete(joke)
    }

    override fun getCount(): Long {
        return repository.count()
    }

}
