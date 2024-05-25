package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.service.RegisterService
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
     * Service for registers
     */
    private val service: RegisterService

) {

    /**
     * Returns list of formats for program.
     *
     * @return list of formats for program
     */
    @GetMapping("formats/programs")
    fun getProgramFormats(): List<String> {
        return getValues(type = RegisterType.PROGRAM_FORMATS)
    }

    /**
     * Returns list of formats for book item.
     *
     * @return list of formats for book item
     */
    @GetMapping("formats/book-items")
    fun getBookItemFormats(): List<String> {
        return getValues(type = RegisterType.BOOK_ITEM_FORMATS)
    }

    /**
     * Returns list of languages.
     *
     * @return list of languages
     */
    @GetMapping("languages")
    fun getLanguages(): List<String> {
        return getValues(type = RegisterType.LANGUAGES)
    }

    /**
     * Returns list of subtitles.
     *
     * @return list of subtitles
     */
    @GetMapping("subtitles")
    fun getSubtitles(): List<String> {
        return getValues(type = RegisterType.SUBTITLES)
    }

    /**
     * Returns register's values.
     *
     * @param type type of register
     * @return register's values
     */
    private fun getValues(type: RegisterType): List<String> {
        return service.get(type = type).values.map { it.code }
    }

}
