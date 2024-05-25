package com.github.vhromada.catalog.common.auth

/**
 * A class represents auth context.
 *
 * @author Vladimir Hromada
 */
class AuthContext {

    /**
     * Headers
     */
    private val headers: MutableMap<String, String> = mutableMapOf()

    /**
     * Adds header.
     *
     * @param name header's name
     * @param value header's value
     */
    fun addHeader(name: Header, value: String) {
        headers.putIfAbsent(name.value, value)
    }

    /**
     * Returns header's value.
     *
     * @param name header's name
     * @return header's value
     */
    fun getHeader(name: Header): String? {
        return headers[name.value]
    }

}
