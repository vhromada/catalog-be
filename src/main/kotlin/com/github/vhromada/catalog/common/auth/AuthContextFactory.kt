package com.github.vhromada.catalog.common.auth

import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import java.util.UUID

/**
 * A class represents factory for creating auth context.
 *
 * @author Vladimir Hromada
 */
object AuthContextFactory {

    /**
     * Inits auth context.
     *
     * @param request request
     * @return auth context
     */
    fun init(request: ServletRequest): AuthContext {
        val auth = AuthContext()
        addHeader(auth = auth, request = request, name = Header.REQUEST_ID, required = true)
        addHeader(auth = auth, request = request, name = Header.USER)
        return auth
    }

    /**
     * Adds header to auth context.
     *
     * @param auth     auth context
     * @param request  request
     * @param name     header's name
     * @param required true if header should be created
     * @return header's value
     */
    private fun addHeader(auth: AuthContext, request: ServletRequest, name: Header, required: Boolean = false) {
        val value = getHeader(request = request, name = name)
        if (!value.isNullOrBlank()) {
            auth.addHeader(name = name, value = value)
        } else if (required) {
            auth.addHeader(name = name, value = UUID.randomUUID().toString())
        }
    }

    /**
     * Returns header's value.
     *
     * @param request request
     * @param name header's name
     * @return header's value
     */
    private fun getHeader(request: ServletRequest, name: Header): String? {
        return if (request is HttpServletRequest) {
            request.getHeader(name.value)
        } else {
            null
        }
    }


}
