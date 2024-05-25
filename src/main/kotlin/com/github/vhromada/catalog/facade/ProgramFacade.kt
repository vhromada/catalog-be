package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.entity.Program
import com.github.vhromada.catalog.entity.ProgramStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeProgramRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException

/**
 * An interface represents facade for programs.
 *
 * @author Vladimir Hromada
 */
interface ProgramFacade {

    /**
     * Returns page of programs for filter.
     *
     * @param filter filter
     * @return page of programs for filter
     */
    fun search(filter: NameFilter): Page<Program>

    /**
     * Returns program.
     *
     * @param uuid UUID
     * @return program
     * @throws InputException if program doesn't exist in data storage
     */
    fun get(uuid: String): Program

    /**
     * Adds program.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Format is null
     *  * Crack is null
     *  * Serial key is null
     *  * Format doesn't exist in data storage
     *
     * @param request request for changing program
     * @return created program
     * @throws InputException if request for changing program isn't valid
     */
    fun add(request: ChangeProgramRequest): Program

    /**
     * Updates program.
     * <br></br>
     * Validation errors:
     *
     *  * Name is null
     *  * Name is empty string
     *  * Count of media is null
     *  * Count of media isn't positive number
     *  * Format is null
     *  * Crack is null
     *  * Serial key is null
     *  * Format doesn't exist in data storage
     *  * Program doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing program
     * @return updated program
     * @throws InputException if request for changing program isn't valid
     */
    fun update(uuid: String, request: ChangeProgramRequest): Program

    /**
     * Removes program.
     *
     * @param uuid UUID
     * @throws InputException if program doesn't exist in data storage
     */
    fun remove(uuid: String)

    /**
     * Duplicates data.
     *
     * @param uuid UUID
     * @return created duplicated program
     * @throws InputException if program doesn't exist in data storage
     */
    fun duplicate(uuid: String): Program

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    fun getStatistics(): ProgramStatistics

}
