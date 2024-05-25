package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Picture
import com.github.vhromada.catalog.entity.io.ChangePictureRequest

/**
 * An interface represents mapper for pictures.
 *
 * @author Vladimir Hromada
 */
interface PictureMapper {

    /**
     * Maps picture.
     *
     * @param source picture
     * @return mapped picture
     */
    fun mapPicture(source: Picture): com.github.vhromada.catalog.entity.Picture

    /**
     * Maps list of pictures.
     *
     * @param source list of pictures
     * @return mapped list of pictures
     */
    fun mapPictures(source: List<Picture>): List<String>

    /**
     * Maps request for changing picture.
     *
     * @param source request for changing picture
     * @return mapped picture
     */
    fun mapRequest(source: ChangePictureRequest): Picture

}
