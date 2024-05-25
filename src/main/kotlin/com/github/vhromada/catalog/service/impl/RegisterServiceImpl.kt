package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.domain.Register
import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.repository.RegisterRepository
import com.github.vhromada.catalog.service.RegisterService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

/**
 * A class represents implementation of service for registers.
 *
 * @author Vladimir Hromada
 */
@Service("registerService")
class RegisterServiceImpl(

    /**
     * Repository for registers
     */
    private val repository: RegisterRepository

) : RegisterService {

    override fun getAll(): List<Register> {
        return repository.findAll()
    }

    override fun get(type: RegisterType): Register {
        return getByType(type = type)
    }

    override fun checkValue(type: RegisterType, code: String) {
        getByType(type = type).values.find { it.code == code }
            ?: throw InputException(key = "REGISTER_VALUE_NOT_EXIST", message = "Register's value doesn't exist.", httpStatus = HttpStatus.NOT_FOUND)
    }

    /**
     * Returns register.
     *
     * @param type type of register
     * @return register
     * @throws InputException if register doesn't exist in data storage
     */
    private fun getByType(type: RegisterType): Register {
        return repository.findByNumber(number = type.number)
            .orElseThrow { InputException(key = "REGISTER_NOT_EXIST", message = "Register doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

}
