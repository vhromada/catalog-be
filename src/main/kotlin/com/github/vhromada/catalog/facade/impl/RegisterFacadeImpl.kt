package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.facade.RegisterFacade
import com.github.vhromada.catalog.service.RegisterService
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for registers.
 *
 * @author Vladimir Hromada
 */
@Component("registerFacade")
class RegisterFacadeImpl(

    /**
     * Service for registers
     */
    private val service: RegisterService

) : RegisterFacade {

    override fun getProgramFormats(): List<String> {
        return getValues(type = RegisterType.PROGRAM_FORMATS)
    }

    override fun getBookItemFormats(): List<String> {
        return getValues(type = RegisterType.BOOK_ITEM_FORMATS)
    }

    override fun getLanguages(): List<String> {
        return getValues(type = RegisterType.LANGUAGES)
    }

    override fun getSubtitles(): List<String> {
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
