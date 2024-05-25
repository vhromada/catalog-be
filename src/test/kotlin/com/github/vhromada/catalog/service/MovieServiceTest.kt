package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Movie
import com.github.vhromada.catalog.domain.filter.MovieFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.MovieMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.MovieRepository
import com.github.vhromada.catalog.service.impl.MovieServiceImpl
import com.github.vhromada.catalog.utils.MediumUtils
import com.github.vhromada.catalog.utils.MovieUtils
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
 * A class represents test for class [MovieService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class MovieServiceTest {

    /**
     * Instance of [MovieRepository]
     */
    @Mock
    private lateinit var repository: MovieRepository

    /**
     * Instance of [MovieMapper]
     */
    @Mock
    private lateinit var mapper: MovieMapper

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [MovieService]
     */
    private lateinit var service: MovieService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = MovieServiceImpl(repository = repository, mapper = mapper, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [MovieService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(MovieUtils.getDomainMovie(index = 1), MovieUtils.getDomainMovie(index = 2)))
        whenever(repository.findAll(any<Specification<Movie>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = MovieFilter(czechName = "czech", originalName = "original"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(any<Specification<Movie>>(), eq(pageable))
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [MovieService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(MovieUtils.getDomainMovie(index = 1), MovieUtils.getDomainMovie(index = 2)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = MovieFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(pageable)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [MovieService.get] with existing movie.
     */
    @Test
    fun getExisting() {
        val movie = MovieUtils.getDomainMovie(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(movie))

        val result = service.get(uuid = movie.uuid)

        assertThat(result).isEqualTo(movie)
        verify(repository).findByUuid(uuid = movie.uuid)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [MovieService.get] with not existing movie.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MOVIE_NOT_EXIST")
            .hasMessageContaining("Movie doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [MovieService.store].
     */
    @Test
    fun store() {
        val movie = MovieUtils.getDomainMovie(index = 1)
        whenever(repository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(movie = movie)

        assertThat(result).isSameAs(movie)
        verify(repository).save(movie)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [MovieService.remove].
     */
    @Test
    fun remove() {
        val movie = MovieUtils.getDomainMovie(index = 1)

        service.remove(movie = movie)

        verify(repository).delete(movie)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [MovieService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedMovie = MovieUtils.getDomainMovie(index = 1)
            .copy(id = 0, uuid = TestConstants.UUID, media = mutableListOf(MediumUtils.getDomainMedium(index = 1).copy(id = null)))
        val copyArgumentCaptor = argumentCaptor<Movie>()
        whenever(repository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Movie
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(movie = MovieUtils.getDomainMovie(index = 1))

        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = result)
        verify(repository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(repository, uuidProvider)
        verifyNoInteractions(mapper)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [MovieService.getStatistics].
     */
    @Test
    fun getStatistics() {
        val mediaStatistics = MediumUtils.getStatistics()
        val movieStatistics = MovieUtils.getStatistics()
        whenever(repository.count()).thenReturn(MovieUtils.MOVIES_COUNT.toLong())
        whenever(repository.getMediaStatistics()).thenReturn(mediaStatistics)
        whenever(mapper.mapStatistics(count = any(), mediaStatistics = any())).thenReturn(movieStatistics)

        val result = service.getStatistics()

        assertThat(result).isEqualTo(movieStatistics)
        verify(repository).count()
        verify(repository).getMediaStatistics()
        verify(mapper).mapStatistics(count = MovieUtils.MOVIES_COUNT.toLong(), mediaStatistics = mediaStatistics)
        verifyNoMoreInteractions(repository, mapper)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain movie.
     *
     * @return any mock for domain movie
     */
    private fun anyDomain(): Movie {
        return any()
    }

}
