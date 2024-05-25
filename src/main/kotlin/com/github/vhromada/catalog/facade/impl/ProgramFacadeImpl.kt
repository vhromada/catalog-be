package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Program
import com.github.vhromada.catalog.entity.ProgramStatistics
import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeProgramRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.ProgramFacade
import com.github.vhromada.catalog.mapper.ProgramMapper
import com.github.vhromada.catalog.service.ProgramService
import com.github.vhromada.catalog.service.RegisterService
import com.github.vhromada.catalog.validator.ProgramValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for programs.
 *
 * @author Vladimir Hromada
 */
@Component("programFacade")
class ProgramFacadeImpl(

    /**
     * Service for programs
     */
    private val programService: ProgramService,

    /**
     * Service for registers
     */
    private val registerService: RegisterService,

    /**
     * Mapper for programs
     */
    private val mapper: ProgramMapper,

    /**
     * Validator for programs
     */
    private val validator: ProgramValidator

) : ProgramFacade {

    override fun search(filter: NameFilter): Page<Program> {
        val programs = programService.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedName", "id")))
        return Page(data = mapper.mapPrograms(source = programs.content), page = programs)
    }

    override fun get(uuid: String): Program {
        return mapper.mapProgram(source = programService.get(uuid = uuid))
    }

    override fun add(request: ChangeProgramRequest): Program {
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.PROGRAM_FORMATS, code = request.format!!)
        return mapper.mapProgram(source = programService.store(program = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangeProgramRequest): Program {
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.PROGRAM_FORMATS, code = request.format!!)
        val program = programService.get(uuid = uuid)
        program.merge(program = mapper.mapRequest(source = request))
        return mapper.mapProgram(source = programService.store(program = program))
    }

    override fun remove(uuid: String) {
        programService.remove(program = programService.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Program {
        return mapper.mapProgram(source = programService.duplicate(program = programService.get(uuid = uuid)))
    }

    override fun getStatistics(): ProgramStatistics {
        return programService.getStatistics()
    }

}
