package com.github.vhromada.catalog.exception

import com.github.vhromada.catalog.common.result.Result
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

/**
 * A class represents input exception.
 *
 * @author Vladimir Hromada
 */
class InputException(

    /**
     * Result
     */
    val result: Result<*>,

    /**
     * HTTPS status code
     */
    val httpStatus: HttpStatusCode = HttpStatus.UNPROCESSABLE_ENTITY

) : RuntimeException(result.toString()) {

    /**
     * Creates a new instance of [InputException].
     *
     * @param key        key
     * @param message    message
     * @param httpStatus HTTP status
     */
    constructor(key: String, message: String, httpStatus: HttpStatus = HttpStatus.UNPROCESSABLE_ENTITY) : this(Result.error<Unit>(key = key, message = message), httpStatus)

}
