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
 * A class represents walkthrough.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "walkthroughs")
@Suppress("JpaDataSourceORMInspection")
data class Walkthrough(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "walkthrough_generator", sequenceName = "walkthroughs_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "walkthrough_generator")
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
    @OneToOne(mappedBy = "walkthrough", fetch = FetchType.LAZY)
    var game: Game? = null

) : Audit() {

    /**
     * Merges walkthrough.
     *
     * @param walkthrough walkthrough
     */
    fun merge(walkthrough: Walkthrough) {
        content = walkthrough.content
    }

    override fun toString(): String {
        return "Walkthrough(id=$id, uuid='$uuid', content='$content', game=${game?.id})"
    }

}
