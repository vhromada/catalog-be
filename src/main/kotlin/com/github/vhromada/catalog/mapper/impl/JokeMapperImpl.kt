package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Joke
import com.github.vhromada.catalog.entity.io.ChangeJokeRequest
import com.github.vhromada.catalog.mapper.JokeMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for jokes.
 *
 * @author Vladimir Hromada
 */
@Component("jokeMapper")
class JokeMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : JokeMapper {

    override fun mapJoke(source: Joke): com.github.vhromada.catalog.entity.Joke {
        return com.github.vhromada.catalog.entity.Joke(
            uuid = source.uuid,
            content = source.content
        )
    }

    override fun mapJokes(source: List<Joke>): List<com.github.vhromada.catalog.entity.Joke> {
        return source.map { mapJoke(source = it) }
    }

    override fun mapRequest(source: ChangeJokeRequest): Joke {
        return Joke(
            id = null,
            uuid = uuidProvider.getUuid(),
            content = source.content!!
        )
    }

}
