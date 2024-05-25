package com.github.vhromada.catalog.validator

import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents validator for pictures.
 *
 * @author Vladimir Hromada
 */
interface PictureValidator {

    /**
     * Validates request for changing picture.
     * <br></br>
     * Validation errors:
     *
     *  * Content is null
     *  * Content is empty
     *
     * @param request request for changing picture
     * @throws InputException if request for changing picture isn't valid
     */
    fun validateRequest(request: ChangePictureRequest)

}
