package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Medium

/**
 * An interface represents mapper for media.
 *
 * @author Vladimir Hromada
 */
interface MediumMapper {

    /**
     * Maps list of media.
     *
     * @param source list of media
     * @return mapped list of media
     */
    fun mapMedia(source: List<Medium>): List<com.github.vhromada.catalog.entity.Medium>

}
