package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Picture
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

/**
 * An interface represents repository for pictures.
 *
 * @author Vladimir Hromada
 */
interface PictureRepository : JpaRepository<Picture, Int> {

    /**
     * Finds picture by UUID.
     *
     * @param uuid UUID
     * @return picture
     */
    fun findByUuid(uuid: String): Optional<Picture>

}
