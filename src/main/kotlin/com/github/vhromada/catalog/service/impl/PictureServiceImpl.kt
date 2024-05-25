package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Picture
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.PictureRepository
import com.github.vhromada.catalog.service.PictureService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for pictures.
 *
 * @author Vladimir Hromada
 */
@Service("pictureService")
class PictureServiceImpl(

    /**
     * Repository for pictures
     */
    private val repository: PictureRepository

) : PictureService {

    override fun search(pageable: Pageable): Page<Picture> {
        return repository.findAll(pageable)
    }

    override fun getById(id: Int): Picture {
        return repository.findById(id)
            .orElseThrow { InputException(key = "PICTURE_NOT_EXIST", message = "Picture doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    override fun getByUuid(uuid: String): Picture {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "PICTURE_NOT_EXIST", message = "Picture doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(picture: Picture): Picture {
        return repository.save(picture)
    }

    @Transactional
    override fun remove(picture: Picture) {
        repository.delete(picture)
    }

}
