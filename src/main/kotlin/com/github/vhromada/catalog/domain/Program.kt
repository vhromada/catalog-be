package com.github.vhromada.catalog.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents program.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "programs")
@Suppress("JpaDataSourceORMInspection")
data class Program(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "program_generator", sequenceName = "programs_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "program_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Name
     */
    @Column(name = "program_name")
    var name: String,

    /**
     * Normalized name
     */
    @Column(name = "normalized_program_name")
    var normalizedName: String,

    /**
     * URL to english Wikipedia page about program
     */
    @Column(name = "wiki_en")
    var wikiEn: String?,

    /**
     * URL to czech Wikipedia page about program
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
     * True if there is crack
     */
    var crack: Boolean,

    /**
     * True if there is serial key
     */
    @Column(name = "serial_key")
    var serialKey: Boolean,

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
     * Merges program.
     *
     * @param program program
     */
    @Suppress("DuplicatedCode")
    fun merge(program: Program) {
        name = program.name
        normalizedName = program.normalizedName
        wikiEn = program.wikiEn
        wikiCz = program.wikiCz
        mediaCount = program.mediaCount
        format = program.format
        crack = program.crack
        serialKey = program.serialKey
        otherData = program.otherData
        note = program.note
    }

}
