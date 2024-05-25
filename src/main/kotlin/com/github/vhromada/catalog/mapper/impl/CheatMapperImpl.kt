package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Cheat
import com.github.vhromada.catalog.entity.io.ChangeCheatRequest
import com.github.vhromada.catalog.mapper.CheatDataMapper
import com.github.vhromada.catalog.mapper.CheatMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for cheats.
 *
 * @author Vladimir Hromada
 */
@Component("cheatMapper")
class CheatMapperImpl(

    /**
     * Mapper for cheat's data
     */
    private val cheatDataMapper: CheatDataMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : CheatMapper {

    override fun mapCheat(source: Cheat): com.github.vhromada.catalog.entity.Cheat {
        return com.github.vhromada.catalog.entity.Cheat(
            uuid = source.uuid,
            gameSetting = source.gameSetting,
            cheatSetting = source.cheatSetting,
            data = cheatDataMapper.mapCheatDataList(source = source.data)
        )
    }

    override fun mapRequest(source: ChangeCheatRequest): Cheat {
        return Cheat(
            id = null,
            uuid = uuidProvider.getUuid(),
            gameSetting = source.gameSetting,
            cheatSetting = source.cheatSetting,
            data = cheatDataMapper.mapRequests(source = source.data!!.filterNotNull()).toMutableList()
        )
    }

}
