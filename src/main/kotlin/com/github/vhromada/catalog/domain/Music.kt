package com.github.vhromada.catalog.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

/**
 * A class represents music.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "music")
@Suppress("JpaDataSourceORMInspection")
data class Music(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "music_generator", sequenceName = "music_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "music_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    @Column(name = "music_name")
    var name: String,

    /**
     * Normalized name
     */
    @Column(name = "normalized_music_name")
    var normalizedName: String,

    /**
     * URL to english Wikipedia page about music
     */
    @Column(name = "wiki_en")
    var wikiEn: String?,

    /**
     * URL to czech Wikipedia page about music
     */
    @Column(name = "wiki_cz")
    var wikiCz: String?,

    /**
     * Count of media
     */
    @Column(name = "media_count")
    var mediaCount: Int,

    /**
     * Note
     */
    var note: String?,

    /**
     * Songs
     */
    @OneToMany(mappedBy = "music", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id")
    @Fetch(FetchMode.SELECT)
    val songs: MutableList<Song>

) : Audit() {

    /**
     * Merges music.
     *
     * @param music music
     */
    fun merge(music: Music) {
        name = music.name
        normalizedName = music.normalizedName
        wikiEn = music.wikiEn
        wikiCz = music.wikiCz
        mediaCount = music.mediaCount
        note = music.note
    }

}
