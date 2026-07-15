package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [RegisterFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class RegisterFacadeSpringTest {

    /**
     * Instance of [RegisterFacade]
     */
    @Autowired
    private lateinit var facade: RegisterFacade

    /**
     * Test method for [RegisterFacade.getProgramFormats].
     */
    @Test
    fun getProgramFormats() {
        val result = facade.getProgramFormats()

        assertThat(result).containsExactly("ISO", "BINARY", "STEAM", "BATTLE_NET")
    }

    /**
     * Test method for [RegisterFacade.getBookItemFormats].
     */
    @Test
    fun getBookItemFormats() {
        val result = facade.getBookItemFormats()

        assertThat(result).containsExactly("PAPER", "PDF", "DOC", "TXT")
    }

    /**
     * Test method for [RegisterFacade.getLanguages].
     */
    @Test
    fun getLanguages() {
        val result = facade.getLanguages()

        assertThat(result).containsExactly("CZ", "EN", "FR", "JP", "SK")
    }

    /**
     * Test method for [RegisterFacade.getSubtitles].
     */
    @Test
    fun getSubtitles() {
        val result = facade.getSubtitles()

        assertThat(result).containsExactly("CZ", "EN")
    }

}
