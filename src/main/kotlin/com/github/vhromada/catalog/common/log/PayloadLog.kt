package com.github.vhromada.catalog.common.log

import com.github.openjson.JSONArray
import com.github.openjson.JSONObject
import java.util.function.Supplier

/**
 * A class represents log of payload.
 *
 * @author Vladimir Hromada
 */
class PayloadLog(

    /**
     * Content type
     */
    private val contentType: String,

    /**
     * Content
     */
    private val content: Supplier<ByteArray>

) {

    /**
     * Returns message.
     *
     * @return message
     */
    fun getMessage(): String {
        if (!SUPPORTED_CONTENT_TYPES.contains(contentType)) {
            return "Unsupported"
        }
        return format(source = String(content.get()))
    }

    /**
     * Formats message.
     *
     * @param source source message
     * @return formatted message
     */
    private fun format(source: String): String {
        if (!source.contains(Regex("[\\n\\r]"))) {
            return source;
        }
        return if (source.trim().startsWith("[")) {
            JSONArray(source).toString()
        } else {
            JSONObject(source).toString()
        }
    }

    companion object {

        /**
         * Supported content types
         */
        val SUPPORTED_CONTENT_TYPES = listOf("application/json");

    }

}
