package com.github.vhromada.catalog.domain

import jakarta.persistence.Basic
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import java.util.Objects

/**
 * A class represents picture.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "pictures")
@Suppress("JpaDataSourceORMInspection")
data class Picture(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "picture_generator", sequenceName = "pictures_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "picture_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Content
     */
    @Basic(fetch = FetchType.LAZY)
    var content: ByteArray

) : Audit() {

    /**
     * Merges picture.
     *
     * @param picture picture
     */
    fun merge(picture: Picture) {
        content = picture.content
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        return if (other !is Picture) {
            false
        } else {
            id == other.id && uuid == other.uuid && content.contentEquals(other.content)
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(id, uuid, content.contentHashCode())
    }

}
