package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.facade.RegisterFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents controller for registers.
 *
 * @author Vladimir Hromada
 */
@RestController("registerController")
@RequestMapping("rest/registers")
@Tag(name = "Registers")
class RegisterController(

    /**
     * Facade for registers
     */
    private val facade: RegisterFacade

) {

    /**
     * Returns list of formats for program.
     *
     * @return list of formats for program
     */
    @GetMapping("formats/programs")
    fun getProgramFormats(): List<String> {
        return facade.getProgramFormats()
    }

    /**
     * Returns list of formats for book item.
     *
     * @return list of formats for book item
     */
    @GetMapping("formats/book-items")
    fun getBookItemFormats(): List<String> {
        return facade.getBookItemFormats()
    }

    /**
     * Returns list of languages.
     *
     * @return list of languages
     */
    @GetMapping("languages")
    fun getLanguages(): List<String> {
        return facade.getLanguages()
    }

    /**
     * Returns list of subtitles.
     *
     * @return list of subtitles
     */
    @GetMapping("subtitles")
    fun getSubtitles(): List<String> {
        return facade.getSubtitles()
    }

}
