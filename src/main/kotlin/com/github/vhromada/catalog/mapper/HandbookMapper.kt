package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Handbook
import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest

/**
 * An interface represents mapper for handbooks.
 *
 * @author Vladimir Hromada
 */
interface HandbookMapper {

    /**
     * Maps handbook.
     *
     * @param source handbook
     * @return mapped handbook
     */
    fun mapHandbook(source: Handbook): com.github.vhromada.catalog.entity.Handbook

    /**
     * Maps request for changing handbook.
     *
     * @param source request for changing handbook
     * @return mapped handbook
     */
    fun mapRequest(source: ChangeHandbookRequest): Handbook

}
