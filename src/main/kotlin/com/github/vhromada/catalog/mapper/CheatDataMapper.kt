package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.CheatData
import com.github.vhromada.catalog.entity.io.ChangeCheatData

/**
 * An interface represents mapper for cheat's data.
 *
 * @author Vladimir Hromada
 */
interface CheatDataMapper {

    /**
     * Maps list of cheat's data.
     *
     * @param source list of cheat's data
     * @return mapped list of cheat's data
     */
    fun mapCheatDataList(source: List<CheatData>): List<com.github.vhromada.catalog.entity.CheatData>

    /**
     * Maps changing cheat's data.
     *
     * @param source changing cheat's data.
     * @return mapped changing cheat's data.
     */
    fun mapRequest(source: ChangeCheatData): CheatData

    /**
     * Maps list of changing cheat's data.
     *
     * @param source list of changing cheat's data.
     * @return mapped list of changing cheat's data.
     */
    fun mapRequests(source: List<ChangeCheatData>): List<CheatData>

}
