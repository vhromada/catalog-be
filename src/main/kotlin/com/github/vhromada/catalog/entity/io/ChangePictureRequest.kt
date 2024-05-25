package com.github.vhromada.catalog.entity.io

import com.github.vhromada.catalog.domain.Picture

/**
 * A class represents request for changing picture.
 *
 * @author Vladimir Hromada
 */
data class ChangePictureRequest(

    /**
     * Content
     */
    val content: ByteArray?

) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is Picture) {
            false
        } else {
            //TODO vhromada 07.04.2024: content.contentEquals(other.content)
            true
        }
    }

    override fun hashCode(): Int {
        return content?.contentHashCode() ?: 0
    }

}
