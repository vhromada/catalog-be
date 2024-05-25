package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Picture
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for pictures.
 *
 * @author Vladimir Hromada
 */
interface PictureFacade {

    /**
     * Returns page of pictures for filter.
     *
     * @param filter filter
     * @return page of pictures for filter
     */
    fun search(filter: PagingFilter): Page<String>

    /**
     * Returns picture.
     *
     * @param uuid UUID
     * @return picture
     * @throws InputException if picture doesn't exist in data storage
     */
    fun get(uuid: String): Picture

    /**
     * Adds picture.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty
     *
     * @param request request for changing picture
     * @return created picture
     * @throws InputException if request for changing picture isn't valid
     */
    fun add(request: ChangePictureRequest): Picture

    /**
     * Updates picture.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty
     *  * Picture doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing picture
     * @return updated picture
     * @throws InputException if request for changing picture isn't valid
     */
    fun update(uuid: String, request: ChangePictureRequest): Picture

    /**
     * Removes picture.
     *
     * @param uuid UUID
     * @throws InputException if picture doesn't exist in data storage
     */
    fun remove(uuid: String)

}
