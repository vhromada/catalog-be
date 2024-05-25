package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Joke
import com.github.vhromada.catalog.entity.io.ChangeJokeRequest

/**
 * An interface represents mapper for jokes.
 *
 * @author Vladimir Hromada
 */
interface JokeMapper {

    /**
     * Maps joke.
     *
     * @param source joke
     * @return mapped joke
     */
    fun mapJoke(source: Joke): com.github.vhromada.catalog.entity.Joke

    /**
     * Maps list of jokes.
     *
     * @param source list of jokes
     * @return mapped list of jokes
     */
    fun mapJokes(source: List<Joke>): List<com.github.vhromada.catalog.entity.Joke>

    /**
     * Maps request for changing joke.
     *
     * @param source request for changing joke
     * @return mapped joke
     */
    fun mapRequest(source: ChangeJokeRequest): Joke

}
