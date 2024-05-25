package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.domain.Program
import com.github.vhromada.catalog.domain.filter.ProgramFilter
import com.github.vhromada.catalog.domain.io.ProgramStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeProgramRequest

/**
 * An interface represents mapper for programs.
 *
 * @author Vladimir Hromada
 */
interface ProgramMapper {

    /**
     * Maps program.
     *
     * @param source program
     * @return mapped program
     */
    fun mapProgram(source: Program): com.github.vhromada.catalog.entity.Program

    /**
     * Maps list of programs.
     *
     * @param source list of programs
     * @return mapped list of programs
     */
    fun mapPrograms(source: List<Program>): List<com.github.vhromada.catalog.entity.Program>

    /**
     * Maps request for changing program.
     *
     * @param source request for changing program
     * @return mapped program
     */
    fun mapRequest(source: ChangeProgramRequest): Program

    /**
     * Maps filter for programs.
     *
     * @param source filter for name
     * @return mapped filter for programs
     */
    fun mapFilter(source: NameFilter): ProgramFilter

    /**
     * Maps statistics.
     *
     * @param source statistics
     * @return statistics
     */
    fun mapStatistics(source: ProgramStatistics): com.github.vhromada.catalog.entity.ProgramStatistics

}
