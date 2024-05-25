package com.github.vhromada.catalog.entity

/**
 * An enumeration represents type of register.
 *
 * @author Vladimir Hromada
 */
enum class RegisterType(

    /**
     * Number of register
     */
    val number: Int

) {

    /**
     * Formats for program
     */
    PROGRAM_FORMATS(number = 1),

    /**
     * Languages
     */
    LANGUAGES(number = 2),

    /**
     * Subtitles
     */
    SUBTITLES(number = 3),

    /**
     * Formats for book item
     */
    BOOK_ITEM_FORMATS(number = 4)

}
