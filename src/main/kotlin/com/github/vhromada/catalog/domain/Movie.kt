package com.github.vhromada.catalog.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.ElementCollection
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
 * A class represents movie.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "movies")
@Suppress("JpaDataSourceORMInspection")
data class Movie(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "movie_generator", sequenceName = "movies_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "movie_generator")
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
     * Year
     */
    @Column(name = "movie_year")
    var year: Int,

    /**
     * Languages
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_languages", joinColumns = [JoinColumn(name = "movie")])
    @Fetch(FetchMode.SELECT)
    @Column(name = "movie_language")
    val languages: MutableList<String>,

    /**
     * Subtitles
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_subtitles", joinColumns = [JoinColumn(name = "movie")])
    @Fetch(FetchMode.SELECT)
    val subtitles: MutableList<String>,

    /**
     * Media
     */
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "movie_media", joinColumns = [JoinColumn(name = "movie")], inverseJoinColumns = [JoinColumn(name = "medium")])
    @OrderBy("id")
    @Fetch(FetchMode.SELECT)
    val media: MutableList<Medium>,

    /**
     * URL to ČSFD page about movie
     */
    var csfd: String?,

    /**
     * IMDB code
     */
    @Column(name = "imdb_code")
    var imdbCode: Int?,

    /**
     * URL to english Wikipedia page about movie
     */
    @Column(name = "wiki_en")
    var wikiEn: String?,

    /**
     * URL to czech Wikipedia page about movie
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
    @JoinTable(name = "movie_genres", joinColumns = [JoinColumn(name = "movie")], inverseJoinColumns = [JoinColumn(name = "genre")])
    @OrderBy("name")
    @Fetch(FetchMode.SELECT)
    val genres: MutableList<Genre>

) : Audit() {

    /**
     * Merges movie.
     *
     * @param movie movie
     */
    fun merge(movie: Movie) {
        czechName = movie.czechName
        normalizedCzechName = movie.normalizedCzechName
        originalName = movie.originalName
        normalizedOriginalName = movie.normalizedOriginalName
        year = movie.year
        languages.clear()
        languages.addAll(movie.languages)
        subtitles.clear()
        subtitles.addAll(movie.subtitles)
        csfd = movie.csfd
        imdbCode = movie.imdbCode
        wikiEn = movie.wikiEn
        wikiCz = movie.wikiCz
        note = movie.note
    }

}
