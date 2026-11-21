package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Walkthrough
import com.github.vhromada.catalog.entity.io.ChangeWalkthroughRequest
import com.github.vhromada.catalog.mapper.WalkthroughMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for walkthroughs.
 *
 * @author Vladimir Hromada
 */
@Component("walkthroughMapper")
class WalkthroughMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : WalkthroughMapper {

    override fun mapWalkthrough(source: Walkthrough): com.github.vhromada.catalog.entity.Walkthrough {
        return com.github.vhromada.catalog.entity.Walkthrough(
            uuid = source.uuid,
            content = source.content
        )
    }

    override fun mapRequest(source: ChangeWalkthroughRequest): Walkthrough {
        return Walkthrough(
            id = null,
            uuid = uuidProvider.getUuid(),
            content = source.content!!
        )
    }

}
