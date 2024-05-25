package com.github.vhromada.catalog.common.result

/**
 * A class represents event.
 *
 * @author Vladimir Hromada
 */
data class Event(

    /**
     * Severity
     */
    val severity: Severity,

    /**
     * Key
     */
    val key: String,

    /**
     * Message
     */
    val message: String

)
