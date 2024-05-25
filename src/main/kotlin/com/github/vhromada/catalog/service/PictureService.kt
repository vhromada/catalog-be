package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Picture
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for pictures.
 *
 * @author Vladimir Hromada
 */
interface PictureService {

    /**
     * Returns page of pictures.
     *
     * @param pageable paging information
     * @return page of pictures
     */
    fun search(pageable: Pageable): Page<Picture>

    /**
     * Returns picture.
     *
     * @param id ID
     * @return picture
     * @throws InputException if picture doesn't exist in data storage
     */
    fun getById(id: Int): Picture

    /**
     * Returns picture.
     *
     * @param uuid UUID
     * @return picture
     * @throws InputException if picture doesn't exist in data storage
     */
    fun getByUuid(uuid: String): Picture

    /**
     * Stores picture.
     *
     * @param picture picture
     * @return stored picture
     */
    fun store(picture: Picture): Picture

    /**
     * Removes picture.
     *
     * @param picture picture
     */
    fun remove(picture: Picture)

}
