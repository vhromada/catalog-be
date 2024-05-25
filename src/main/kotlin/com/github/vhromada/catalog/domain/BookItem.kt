package com.github.vhromada.catalog.domain

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
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode

/**
 * A class represents book item.
 *
 * @author Vladimir Hromada
 */
@Entity
@Table(name = "book_items")
@Suppress("JpaDataSourceORMInspection")
data class BookItem(

    /**
     * ID
     */
    @Id
    @SequenceGenerator(name = "book_item_generator", sequenceName = "book_items_sq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "book_item_generator")
    var id: Int?,

    /**
     * UUID
     */
    val uuid: String,

    /**
     * Languages
     */
    @Column(name = "book_item_language")
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "book_item_languages", joinColumns = [JoinColumn(name = "book_item")])
    @Fetch(FetchMode.SELECT)
    val languages: MutableList<String>,

    /**
     * Format
     */
    var format: String,

    /**
     * Note
     */
    var note: String?,

    /**
     * Book
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book")
    var book: Book? = null

) : Audit() {

    /**
     * Merges book's item.
     *
     * @param bookItem book's item
     */
    fun merge(bookItem: BookItem) {
        languages.clear()
        languages.addAll(bookItem.languages)
        format = bookItem.format
        note = bookItem.note
    }

    override fun toString(): String {
        return "BookItem(id=$id, uuid=$uuid, languages=$languages, format=$format, note=$note, book=${book?.id})"
    }

}
