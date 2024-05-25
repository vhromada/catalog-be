package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.domain.Picture
import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import com.github.vhromada.catalog.mapper.PictureMapper
import com.github.vhromada.catalog.provider.UuidProvider
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper for pictures.
 *
 * @author Vladimir Hromada
 */
@Component("pictureMapper")
class PictureMapperImpl(

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : PictureMapper {

    override fun mapPicture(source: Picture): com.github.vhromada.catalog.entity.Picture {
        return com.github.vhromada.catalog.entity.Picture(
            uuid = source.uuid,
            content = source.content
        )
    }

    override fun mapPictures(source: List<Picture>): List<String> {
        return source.map { it.uuid }
    }

    override fun mapRequest(source: ChangePictureRequest): Picture {
        return Picture(
            id = null,
            uuid = uuidProvider.getUuid(),
            content = source.content!!
        )
    }

}
