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
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

/**
 * A class represents season.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "seasons")
@Suppress("JpaDataSourceORMInspection")
data class Season(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "season_generator", sequenceName = "seasons_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "season_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Number of season
     */
    @Column(name = "season_number")
    var number: Int,

    /**
     * Starting year
     */
    @Column(name = "start_year")
    var startYear: Int,

    /**
     * Ending year
     */
    @Column(name = "end_year")
    var endYear: Int,

    /**
     * Language
     */
    @Column(name = "season_language")
    var language: String,

    /**
     * Subtitles
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "season_subtitles", joinColumns = [JoinColumn(name = "season")])
    @Fetch(FetchMode.SELECT)
    val subtitles: MutableList<String>,

    /**
     * Note
     */
    var note: String?,

    /**
     * Episodes
     */
    @OneToMany(mappedBy = "season", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("number")
    @Fetch(FetchMode.SELECT)
    val episodes: MutableList<Episode>,

    /**
     * Show
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tv_show")
    var show: Show? = null

) : Audit() {

    /**
     * Merges season.
     *
     * @param season season
     */
    fun merge(season: Season) {
        number = season.number
        startYear = season.startYear
        endYear = season.endYear
        language = season.language
        subtitles.clear()
        subtitles.addAll(season.subtitles)
        note = season.note
    }

    override fun toString(): String {
        return "Season(id=$id, uuid=$uuid, number=$number, startYear=$startYear, endYear=$endYear, language=$language, subtitles=$subtitles, note=$note, episodes=$episodes, show=${show?.id})"
    }

}
