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
 * A class represents song.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "songs")
@Suppress("JpaDataSourceORMInspection")
data class Song(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "song_generator", sequenceName = "songs_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    @Column(name = "song_name")
    var name: String,

    /**
     * Length
     */
    @Column(name = "song_length")
    var length: Int,

    /**
     * Note
     */
    var note: String?,

    /**
     * Music
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "music")
    var music: Music? = null

) : Audit() {

    /**
     * Merges song.
     *
     * @param song song
     */
    fun merge(song: Song) {
        name = song.name
        length = song.length
        note = song.note
    }

    override fun toString(): String {
        return "Song(id=$id, uuid=$uuid, name=$name, length=$length, note=$note, music=${music?.id})"
    }

}
