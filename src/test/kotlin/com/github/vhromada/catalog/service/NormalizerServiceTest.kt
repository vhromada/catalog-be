package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.service.impl.NormalizerServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * A class represents test for class [NormalizerService].
 *
 * @author Vladimir Hromada
 */
class NormalizerServiceTest {

    /**
     * Text
     */
    private val text = "sžŠa"

    /**
     * Normalized text
     */
    private val normalizedText = "szsa"

    /**
     * Instance of [NormalizerService]
     */
    private lateinit var service: NormalizerService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = NormalizerServiceImpl()
    }

    /**
     * Test method for [NormalizerService.normalize] with number.
     */
    @Test
    fun normalizeNumber() {
        for (i in 0..9) {
            val result = service.normalize(source = "$i$text")
            assertThat(result).isEqualTo("0000$i$normalizedText")
        }
    }

    /**
     * Test method for [NormalizerService.normalize] with lower case letter.
     */
    @Test
    fun normalizeLowerCaseLetter() {
        for (i in 'a'..'c') {
            val result = service.normalize(source = "$i$text")
            assertThat(result).isEqualTo("0${i.code}0$i$normalizedText")
        }
        for (i in 'd'..'z') {
            val result = service.normalize(source = "$i$text")
            assertThat(result).isEqualTo("${i.code}0$i$normalizedText")
        }
    }

    /**
     * Test method for [NormalizerService.normalize] with upper case letter.
     */
    @Test
    fun normalizeUpperCaseLetter() {
        for (i in 'A'..'C') {
            val result = service.normalize(source = "$i$text")
            assertThat(result).isEqualTo("0${i.lowercaseChar().code}0${i.lowercaseChar()}$normalizedText")
        }
        for (i in 'D'..'Z') {
            val result = service.normalize(source = "$i$text")
            assertThat(result).isEqualTo("${i.lowercaseChar().code}0${i.lowercaseChar()}$normalizedText")
        }
    }

    /**
     * Test method for [NormalizerService.normalize] with special letter.
     */
    @Test
    fun normalizeSpecialLetter() {
        var result = service.normalize(source = "á$text")
        assertThat(result).isEqualTo("0${'a'.code}1a$normalizedText")

        result = service.normalize(source = "Á$text")
        assertThat(result).isEqualTo("0${'a'.code}1a$normalizedText")

        result = service.normalize(source = "ú$text")
        assertThat(result).isEqualTo("${'u'.code}1u$normalizedText")

        result = service.normalize(source = "Ú$text")
        assertThat(result).isEqualTo("${'u'.code}1u$normalizedText")

        result = service.normalize(source = "ž$text")
        assertThat(result).isEqualTo("${'z'.code}1z$normalizedText")

        result = service.normalize(source = "Ž$text")
        assertThat(result).isEqualTo("${'z'.code}1z$normalizedText")
    }

}
