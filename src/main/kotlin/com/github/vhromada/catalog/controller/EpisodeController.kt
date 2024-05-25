package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Episode
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeEpisodeRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.EpisodeFacade
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
 * A class represents controller for episodes.
 *
 * @author Vladimir Hromada
 */
@RestController("episodeController")
@RequestMapping("rest/shows/{showUuid}/seasons/{seasonUuid}/episodes")
@Tag(name = "Shows")
class EpisodeController(

    /**
     * Facade for episodes
     */
    private val facade: EpisodeFacade

) {

    /**
     * Returns page of episodes for specified season and filter.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *
     * @param showUuid   show's UUID
     * @param seasonUuid season's UUID
     * @param filter     filter
     * @return page of episodes for specified season and filter
     */
    @GetMapping
    fun search(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String,
        filter: PagingFilter
    ): Page<Episode> {
        return facade.findAll(show = showUuid, season = seasonUuid, filter = filter)
    }

    /**
     * Returns episode.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *  * Episode doesn't exist in data storage
     *
     * @param showUuid    show's UUID
     * @param seasonUuid  season's UUID
     * @param episodeUuid episode's UUID
     * @return episode
     */
    @GetMapping("{episodeUuid}")
    fun get(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String,
        @PathVariable("episodeUuid") episodeUuid: String
    ): Episode {
        return facade.get(show = showUuid, season = seasonUuid, uuid = episodeUuid)
    }

    /**
     * Adds episode.
     * <br></br>
     * Validation errors:
     *
     *  * Number of episode is null
     *  * Number of episode isn't positive number
     *  * Name is null
     *  * Name is empty string
     *  * Length of episode is null
     *  * Length of episode is negative value
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *
     * @param showUuid   show's UUID
     * @param seasonUuid season's UUID
     * @param request    request for changing episode
     * @return added episode
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String,
        @RequestBody request: ChangeEpisodeRequest
    ): Episode {
        return facade.add(show = showUuid, season = seasonUuid, request = request)
    }

    /**
     * Updates episode.
     * <br></br>
     * Validation errors:
     *
     *  * Number of episode is null
     *  * Number of episode isn't positive number
     *  * Name is null
     *  * Name is empty string
     *  * Length of episode is null
     *  * Length of episode is negative value
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *  * Episode doesn't exist in data storage
     *
     * @param showUuid    show's UUID
     * @param seasonUuid  season's UUID
     * @param episodeUuid episode's UUID
     * @param request     request for changing episode
     * @return updated episode
     */
    @PutMapping("{episodeUuid}")
    fun update(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String,
        @PathVariable("episodeUuid") episodeUuid: String,
        @RequestBody request: ChangeEpisodeRequest
    ): Episode {
        return facade.update(show = showUuid, season = seasonUuid, uuid = episodeUuid, request = request)
    }

    /**
     * Removes episode.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *  * Episode doesn't exist in data storage
     *
     * @param showUuid    show's UUID
     * @param seasonUuid  season's UUID
     * @param episodeUuid episode's UUID
     */
    @DeleteMapping("{episodeUuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String,
        @PathVariable("episodeUuid") episodeUuid: String
    ) {
        facade.remove(show = showUuid, season = seasonUuid, uuid = episodeUuid)
    }

    /**
     * Duplicates episode.
     * <br></br>
     * Validation errors:
     *
     *  * Show doesn't exist in data storage
     *  * Season doesn't exist in data storage
     *  * Episode doesn't exist in data storage
     *
     * @param showUuid    show's UUID
     * @param seasonUuid  season's UUID
     * @param episodeUuid episode's UUID
     * @return duplicated episode
     */
    @PostMapping("{episodeUuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(
        @PathVariable("showUuid") showUuid: String,
        @PathVariable("seasonUuid") seasonUuid: String,
        @PathVariable("episodeUuid") episodeUuid: String
    ): Episode {
        return facade.duplicate(show = showUuid, season = seasonUuid, uuid = episodeUuid)
    }

}
