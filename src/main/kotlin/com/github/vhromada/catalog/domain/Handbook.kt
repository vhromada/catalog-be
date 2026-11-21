package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents handbook.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "handbooks")
@Suppress("JpaDataSourceORMInspection")
data class Handbook(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "handbook_generator", sequenceName = "handbooks_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "handbook_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Content
     */
    @Column(name = "content")
    var content: String,

    /**
     * Game
     */
    @OneToOne(mappedBy = "handbook", fetch = FetchType.LAZY)
    var game: Game? = null

) : Audit() {

    /**
     * Merges handbook.
     *
     * @param handbook handbook
     */
    fun merge(handbook: Handbook) {
        content = handbook.content
    }

    override fun toString(): String {
        return "Handbook(id=$id, uuid='$uuid', content='$content', game=${game?.id})"
    }

}
