package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Genre
import com.github.vhromada.catalog.domain.filter.GenreFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.GenreRepository
import com.github.vhromada.catalog.service.impl.GenreServiceImpl
import com.github.vhromada.catalog.utils.GenreUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [GenreService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class GenreServiceTest {

    /**
     * Instance of [GenreRepository]
     */
    @Mock
    private lateinit var repository: GenreRepository

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [GenreService]
     */
    private lateinit var service: GenreService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = GenreServiceImpl(repository = repository, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [GenreService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(GenreUtils.getDomainGenre(index = 1), GenreUtils.getDomainGenre(index = 2)))
        whenever(repository.findAll(any<Specification<Genre>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = GenreFilter(name = "Name"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(any<Specification<Genre>>(), eq(pageable))
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [GenreService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(GenreUtils.getDomainGenre(index = 1), GenreUtils.getDomainGenre(index = 2)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = GenreFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(pageable)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [GenreService.get] with existing genre.
     */
    @Test
    fun getExisting() {
        val genre = GenreUtils.getDomainGenre(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(genre))

        val result = service.get(uuid = genre.uuid)

        assertThat(result).isEqualTo(genre)
        verify(repository).findByUuid(uuid = genre.uuid)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [GenreService.get] with not existing genre.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GENRE_NOT_EXIST")
            .hasMessageContaining("Genre doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [GenreService.store].
     */
    @Test
    fun store() {
        val genre = GenreUtils.getDomainGenre(index = 1)
        whenever(repository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(genre = genre)

        assertThat(result).isSameAs(genre)
        verify(repository).save(genre)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [GenreService.remove].
     */
    @Test
    fun remove() {
        val genre = GenreUtils.getDomainGenre(index = 1)

        service.remove(genre = genre)

        verify(repository).delete(genre)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [GenreService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedGenre = GenreUtils.getDomainGenre(index = 1)
            .copy(id = 0, uuid = TestConstants.UUID)
        val copyArgumentCaptor = argumentCaptor<Genre>()
        whenever(repository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Genre
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(genre = GenreUtils.getDomainGenre(index = 1))

        GenreUtils.assertGenreDeepEquals(expected = expectedGenre, actual = result)
        verify(repository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(repository, uuidProvider)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [GenreService.getCount].
     */
    @Test
    fun getCount() {
        whenever(repository.count()).thenReturn(GenreUtils.GENRES_COUNT.toLong())

        val result = service.getCount()

        assertThat(result).isEqualTo(GenreUtils.GENRES_COUNT.toLong())
        verify(repository).count()
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain genre.
     *
     * @return any mock for domain genre
     */
    private fun anyDomain(): Genre {
        return any()
    }

}
