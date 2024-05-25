package com.github.vhromada.catalog.domain

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
 * A class represents account.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "accounts")
@Suppress("JpaDataSourceORMInspection")
data class Account(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "account_generator", sequenceName = "accounts_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_generator")
    val id: Int? = null,

    /**
     * UUID
     */
    val uuid: String?,

    /**
     * Username
     */
    var username: String?,

    /**
     * Password
     */
    var password: String? = null,

    /**
     * True if account is locked
     */
    var locked: Boolean? = null,

    /**
     * Roles
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "account_roles", joinColumns = [JoinColumn(name = "account")], inverseJoinColumns = [JoinColumn(name = "role")])
    @OrderBy("id")
    @Fetch(FetchMode.SELECT)
    val roles: MutableList<Role>? = null

) {

    /**
     * Changes roles.
     *
     * @param roleList roles
     */
    fun changeRoles(roleList: List<Role>) {
        roles!!.clear()
        roles.addAll(roleList)
    }

    /**
     * Merges account.
     *
     * @param account account
     */
    fun merge(account: Account) {
        username = account.username
        password = account.password
    }

}
