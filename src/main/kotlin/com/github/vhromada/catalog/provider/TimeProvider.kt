package com.github.vhromada.catalog.provider

import java.time.LocalDateTime

/**
 * An interface represents provider for time.
 *
 * @author Vladimir Hromada
 */
interface TimeProvider {

    /**
     * Returns time.
     *
     * @return time
     */
    fun getTime(): LocalDateTime

}
