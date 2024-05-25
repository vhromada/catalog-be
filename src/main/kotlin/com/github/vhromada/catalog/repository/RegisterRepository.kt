package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Register
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for registers.
 *
 * @author Vladimir Hromada
 */
interface RegisterRepository : JpaRepository<Register, Int> {

    /**
     * Finds register by number.
     *
     * @param number number
     * @return register
     */
    fun findByNumber(number: Int): Optional<Register>

}
