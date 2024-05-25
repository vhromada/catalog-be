package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Joke
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.Optional

/**
 * An interface represents repository for jokes.
 *
 * @author Vladimir Hromada
 */
interface JokeRepository : JpaRepository<Joke, Int>, JpaSpecificationExecutor<Joke> {

    /**
     * Finds joke by UUID.
     *
     * @param uuid UUID
     * @return joke
     */
    fun findByUuid(uuid: String): Optional<Joke>

}
