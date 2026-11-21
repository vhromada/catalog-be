package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Handbook
import com.github.vhromada.catalog.entity.io.ChangeHandbookRequest
import com.github.vhromada.catalog.mapper.HandbookMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for handbooks.
 *
 * @author Vladimir Hromada
 */
@Component("handbookMapper")
class HandbookMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : HandbookMapper {

    override fun mapHandbook(source: Handbook): com.github.vhromada.catalog.entity.Handbook {
        return com.github.vhromada.catalog.entity.Handbook(
            uuid = source.uuid,
            content = source.content
        )
    }

    override fun mapRequest(source: ChangeHandbookRequest): Handbook {
        return Handbook(
            id = null,
            uuid = uuidProvider.getUuid(),
            content = source.content!!
        )
    }

}
