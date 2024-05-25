package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.domain.Register
import com.github.vhromada.catalog.entity.RegisterType

/**
 * An interface represents service for registers.
 *
 * @author Vladimir Hromada
 */
interface RegisterService {

    /**
     * Returns list of registers.
     *
     * @return list of registers
     */
    fun getAll(): List<Register>

    /**
     * Returns register.
     *
     * @param type type of register
     * @return register
     * @throws InputException if register doesn't exist in data storage
     */
    fun get(type: RegisterType): Register

    /**
     * Checks register's value code.
     *
     * @param type type of register
     * @param code register's value code
     * @throws InputException if register doesn't exist in data storage
     * or register's value code doesn't exist in data storage
     */
    fun checkValue(type: RegisterType, code: String)

}
