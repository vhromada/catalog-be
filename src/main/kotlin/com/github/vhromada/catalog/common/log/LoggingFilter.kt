package com.github.vhromada.catalog.common.log

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import org.springframework.web.util.WebUtils
import java.util.UUID

/**
 * A class represents filter for logging calling endpoints.
 *
 * @author Vladimir Hromada
 */
class LoggingFilter(

    /**
     * Sensitive log
     */
    private val sensitiveLog: SensitiveLog

) : OncePerRequestFilter() {

    /**
     * Logger
     */
    private val log = KotlinLogging.logger {}

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val uuid = UUID.randomUUID().toString()
        val wrappedRequest = prepareRequest(request = request)
        val wrappedResponse = prepareResponse(response = response)
        try {
            logRequest(request = wrappedRequest, uuid = uuid)
            filterChain.doFilter(wrappedRequest, wrappedResponse)
            logResponse(request = wrappedRequest, response = wrappedResponse, uuid = uuid)
            if (response != wrappedResponse) {
                wrappedResponse.copyBodyToResponse()
            }
        } catch (ex: Exception) {
            logException(request = wrappedRequest, exception = ex, uuid = uuid)
            throw ex
        }
    }

    /**
     * Returns request as [MultiReadHttpServletRequest].
     *
     * @param request request
     * @return request as [MultiReadHttpServletRequest]
     */
    private fun prepareRequest(request: HttpServletRequest): MultiReadHttpServletRequest {
        return WebUtils.getNativeRequest(request, MultiReadHttpServletRequest::class.java)
            ?: MultiReadHttpServletRequest(request)
    }

    /**
     * Returns response as [ContentCachingResponseWrapper].
     *
     * @param response response
     * @return response as [ContentCachingResponseWrapper]
     */
    private fun prepareResponse(response: HttpServletResponse): ContentCachingResponseWrapper {
        return WebUtils.getNativeResponse(response, ContentCachingResponseWrapper::class.java)
            ?: ContentCachingResponseWrapper(response)
    }

    /**
     * Logs request.
     *
     * @param request request
     * @param uuid    UUID
     */
    private fun logRequest(request: MultiReadHttpServletRequest, uuid: String) {
        val messageParts = mutableMapOf<String, String>()
        messageParts["headers"] = request.headerNames.toList().toSet().joinToString(separator = ",", prefix = "{", postfix = "}") { "$it=${request.getHeaders(it).toList()}" }
        if (request.contentLengthLong > 0) {
            val contentType = request.contentType ?: request.getHeader("Accept")
            messageParts["payload"] = PayloadLog(contentType = contentType, content = { request.getContentAsByteArray() }).getMessage()
        }
        log.info { prepareMessage(prefix = "REQ", uuid = uuid, request = request, messageParts = messageParts) }
    }

    /**
     * Logs response.
     *
     * @param request  request
     * @param response response
     * @param uuid     UUID
     */
    private fun logResponse(request: MultiReadHttpServletRequest, response: ContentCachingResponseWrapper, uuid: String) {
        val messageParts = mutableMapOf<String, String>()
        messageParts["headers"] = response.headerNames.toSet().joinToString(separator = ",", prefix = "{", postfix = "}") { "$it=${response.getHeaders(it)}" }
        if (response.contentSize > 0) {
            messageParts["type"] = response.contentType
            messageParts["payload"] = sensitiveLog.process(PayloadLog(contentType = response.contentType, content = { response.contentAsByteArray }).getMessage())
        }
        messageParts["status"] = getResponseStatus(response)
        log.info { prepareMessage(prefix = "RES", uuid = uuid, request = request, messageParts = messageParts) }
    }

    /**
     * Logs exception.
     *
     * @param request   request
     * @param exception exception
     * @param uuid      UUID
     */
    private fun logException(request: HttpServletRequest, exception: Exception, uuid: String) {
        val messageParts = mapOf("error" to "${getExceptionMessage(exception)}\n\t$exception")
        log.error { prepareMessage(prefix = "RES", uuid = uuid, request = request, messageParts = messageParts) }
    }

    /**
     * Prepares message for logging.
     *
     * @param prefix       prefix
     * @param uuid         UUID
     * @param request      request
     * @param messageParts message parts
     * @return message for logging
     */
    private fun prepareMessage(prefix: String, uuid: String, request: HttpServletRequest, messageParts: Map<String, String>): String {
        val message = StringBuilder("$prefix [${uuid}] ${request.method} uri=${request.requestURI}")
        if (!request.queryString.isNullOrBlank()) {
            message.append("?${request.queryString}")
        }
        message.append("; ")
        message.append(messageParts.entries.joinToString(separator = "; ") { "${it.key}=${it.value}" })
        return sensitiveLog.process(message = message.toString())
    }

    /**
     * Returns string representation of response HTTP status.
     *
     * @param response response
     * @return string representation of response HTTP status
     */
    private fun getResponseStatus(response: HttpServletResponse): String {
        val status = HttpStatus.valueOf(response.status)
        return "${status.value()} ${status.reasonPhrase}"
    }

    /**
     * Returns exception message.
     *
     * @param exception exception
     * @return exception message
     */
    private fun getExceptionMessage(exception: Exception): String {
        return if (exception.message.isNullOrBlank()) {
            ExceptionUtils.getRootCauseMessage(exception)
        } else {
            exception.message!!
        }
    }

}
