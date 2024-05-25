package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Program
import com.github.vhromada.catalog.domain.filter.ProgramFilter
import com.github.vhromada.catalog.domain.io.ProgramStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeProgramRequest
import com.github.vhromada.catalog.mapper.ProgramMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for programs.
 *
 * @author Vladimir Hromada
 */
@Component("programMapper")
class ProgramMapperImpl(

    /**
     * Service for normalizing strings
     */
    private val normalizerService: NormalizerService,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : ProgramMapper {

    override fun mapProgram(source: Program): com.github.vhromada.catalog.entity.Program {
        return com.github.vhromada.catalog.entity.Program(
            uuid = source.uuid,
            name = source.name,
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            mediaCount = source.mediaCount,
            format = source.format,
            crack = source.crack,
            serialKey = source.serialKey,
            otherData = source.otherData,
            note = source.note
        )
    }

    override fun mapPrograms(source: List<Program>): List<com.github.vhromada.catalog.entity.Program> {
        return source.map { mapProgram(source = it) }
    }

    override fun mapRequest(source: ChangeProgramRequest): Program {
        return Program(
            id = null,
            uuid = uuidProvider.getUuid(),
            name = source.name!!,
            normalizedName = normalizerService.normalize(source = source.name),
            wikiEn = source.wikiEn,
            wikiCz = source.wikiCz,
            mediaCount = source.mediaCount!!,
            format = source.format!!,
            crack = source.crack!!,
            serialKey = source.serialKey!!,
            otherData = source.otherData,
            note = source.note
        )
    }

    override fun mapFilter(source: NameFilter): ProgramFilter {
        return ProgramFilter(name = source.name)
    }

    override fun mapStatistics(source: ProgramStatistics): com.github.vhromada.catalog.entity.ProgramStatistics {
        return com.github.vhromada.catalog.entity.ProgramStatistics(
            count = source.count.toInt(),
            mediaCount = if (source.mediaCount == null) 0 else source.mediaCount.toInt()
        )
    }

}
