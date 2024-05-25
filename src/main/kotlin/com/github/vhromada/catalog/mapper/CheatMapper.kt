package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Cheat
import com.github.vhromada.catalog.entity.io.ChangeCheatRequest

/**
 * An interface represents mapper for cheats.
 *
 * @author Vladimir Hromada
 */
interface CheatMapper {

    /**
     * Maps cheat.
     *
     * @param source cheat
     * @return mapped cheat
     */
    fun mapCheat(source: Cheat): com.github.vhromada.catalog.entity.Cheat

    /**
     * Maps request for changing cheat.
     *
     * @param source request for changing cheat
     * @return mapped cheat
     */
    fun mapRequest(source: ChangeCheatRequest): Cheat

}
