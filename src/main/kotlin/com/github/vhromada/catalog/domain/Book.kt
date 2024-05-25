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
 * A class represents book.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "books")
@Suppress("JpaDataSourceORMInspection")
data class Book(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "book_generator", sequenceName = "books_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_generator")
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
     * Description
     */
    var description: String,

    /**
     * Returns note.
     *
     * @return note
     */
    var note: String?,

    /**
     * Authors
     */
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_authors", joinColumns = [JoinColumn(name = "book")], inverseJoinColumns = [JoinColumn(name = "author")])
    @OrderBy("normalizedLastName, normalizedFirstName")
    @Fetch(FetchMode.SELECT)
    val authors: MutableList<Author>,

    /**
     * Items
     */
    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER, cascade = [CascadeType.ALL], orphanRemoval = true)
    @OrderBy("id")
    @Fetch(FetchMode.SELECT)
    val items: MutableList<BookItem>

) : Audit() {

    /**
     * Merges book.
     *
     * @param book book
     */
    fun merge(book: Book) {
        czechName = book.czechName
        normalizedCzechName = book.normalizedCzechName
        originalName = book.originalName
        normalizedOriginalName = book.normalizedOriginalName
        description = book.description
        note = book.note
    }

}
