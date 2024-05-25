package com.github.vhromada.catalog.common.log

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * A class represents test for class [SensitiveLog].
 *
 * @author Vladimir Hromada
 */
class SensitiveLogTest {

    /**
     * Test method for [SensitiveLog.process].
     */
    @Test
    fun process() {
        val rules = "\"password\"\\s*:\\s*\".*?\";\"password\":\"*****\"|[a,A]uthorization=\\[Basic.*?\\];authorization=[Basic *****]"
        val source = "test\"password\"\t: \"test\"message;authorization=[Basic auth]auth"
        val result = SensitiveLog.of(rules = rules).process(message = source)
        assertThat(result).isEqualTo("test\"password\":\"*****\"message;authorization=[Basic *****]auth")
    }

}
