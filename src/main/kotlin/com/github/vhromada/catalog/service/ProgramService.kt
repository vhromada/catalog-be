package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Program
import com.github.vhromada.catalog.domain.filter.ProgramFilter
import com.github.vhromada.catalog.entity.ProgramStatistics
import com.github.vhromada.catalog.exception.InputException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

/**
 * An interface represents service for programs.
 *
 * @author Vladimir Hromada
 */
interface ProgramService {

    /**
     * Returns page of programs by filter.
     *
     * @param filter   filter
     * @param pageable paging information
     * @return page of programs by filter
     */
    fun search(filter: ProgramFilter, pageable: Pageable): Page<Program>

    /**
     * Returns program.
     *
     * @param uuid UUID
     * @return program
     * @throws InputException if program doesn't exist in data storage
     */
    fun get(uuid: String): Program

    /**
     * Stores program.
     *
     * @param program program
     * @return stored program
     */
    fun store(program: Program): Program

    /**
     * Removes program.
     *
     * @param program program
     */
    fun remove(program: Program)

    /**
     * Duplicates program.
     *
     * @param program program
     * @return duplicated program
     */
    fun duplicate(program: Program): Program

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): ProgramStatistics

}
