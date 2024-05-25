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
import jakarta.persistence.OneToOne
import jakarta.persistence.OrderBy
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

/**
 * A class represents cheat.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "cheats")
@Suppress("JpaDataSourceORMInspection")
data class Cheat(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "cheat_generator", sequenceName = "cheats_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cheat_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Setting for game
     */
    @Column(name = "game_setting")
    var gameSetting: String?,

    /**
     * Setting for cheat
     */
    @Column(name = "cheat_setting")
    var cheatSetting: String?,

    /**
     * Data
     */
    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinTable(name = "cheat_cheat_data", joinColumns = [JoinColumn(name = "cheat")], inverseJoinColumns = [JoinColumn(name = "cheat_data")])
    @OrderBy("id")
    @Fetch(FetchMode.SELECT)
    val data: MutableList<CheatData>,

    /**
     * Game
     */
    @OneToOne(mappedBy = "cheat", fetch = FetchType.LAZY)
    var game: Game? = null

) : Audit() {

    /**
     * Merges cheat.
     *
     * @param cheat cheat
     */
    fun merge(cheat: Cheat) {
        gameSetting = cheat.gameSetting
        cheatSetting = cheat.cheatSetting
    }

    override fun toString(): String {
        return "Cheat(id=$id, uuid='$uuid', gameSetting='$gameSetting', cheatSetting='$cheatSetting', data=$data, game=${game?.id})"
    }

}
