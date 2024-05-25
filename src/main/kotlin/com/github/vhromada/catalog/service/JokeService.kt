package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Joke
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for jokes.
 *
 * @author Vladimir Hromada
 */
interface JokeService {

    /**
     * Returns page of jokes.
     *
     * @param pageable paging information
     * @return page of jokes
     */
    fun search(pageable: Pageable): Page<Joke>

    /**
     * Returns joke.
     *
     * @param uuid UUID
     * @return joke
     * @throws InputException if joke doesn't exist in data storage
     */
    fun get(uuid: String): Joke

    /**
     * Stores joke.
     *
     * @param joke joke
     * @return stored joke
     */
    fun store(joke: Joke): Joke

    /**
     * Removes joke.
     *
     * @param joke joke
     */
    fun remove(joke: Joke)

    /**
     * Returns count of jokes.
     *
     * @return count of jokes
     */
    fun getCount(): Long

}
