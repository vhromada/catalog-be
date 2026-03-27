package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.Book
import com.github.vhromada.catalog.entity.BookStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeBookRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.BookFacade
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
 * A class represents controller for books.
 *
 * @author Vladimir Hromada
 */
@RestController("bookController")
@RequestMapping("rest/books")
@Tag(name = "Books")
class BookController(

    /**
     * Facade for books
     */
    private val facade: BookFacade

) {

    /**
     * Returns list of books for filter.
     *
     * @param filter filter
     * @return list of books for filter
     */
    @GetMapping
    fun search(filter: MultipleNameFilter): Page<Book> {
        return facade.search(filter = filter)
    }

    /**
     * Returns book.
     * <br></br>
     * Validation errors:
     *
     *  * Book doesn't exist in data storage
     *
     * @param uuid UUID
     * @return book
     */
    @GetMapping("{uuid}")
    fun get(@PathVariable uuid: String): Book {
        return facade.get(uuid = uuid)
    }

    /**
     * Adds book.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Description is null
     *  * Description is empty string
     *  * Authors are null
     *  * Authors contain null value
     *  * Authors is empty string
     *  * Author doesn't exist in data storage
     *
     * @param request request for changing book
     * @return added book
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestBody request: ChangeBookRequest): Book {
        return facade.add(request = request)
    }

    /**
     * Updates book.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *  * Description is null
     *  * Description is empty string
     *  * Authors are null
     *  * Authors contain null value
     *  * Authors is empty string
     *  * Author doesn't exist in data storage
     *  * Book doesn't exist in data storage
     *
     * @param uuid    UUID
     * @param request request for changing book
     * @return updated book
     */
    @PutMapping("{uuid}")
    fun update(
        @PathVariable uuid: String,
        @RequestBody request: ChangeBookRequest
    ): Book {
        return facade.update(uuid = uuid, request = request)
    }

    /**
     * Removes book.
     * <br></br>
     * Validation errors:
     *
     *  * Book doesn't exist in data storage
     *
     * @param uuid UUID
     */
    @DeleteMapping("{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable uuid: String) {
        facade.remove(uuid = uuid)
    }

    /**
     * Duplicates book.
     * <br></br>
     * Validation errors:
     *
     *  * Book doesn't exist in data storage
     *
     * @param uuid UUID
     * @return duplicated book
     */
    @PostMapping("{uuid}/duplicate")
    @ResponseStatus(HttpStatus.CREATED)
    fun duplicate(@PathVariable uuid: String): Book {
        return facade.duplicate(uuid = uuid)
    }

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @GetMapping("statistics")
    fun getStatistics(): BookStatistics {
        return facade.getStatistics()
    }

}
