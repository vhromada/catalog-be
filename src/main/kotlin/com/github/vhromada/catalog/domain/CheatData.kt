package com.github.vhromada.catalog.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

/**
 * A class represents cheat's data.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "cheat_data")
@Suppress("JpaDataSourceORMInspection")
data class CheatData(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "cheat_data_generator", sequenceName = "cheat_data_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cheat_data_generator")
    var id: Int?,

    /**
     * Action
     */
    var action: String,

    /**
     * Description
     */
    var description: String

) : Audit() {

    /**
     * Merges cheat's data.
     *
     * @param cheatData cheat's data
     */
    fun merge(cheatData: CheatData) {
        action = cheatData.action
        description = cheatData.description
    }

}
