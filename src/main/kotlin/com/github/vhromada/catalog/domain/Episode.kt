package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents episode.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "episodes")
@Suppress("JpaDataSourceORMInspection")
data class Episode(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "episode_generator", sequenceName = "episodes_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "episode_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Number of episode
     */
    @Column(name = "episode_number")
    var number: Int,

    /**
     * Name
     */
    @Column(name = "episode_name")
    var name: String,

    /**
     * Length
     */
    @Column(name = "episode_length")
    var length: Int,

    /**
     * Note
     */
    var note: String?,

    /**
     * Season
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season")
    var season: Season? = null

) : Audit() {

    /**
     * Merges episode.
     *
     * @param episode episode
     */
    fun merge(episode: Episode) {
        number = episode.number
        name = episode.name
        length = episode.length
        note = episode.note
    }

    override fun toString(): String {
        return "Episode(id=$id, uuid='$uuid', number=$number, name='$name', length=$length, note=$note, season=${season?.id})"
    }

}
