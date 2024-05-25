package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Episode
import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest
import com.github.vhromada.catalog.mapper.EpisodeMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for episodes.
 *
 * @author Vladimir Hromada
 */
@Component("episodeMapper")
class EpisodeMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : EpisodeMapper {

    override fun mapEpisode(source: Episode): com.github.vhromada.catalog.entity.Episode {
        return com.github.vhromada.catalog.entity.Episode(
            uuid = source.uuid,
            number = source.number,
            name = source.name,
            length = source.length,
            note = source.note
        )
    }

    override fun mapEpisodes(source: List<Episode>): List<com.github.vhromada.catalog.entity.Episode> {
        return source.map { mapEpisode(source = it) }
    }

    override fun mapRequest(source: ChangeEpisodeRequest): Episode {
        return Episode(
            id = null,
            uuid = uuidProvider.getUuid(),
            number = source.number!!,
            name = source.name!!,
            length = source.length!!,
            note = source.note
        )
    }

}
