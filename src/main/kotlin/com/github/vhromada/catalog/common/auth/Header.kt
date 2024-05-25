package com.github.vhromada.catalog.common.auth

/**
 * An enumeration represents header.
 *
 * @author Vladimir Hromada
 */
enum class Header(

    /**
     * Value
     */
    val value: String

) {

    /**
     * X-Request-Id
     */
    REQUEST_ID("X-Request-Id"),

    /**
     * X-User
     */
    USER("X-User")

}
