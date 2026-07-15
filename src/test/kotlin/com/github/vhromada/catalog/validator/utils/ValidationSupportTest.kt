package com.github.vhromada.catalog.validator.utils

import com.github.vhromada.catalog.common.result.Result
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

/**
 * A class represents test for class [ValidationSupport].
 *
 * @author Vladimir Hromada
 */
class ValidationSupportTest {

    /**
     * Test method for [ValidationSupport.validateNames].
     */
    @Test
    fun validateNames() {
        val result = Result<Unit>()

        ValidationSupport.validateNames(czechName = "czName", originalName = "origName", prefix = "MOVIE_", result = result)

        assertThat(result.isOk()).isTrue()
        assertThat(result.events()).isEmpty()
    }

    /**
     * Test method for [ValidationSupport.validateNames] with null czech name.
     */
    @Test
    fun validateNamesNullCzechName() {
        val result = Result<Unit>()

        ValidationSupport.validateNames(czechName = null, originalName = "origName", prefix = "MOVIE_", result = result)

        assertThat(result.isError()).isTrue()
        assertThat(result.events()).hasSize(1)
        assertThat(result.events()[0].key).isEqualTo("MOVIE_CZECH_NAME_NULL")
        assertThat(result.events()[0].message).isEqualTo("Czech name mustn't be null.")
    }

    /**
     * Test method for [ValidationSupport.validateNames] with empty czech name.
     */
    @Test
    fun validateNamesEmptyCzechName() {
        val result = Result<Unit>()

        ValidationSupport.validateNames(czechName = "", originalName = "origName", prefix = "MOVIE_", result = result)

        assertThat(result.isError()).isTrue()
        assertThat(result.events()).hasSize(1)
        assertThat(result.events()[0].key).isEqualTo("MOVIE_CZECH_NAME_EMPTY")
        assertThat(result.events()[0].message).isEqualTo("Czech name mustn't be empty string.")
    }

    /**
     * Test method for [ValidationSupport.validateNames] with null original name.
     */
    @Test
    fun validateNamesNullOriginalName() {
        val result = Result<Unit>()

        ValidationSupport.validateNames(czechName = "czName", originalName = null, prefix = "MOVIE_", result = result)

        assertThat(result.isError()).isTrue()
        assertThat(result.events()).hasSize(1)
        assertThat(result.events()[0].key).isEqualTo("MOVIE_ORIGINAL_NAME_NULL")
        assertThat(result.events()[0].message).isEqualTo("Original name mustn't be null.")
    }

    /**
     * Test method for [ValidationSupport.validateNames] with empty original name.
     */
    @Test
    fun validateNamesEmptyOriginalName() {
        val result = Result<Unit>()

        ValidationSupport.validateNames(czechName = "czName", originalName = "", prefix = "MOVIE_", result = result)

        assertThat(result.isError()).isTrue()
        assertThat(result.events()).hasSize(1)
        assertThat(result.events()[0].key).isEqualTo("MOVIE_ORIGINAL_NAME_EMPTY")
        assertThat(result.events()[0].message).isEqualTo("Original name mustn't be empty string.")
    }

    /**
     * Test method for [ValidationSupport.validateItems].
     */
    @Test
    fun validateItems() {
        val result = Result<Unit>()

        ValidationSupport.validateItems(
            items = listOf("action"),
            collectionKey = "MOVIE_GENRES",
            itemKey = "MOVIE_GENRE",
            collectionLabel = "Genres",
            itemLabel = "Genre",
            result = result
        )

        assertThat(result.isOk()).isTrue()
        assertThat(result.events()).isEmpty()
    }

    /**
     * Test method for [ValidationSupport.validateItems] with null items.
     */
    @Test
    fun validateItemsNullItems() {
        val result = Result<Unit>()

        ValidationSupport.validateItems(
            items = null,
            collectionKey = "MOVIE_GENRES",
            itemKey = "MOVIE_GENRE",
            collectionLabel = "Genres",
            itemLabel = "Genre",
            result = result
        )

        assertThat(result.isError()).isTrue()
        assertThat(result.events()).hasSize(1)
        assertThat(result.events()[0].key).isEqualTo("MOVIE_GENRES_NULL")
        assertThat(result.events()[0].message).isEqualTo("Genres mustn't be null.")
    }

    /**
     * Test method for [ValidationSupport.validateItems] with items with null value.
     */
    @Test
    fun validateItemsBadItems() {
        val result = Result<Unit>()

        ValidationSupport.validateItems(
            items = listOf(null),
            collectionKey = "MOVIE_GENRES",
            itemKey = "MOVIE_GENRE",
            collectionLabel = "Genres",
            itemLabel = "Genre",
            result = result
        )

        assertThat(result.isError()).isTrue()
        assertThat(result.events()).hasSize(1)
        assertThat(result.events()[0].key).isEqualTo("MOVIE_GENRES_CONTAIN_NULL")
        assertThat(result.events()[0].message).isEqualTo("Genres mustn't contain null value.")
    }

    /**
     * Test method for [ValidationSupport.validateItems] with items with empty item.
     */
    @Test
    fun validateItemsBadItem() {
        val result = Result<Unit>()

        ValidationSupport.validateItems(
            items = listOf(""),
            collectionKey = "MOVIE_GENRES",
            itemKey = "MOVIE_GENRE",
            collectionLabel = "Genres",
            itemLabel = "Genre",
            result = result
        )

        assertThat(result.isError()).isTrue()
        assertThat(result.events()).hasSize(1)
        assertThat(result.events()[0].key).isEqualTo("MOVIE_GENRE_EMPTY")
        assertThat(result.events()[0].message).isEqualTo("Genre mustn't be empty string.")
    }

}
