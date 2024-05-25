package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.service.NormalizerService
import org.springframework.stereotype.Component
import java.text.Normalizer

/**
 * A class represents implementation of service for normalizing strings.
 *
 * @author Vladimir Hromada
 */
@Component("normalizerService")
class NormalizerServiceImpl : NormalizerService {

    override fun normalize(source: String): String {
        val normalized = Regex("\\p{InCombiningDiacriticalMarks}").replace(Normalizer.normalize(source, Normalizer.Form.NFD), "").lowercase()
        val char = source.lowercase()[0]
        val prefix =
            if (char.isDigit()) {
                "0000"
            } else if (char.code in 'a'.code..'z'.code) {
                "${char.code}0"
            } else {
                "${normalized[0].code}1"
            }
        return if (prefix.length == 3) {
            "0$prefix$normalized"
        } else {
            "$prefix$normalized"
        }
    }

}
