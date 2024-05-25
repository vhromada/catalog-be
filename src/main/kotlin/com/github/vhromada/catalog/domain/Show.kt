package com.github.vhromada.catalog.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

/**
 * A class represents show.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "tv_shows")
@Suppress("JpaDataSourceORMInspection")
data class Show(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "show_generator", sequenceName = "tv_shows_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "show_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Czech name
     */
    @Column(name = "czech_name")
    var czechName: String,

    /**
     * Normalized czech name
     */
    @Column(name = "normalized_czech_name")
    var normalizedCzechName: String,

    /**
     * Original name
     */
    @Column(name = "original_name")
    var originalName: String,

    /**
     * Normalized original name
     */
    @Column(name = "normalized_original_name")
    var normalizedOriginalName: String,

    /**
     * URL to ČSFD page about show
     */
    var csfd: String?,

    /**
     * IMDB code
     */
    @Column(name = "imdb_code")
    var imdbCode: Int?,

    /**
     * URL to english Wikipedia page about show
     */
    @Column(name = "wiki_en")
    var wikiEn: String?,

    /**
     * URL to czech Wikipedia page about show
     */
    @Column(name = "wiki_cz")
    var wikiCz: String?,

    /**
     * Picture ID
     */
    var picture: Int?,

    /**
     * Note
     */
    var note: String?,

    /**
     * Genres
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tv_show_genres", joinColumns = [JoinColumn(name = "tv_show")], inverseJoinColumns = [JoinColumn(name = "genre")])
    @OrderBy("name")
    @Fetch(FetchMode.SELECT)
    val genres: MutableList<Genre>,

    /**
     * Seasons
     */
    @OneToMany(mappedBy = "show", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("number")
    @Fetch(FetchMode.SELECT)
    val seasons: MutableList<Season>

) : Audit() {

    /**
     * Merges show.
     *
     * @param show show
     */
    @Suppress("DuplicatedCode")
    fun merge(show: Show) {
        czechName = show.czechName
        normalizedCzechName = show.normalizedCzechName
        originalName = show.originalName
        normalizedOriginalName = show.normalizedOriginalName
        csfd = show.csfd
        imdbCode = show.imdbCode
        wikiEn = show.wikiEn
        wikiCz = show.wikiCz
        note = show.note
    }

}
