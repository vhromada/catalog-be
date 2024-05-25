package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Medium
import com.github.vhromada.catalog.mapper.MediumMapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for media.
 *
 * @author Vladimir Hromada
 */
@Component("mediumMapper")
class MediumMapperImpl : MediumMapper {

    override fun mapMedia(source: List<Medium>): List<com.github.vhromada.catalog.entity.Medium> {
        return source.map { mapMedium(source = it) }
    }

    /**
     * Maps medium.
     *
     * @param source medium
     * @return mapped medium
     */
    private fun mapMedium(source: Medium): com.github.vhromada.catalog.entity.Medium {
        return com.github.vhromada.catalog.entity.Medium(
            number = source.number,
            length = source.length
        )
    }

}
