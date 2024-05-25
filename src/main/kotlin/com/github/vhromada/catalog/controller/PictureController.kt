package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.PictureFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

/**
 * A class represents controller for pictures.
 *
 * @author Vladimir Hromada
 */
@RestController("pictureController")
@RequestMapping("rest/pictures")
@Tag(name = "Pictures")
class PictureController(

    /**
     * Facade for pictures
     */
    private val facade: PictureFacade

) {

    /**
     * Returns page of pictures for filter.
     *
     * @param filter filter
     * @return page of pictures for filter
     */
    @GetMapping
    fun search(filter: PagingFilter): Page<String> {
        return facade.search(filter = filter)
    }

    /**
     * Returns picture.
     * <br></br>
     * Validation errors:
     *
     *  * Picture doesn't exist in data storage
     *
     * @param id ID
     * @return picture
     */
    @GetMapping("{id}")
    fun get(@PathVariable("id") id: String): ResponseEntity<Resource> {
        val picture = facade.get(uuid = id)
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"picture.jpg\"")
            .header(HttpHeaders.CONTENT_TYPE, "image/jpg")
            .body(ByteArrayResource(picture.content))
    }

    /**
     * Adds picture.
     * <br></br>
     * Validation errors:
     *
     *  * File is empty
     *
     * @param file picture
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@RequestParam("file") file: MultipartFile) {
        if (file.isEmpty) {
            throw InputException(key = "FILE_EMPTY", message = "File mustn't be empty.")
        }
        facade.add(request = ChangePictureRequest(content = file.bytes))
    }

    /**
     * Removes picture.
     * <br></br>
     * Validation errors:
     *
     *  * Picture doesn't exist in data storage
     *
     * @param id ID
     */
    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun remove(@PathVariable("id") id: String) {
        facade.remove(uuid = id)
    }

}
