package com.github.vhromada.catalog.utils

import java.time.LocalDate

/**
 * A class represents constants.
 *
 * @author Vladimir Hromada
 */
object Constants {

    /**
     * Minimal year
     */
    const val MIN_YEAR = 1930

    /**
     * Current year
     */
    val CURRENT_YEAR = LocalDate.now().year

    /**
     * Maximum IMDB code
     */
    const val MAX_IMDB_CODE = 999_999_999

}
