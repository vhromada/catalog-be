package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Season
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.SeasonFacade
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
 * A class represents controller for seasons.
 *
 * @author Vladimir Hromada
 */
@RestController("seasonController")
@RequestMapping("rest/shows/{showUuid}/seasons")
@Tag(name = "Shows")
class SeasonController(

    /**
     * Facade for seasons
     */
    private val facade: SeasonFacade

) {

    /**
     * Returns page of seasons for specified show and filter.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *
     * @param showUuid show's UUID
     * @param filter   filter
     * @return page of seasons for specified show and filter
     */
    @GetMapping
    fun search(
        @PathVariable("showUuid") showUuid: String,
        filter: PagingFilter
    ): Page<Season> {
        return facade.findAll(show = showUuid, filter = filter)
    }

    /**
     * Returns season.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *
     * @param showUuid   show's UUID
     * @param seasonUuid season's UUID
     * @return season
     */
    @GetMapping("{seasonUuid}")
    fun get(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String
    ): Season {
        return facade.get(show = showUuid, uuid = seasonUuid)
    }

    /**
     * Adds season.
     * <br></br>
     * Validation errors:
     *
     *  * Number of season is null
     *  * Number of season isn't positive number
     *  * Starting year is null
     *  * Starting year isn't between 1930 and current year
     *  * Ending year is null
     *  * Ending year isn't between 1930 and current year
     *  * Starting year is greater than ending year
     *  * Language is null
     *  * Subtitles are null
     *  * Subtitles contain null value
     *  * Show doesn't exist in season storage
     *
     * @param showUuid show's UUID
     * @param request  request for changing season
     * @return added season
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(
        @PathVariable("showUuid") showUuid: String,
        @RequestBody request: ChangeSeasonRequest
    ): Season {
        return facade.add(show = showUuid, request = request)
    }

    /**
     * Updates season.
     * <br></br>
     * Validation errors:
     *
     *  * Number of season is null
     *  * Number of season isn't positive number
     *  * Starting year is null
     *  * Starting year isn't between 1930 and current year
     *  * Ending year is null
     *  * Ending year isn't between 1930 and current year
     *  * Starting year is greater than ending year
     *  * Language is null
     *  * Subtitles are null
     *  * Subtitles contain null value
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *
     * @param showUuid   show's UUID
     * @param seasonUuid season's UUID
     * @param request    request for changing season
     * @return updated season
     */
    @PutMapping("{seasonUuid}")
    fun update(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String,
        @RequestBody request: ChangeSeasonRequest
    ): Season {
        return facade.update(show = showUuid, uuid = seasonUuid, request = request)
    }

    /**
     * Removes season.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *
     * @param showUuid   show's UUID
     * @param seasonUuid season's UUID
     */
    @DeleteMapping("{seasonUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String
    ) {
        facade.remove(show = showUuid, uuid = seasonUuid)
    }

    /**
     * Duplicates season.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *
     * @param showUuid   show's UUID
     * @param seasonUuid season's UUID
     * @return duplicated season
     */
    @PostMapping("{seasonUuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String
    ): Season {
        return facade.duplicate(show = showUuid, uuid = seasonUuid)
    }

}
