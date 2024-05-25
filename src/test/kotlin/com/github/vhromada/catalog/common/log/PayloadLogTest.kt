package com.github.vhromada.catalog.common.log

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * A class represents test for class [PayloadLog].
 *
 * @author Vladimir Hromada
 */
class PayloadLogTest {

    /**
     * Test method for [PayloadLog.getMessage] with JSON object.
     */
    @Test
    fun getMessageJsonObject() {
        val source = "{\n\t\"name\"\n:\n\"test\"\n}".toByteArray()
        val result = PayloadLog(contentType = "application/json", content = { source }).getMessage()
        assertThat(result).isEqualTo("{\"name\":\"test\"}")
    }

    /**
     * Test method for [PayloadLog.getMessage] with JSON array.
     */
    @Test
    fun getMessageJsonArray() {
        val source = "[\n\"CZ\",\n\"EN\"\n]".toByteArray()
        val result = PayloadLog(contentType = "application/json", content = { source }).getMessage()
        assertThat(result).isEqualTo("[\"CZ\",\"EN\"]")
    }

    /**
     * Test method for [PayloadLog.getMessage] with text.
     */
    @Test
    fun getMessageText() {
        val source = "text".toByteArray()
        val result = PayloadLog(contentType = "text/plain", content = { source }).getMessage()
        assertThat(result).isEqualTo("Unsupported")
    }

}
