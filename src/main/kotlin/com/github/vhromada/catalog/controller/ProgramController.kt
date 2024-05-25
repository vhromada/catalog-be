package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Program
import com.github.vhromada.catalog.entity.ProgramStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeProgramRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.ProgramFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents controller for programs.
 *
 * @author Vladimir Hromada
 */
@RestController("programController")
@RequestMapping("rest/programs")
@Tag(name = "Programs")
class ProgramController(

    /**
     * Facade for programs
     */
    private val facade: ProgramFacade

) {

    /**
     * Returns list of programs for filter.
     *
     * @param filter filter
     * @return list of programs for filter
     */
    @GetMapping
    fun search(filter: NameFilter): Page<Program> {
        return facade.search(filter = filter)
    }

    /**
     * Returns program.
     * <br></br>
     * Validation errors:
     *
     *  * Program doesn't exist in data storage
     *
     * @param uuid UUID
     * @return program
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable("uuid") uuid: String): Program {
        return facade.get(uuid = uuid)
    }

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
     *
     * @param request request for changing program
     * @return added program
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeProgramRequest): Program {
        return facade.add(request = request)
    }

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
     *  * Program doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing program
     * @return updated program
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable("uuid") uuid: String,
        @RequestBody request: ChangeProgramRequest
    ): Program {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes program.
     * <br></br>
     * Validation errors:
     *
     *  * Program doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("uuid") uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates program.
     * <br></br>
     * Validation errors:
     *
     *  * Program doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated program
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable("uuid") uuid: String): Program {
        return facade.duplicate(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): ProgramStatistics {
        return facade.getStatistics()
    }

}
