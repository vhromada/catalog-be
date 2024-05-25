package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Author
import com.github.vhromada.catalog.entity.AuthorStatistics
import com.github.vhromada.catalog.entity.filter.AuthorFilter
import com.github.vhromada.catalog.entity.io.ChangeAuthorRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.AuthorFacade
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
 * A class represents controller for authors.
 *
 * @author Vladimir Hromada
 */
@RestController("authorController")
@RequestMapping("rest/authors")
@Tag(name = "Authors")
class AuthorController(

    /**
     * Facade for authors
     */
    private val facade: AuthorFacade

) {

    /**
     * Returns page of authors for filter.
     *
     * @param filter filter
     * @return page of authors for filter
     */
    @GetMapping
    fun search(filter: AuthorFilter): Page<Author> {
        return facade.search(filter = filter)
    }

    /**
     * Returns author.
     * <br></br>
     * Validation errors:
     *
     *  * Author doesn't exist in data storage
     *
     * @param uuid UUID
     * @return author
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable("uuid") uuid: String): Author {
        return facade.get(uuid = uuid)
    }

    /**
     * Adds author.
     * <br></br>
     * Validation errors:
     *
     *  * First name is null
     *  * First name is empty string
     *  * Last name is null
     *  * Last name is empty string
     *
     * @param request request for changing author
     * @return added author
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeAuthorRequest): Author {
        return facade.add(request = request)
    }

    /**
     * Updates author.
     * <br></br>
     * Validation errors:
     *
     *  * First name is null
     *  * First name is empty string
     *  * Last name is null
     *  * Last name is empty string
     *  * Author doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing author
     * @return updated author
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable("uuid") uuid: String,
        @RequestBody request: ChangeAuthorRequest
    ): Author {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes author.
     * <br></br>
     * Validation errors:
     *
     *  * Author doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("uuid") uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates author.
     * <br></br>
     * Validation errors:
     *
     *  * Author doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated author
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable("uuid") uuid: String): Author {
        return facade.duplicate(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): AuthorStatistics {
        return facade.getStatistics()
    }

}
