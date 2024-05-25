package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Joke
import com.github.vhromada.catalog.entity.JokeStatistics
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeJokeRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.JokeFacade
import com.github.vhromada.catalog.mapper.JokeMapper
import com.github.vhromada.catalog.service.JokeService
import com.github.vhromada.catalog.validator.JokeValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for jokes.
 *
 * @author Vladimir Hromada
 */
@Component("jokeFacade")
class JokeFacadeImpl(

    /**
     * Service for jokes
     */
    private val service: JokeService,

    /**
     * Mapper for jokes
     */
    private val mapper: JokeMapper,

    /**
     * Validator for jokes
     */
    private val validator: JokeValidator

) : JokeFacade {

    override fun search(filter: PagingFilter): Page<Joke> {
        val jokes = service.search(pageable = filter.toPageable(sort = Sort.by("id")))
        return Page(data = mapper.mapJokes(source = jokes.content), page = jokes)
    }

    override fun get(uuid: String): Joke {
        return mapper.mapJoke(source = service.get(uuid = uuid))
    }

    override fun add(request: ChangeJokeRequest): Joke {
        validator.validateRequest(request = request)
        return mapper.mapJoke(source = service.store(joke = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangeJokeRequest): Joke {
        validator.validateRequest(request = request)
        val joke = service.get(uuid = uuid)
        joke.merge(joke = mapper.mapRequest(source = request))
        return mapper.mapJoke(source = service.store(joke = joke))
    }

    override fun remove(uuid: String) {
        service.remove(joke = service.get(uuid = uuid))
    }

    override fun getStatistics(): JokeStatistics {
        return JokeStatistics(count = service.getCount().toInt())
    }

}
