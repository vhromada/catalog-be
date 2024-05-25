package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Picture
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.PictureFacade
import com.github.vhromada.catalog.mapper.PictureMapper
import com.github.vhromada.catalog.service.PictureService
import com.github.vhromada.catalog.validator.PictureValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for pictures.
 *
 * @author Vladimir Hromada
 */
@Component("pictureFacade")
class PictureFacadeImpl(

    /**
     * Service for pictures
     */
    private val service: PictureService,

    /**
     * Mapper for pictures
     */
    private val mapper: PictureMapper,

    /**
     * Validator for pictures
     */
    private val validator: PictureValidator

) : PictureFacade {

    override fun search(filter: PagingFilter): Page<String> {
        val pictures = service.search(pageable = filter.toPageable(sort = Sort.by("id")))
        return Page(data = mapper.mapPictures(source = pictures.content), page = pictures)
    }

    override fun get(uuid: String): Picture {
        return mapper.mapPicture(source = service.getByUuid(uuid = uuid))
    }

    override fun add(request: ChangePictureRequest): Picture {
        validator.validateRequest(request = request)
        return mapper.mapPicture(source = service.store(picture = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangePictureRequest): Picture {
        validator.validateRequest(request = request)
        val picture = service.getByUuid(uuid = uuid)
        picture.merge(picture = mapper.mapRequest(source = request))
        return mapper.mapPicture(source = service.store(picture = picture))
    }

    override fun remove(uuid: String) {
        service.remove(picture = service.getByUuid(uuid = uuid))
    }

}
