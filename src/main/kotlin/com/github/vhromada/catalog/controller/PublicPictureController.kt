package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.facade.PictureFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * A class represents public controller for pictures.
 *
 * @author Vladimir Hromada
 */
@RestController("publicPictureController")
@RequestMapping("rest/public/pictures")
@Tag(name = "Pictures")
class PublicPictureController(

    /**
     * Facade for pictures
     */
    private val facade: PictureFacade

) {

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

}
