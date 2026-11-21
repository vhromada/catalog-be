package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Walkthrough
import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest

/**
 * An interface represents mapper for walkthroughs.
 *
 * @author Vladimir Hromada
 */
interface WalkthroughMapper {

    /**
     * Maps walkthrough.
     *
     * @param source walkthrough
     * @return mapped walkthrough
     */
    fun mapWalkthrough(source: Walkthrough): com.github.vhromada.catalog.entity.Walkthrough

    /**
     * Maps request for changing walkthrough.
     *
     * @param source request for changing walkthrough
     * @return mapped walkthrough
     */
    fun mapRequest(source: ChangeWalkthroughRequest): Walkthrough

}
