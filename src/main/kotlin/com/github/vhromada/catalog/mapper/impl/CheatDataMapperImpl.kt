package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.CheatData
import com.github.vhromada.catalog.entity.io.ChangeCheatData
import com.github.vhromada.catalog.mapper.CheatDataMapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for cheat's data.
 *
 * @author Vladimir Hromada
 */
@Component("cheatDataMapper")
class CheatDataMapperImpl : CheatDataMapper {

    override fun mapCheatDataList(source: List<CheatData>): List<com.github.vhromada.catalog.entity.CheatData> {
        return source.map { mapCheatData(source = it) }
    }

    override fun mapRequest(source: ChangeCheatData): CheatData {
        return CheatData(
            id = null,
            action = source.action!!,
            description = source.description!!
        )
    }

    override fun mapRequests(source: List<ChangeCheatData>): List<CheatData> {
        return source.map { mapRequest(source = it) }
    }

    /**
     * Maps cheat's data.
     *
     * @param source cheat's data
     * @return mapped cheat's data
     */
    private fun mapCheatData(source: CheatData): com.github.vhromada.catalog.entity.CheatData {
        return com.github.vhromada.catalog.entity.CheatData(
            action = source.action,
            description = source.description
        )
    }

}
