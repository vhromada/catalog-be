package com.github.vhromada.catalog.exception

import com.github.vhromada.catalog.mapper.IssueMapper
import org.springframework.core.annotation.Order
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * A class represents common exception handler for REST
 *
 * @author Vladimir Hromada
 */
@ControllerAdvice(annotations = [RestController::class])
@Order(-1000)
class RestExceptionHandler(

    /**
     * Mapper
     */
    private val mapper: IssueMapper

) : ResponseEntityExceptionHandler() {

    /**
     * Handles [InputException].
     *
     * @param ex      exception
     * @param request request
     * @return response for exception
     */
    @ExceptionHandler(InputException::class)
    fun handleInputException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        return ResponseEntity(mapper.map(source = (ex as InputException).result), ex.httpStatus)
    }

}
