package com.github.vhromada.catalog.entity

import java.util.Objects

/**
 * A class represents picture.
 *
 * @author Vladimir Hromada
 */
data class Picture(

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Content
     */
    val content: ByteArray,

    ) {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is Picture) {
            false
        } else {
            uuid == other.uuid && content.contentEquals(other.content)
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(uuid, content.contentHashCode())
    }

}
