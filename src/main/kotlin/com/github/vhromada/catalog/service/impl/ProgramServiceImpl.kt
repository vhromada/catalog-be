package com.github.vhromada.catalog.service.impl

import com.github.vhromada.catalog.domain.Program
import com.github.vhromada.catalog.domain.filter.ProgramFilter
import com.github.vhromada.catalog.entity.ProgramStatistics
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.ProgramMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.ProgramRepository
import com.github.vhromada.catalog.service.ProgramService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents implementation of service for programs.
 *
 * @author Vladimir Hromada
 */
@Service("programService")
class ProgramServiceImpl(

    /**
     * Repository for programs
     */
    private val repository: ProgramRepository,

    /**
     * Mapper for programs
     */
    private val mapper: ProgramMapper,

    /**
     * Provider for UUID
     */
    private val uuidProvider: UuidProvider

) : ProgramService {

    override fun search(filter: ProgramFilter, pageable: Pageable): Page<Program> {
        if (filter.isEmpty()) {
            return repository.findAll(pageable)
        }
        return repository.findAll(filter.toSpecification(), pageable)
    }

    override fun get(uuid: String): Program {
        return repository.findByUuid(uuid = uuid)
            .orElseThrow { InputException(key = "PROGRAM_NOT_EXIST", message = "Program doesn't exist.", httpStatus = HttpStatus.NOT_FOUND) }
    }

    @Transactional
    override fun store(program: Program): Program {
        return repository.save(program)
    }

    @Transactional
    override fun remove(program: Program) {
        repository.delete(program)
    }

    @Transactional
    override fun duplicate(program: Program): Program {
        return repository.save(program.copy(id = null, uuid = uuidProvider.getUuid()))
    }

    override fun getStatistics(): ProgramStatistics {
        return mapper.mapStatistics(source = repository.getStatistics())
    }

}
