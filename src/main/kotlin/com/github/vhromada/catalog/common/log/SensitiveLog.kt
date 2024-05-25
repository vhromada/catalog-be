package com.github.vhromada.catalog.common.log

/**
 * A class represents sensitive log.
 *
 * @author Vladimir Hromada
 */
class SensitiveLog(

    /**
     * Rules
     */
    private val rules: List<Pair<String, String>>

) {

    /**
     * Processes message.
     *
     * @param message message
     * @return processed message
     */
    fun process(message: String): String {
        var result = message
        rules.forEach {
            result = result.replace(Regex(it.first), it.second)
        }
        return result
    }

    companion object {

        /**
         * Returns result with specified rules.
         *
         * @param rules rules
         * @return result with specified rules
         */
        fun of(rules: String): SensitiveLog {
            if (rules.isBlank()) {
                return SensitiveLog(rules = emptyList())
            }
            val ruleList = mutableListOf<Pair<String, String>>()
            rules.split(Regex("\\|")).forEach {
                val parts = it.split(";", limit = 2)
                ruleList.add(Pair(parts[0], parts[1]))
            }
            return SensitiveLog(rules = ruleList.toList())
        }

    }

}
