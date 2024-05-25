package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Game
import com.github.vhromada.catalog.domain.filter.GameFilter
import com.github.vhromada.catalog.domain.io.GameStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeGameRequest
import com.github.vhromada.catalog.mapper.GameMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for games.
 *
 * @author Vladimir Hromada
 */
@Component("gameMapper")
class GameMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : GameMapper {

    override fun mapGame(source: Game): com.github.vhromada.catalog.entity.Game {
        return com.github.vhromada.catalog.entity.Game(
            uuid = source.uuid,
            name = source.name,
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            mediaCount = source.mediaCount,
            format = source.format,
            crack = source.crack,
            serialKey = source.serialKey,
            patch = source.patch,
            trainer = source.trainer,
            trainerData = source.trainerData,
            editor = source.editor,
            saves = source.saves,
            otherData = source.otherData,
            note = source.note,
            cheat = source.cheat != null
        )
    }

    override fun mapGames(source: List<Game>): List<com.github.vhromada.catalog.entity.Game> {
        return source.map { mapGame(source = it) }
    }

    override fun mapRequest(source: ChangeGameRequest): Game {
        return Game(
            id = null,
            uuid = uuidProvider.getUuid(),
            name = source.name!!,
            normalizedName = normalizerService.normalize(source = source.name),
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            mediaCount = source.mediaCount!!,
            format = source.format!!,
            cheat = null,
            crack = source.crack!!,
            serialKey = source.serialKey!!,
            patch = source.patch!!,
            trainer = source.trainer!!,
            trainerData = source.trainerData!!,
            editor = source.editor!!,
            saves = source.saves!!,
            otherData = source.otherData,
            note = source.note
        )
    }

    override fun mapFilter(source: NameFilter): GameFilter {
        return GameFilter(name = source.name)
    }

    override fun mapStatistics(source: GameStatistics): com.github.vhromada.catalog.entity.GameStatistics {
        return com.github.vhromada.catalog.entity.GameStatistics(
            count = source.count.toInt(),
            mediaCount = if (source.mediaCount == null) 0 else source.mediaCount.toInt()
        )
    }

}
