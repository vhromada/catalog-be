package com.github.vhromada.catalog.service

/**
 * An interface represents service for normalizing strings.
 *
 * @author Vladimir Hromada
 */
interface NormalizerService {

    /**
     * Returns normalized string.
     *
     * @param source string for normalizing
     * @return normalized string
     */
    fun normalize(source: String): String

}
