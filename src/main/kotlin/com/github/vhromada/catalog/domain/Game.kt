package com.github.vhromada.catalog.domain

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents game.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "games")
@Suppress("JpaDataSourceORMInspection")
data class Game(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "game_generator", sequenceName = "games_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    @Column(name = "game_name")
    var name: String,

    /**
     * Normalized name
     */
    @Column(name = "normalized_game_name")
    var normalizedName: String,

    /**
     * URL to english Wikipedia page about game
     */
    @Column(name = "wiki_en")
    var wikiEn: String?,

    /**
     * URL to czech Wikipedia page about game
     */
    @Column(name = "wiki_cz")
    var wikiCz: String?,

    /**
     * Count of media
     */
    @Column(name = "media_count")
    var mediaCount: Int,

    /**
     * Format
     */
    var format: String,

    /**
     * Cheat
     */
    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "cheat")
    var cheat: Cheat?,

    /**
     * True if there is crack
     */
    var crack: Boolean,

    /**
     * True if there is serial key
     */
    @Column(name = "serial_key")
    var serialKey: Boolean,

    /**
     * True if there is patch
     */
    var patch: Boolean,

    /**
     * True if there is trainer
     */
    var trainer: Boolean,

    /**
     * True if there is data for trainer
     */
    @Column(name = "trainer_data")
    var trainerData: Boolean,

    /**
     * True if there is editor
     */
    @Column(name = "editor")
    var editor: Boolean,

    /**
     * True if there are saves
     */
    var saves: Boolean,

    /**
     * Other data
     */
    @Column(name = "other_data")
    var otherData: String?,

    /**
     * Note
     */
    var note: String?

) : Audit() {

    /**
     * Merges game.
     *
     * @param game game
     */
    @Suppress("DuplicatedCode")
    fun merge(game: Game) {
        name = game.name
        normalizedName = game.normalizedName
        wikiEn = game.wikiEn
        wikiCz = game.wikiCz
        mediaCount = game.mediaCount
        format = game.format
        crack = game.crack
        serialKey = game.serialKey
        patch = game.patch
        trainer = game.trainer
        trainerData = game.trainerData
        editor = game.serialKey
        saves = game.saves
        otherData = game.otherData
        note = game.note
    }

}
