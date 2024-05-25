package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.MovieFilter
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.GenreUtils
import com.github.vhromada.catalog.utils.MediumUtils
import com.github.vhromada.catalog.utils.MovieUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [MovieRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class MovieRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [MovieRepository]
     */
    @Autowired
    private lateinit var repository: MovieRepository

    /**
     * Test method for get movies.
     */
    @Test
    fun getMovies() {
        val movies = repository.findAll()

        MovieUtils.assertDomainMoviesDeepEquals(expected = MovieUtils.getDomainMovies(), actual = movies)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
        }
    }

    /**
     * Test method for get movie.
     */
    @Test
    fun getMovie() {
        for (i in 1..MovieUtils.MOVIES_COUNT) {
            val movie = repository.findById(i).orElse(null)

            MovieUtils.assertMovieDeepEquals(expected = MovieUtils.getDomainMovie(index = i), actual = movie)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
        }
    }

    /**
     * Test method for add movie.
     */
    @Test
    @DirtiesContext
    fun add() {
        val movie = MovieUtils.newDomainMovie(id = null)
            .copy(genres = mutableListOf(GenreUtils.getDomainGenre(entityManager = entityManager, id = 1)!!))
        val expectedMovie = MovieUtils.newDomainMovie(id = MovieUtils.MOVIES_COUNT + 1)
            .copy(
                media = mutableListOf(MediumUtils.newDomainMedium(id = MediumUtils.MEDIA_COUNT + 1).fillAudit(audit = AuditUtils.newAudit())),
                picture = null,
                genres = mutableListOf(GenreUtils.getDomainGenre(index = 1))
            ).fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(movie)

        assertSoftly {
            it.assertThat(movie.id).isEqualTo(MovieUtils.MOVIES_COUNT + 1)
            it.assertThat(movie.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(movie.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(movie.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(movie.updatedTime).isEqualTo(TestConstants.TIME)
        }
        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = MovieUtils.getDomainMovie(entityManager = entityManager, id = MovieUtils.MOVIES_COUNT + 1))

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT + 1)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT + 1)
        }
    }

    /**
     * Test method for update movie.
     */
    @Test
    fun update() {
        val movie = MovieUtils.getDomainMovie(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedMovie = MovieUtils.getDomainMovie(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(movie)

        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = MovieUtils.getDomainMovie(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
        }
    }

    /**
     * Test method for update movie with added medium.
     */
    @Test
    @DirtiesContext
    fun updateAddedMedium() {
        val movie = MovieUtils.getDomainMovie(entityManager = entityManager, id = 1)!!
            .updated()
        movie.media.add(MediumUtils.newDomainMedium(id = null))
        val expectedMovie = MovieUtils.getDomainMovie(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedMovie.media.add(MediumUtils.newDomainMedium(id = MediumUtils.MEDIA_COUNT + 1).fillAudit(audit = AuditUtils.newAudit()))

        repository.saveAndFlush(movie)

        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = MovieUtils.getDomainMovie(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT + 1)
        }
    }

    /**
     * Test method for update movie with removed medium.
     */
    @Test
    fun updateRemovedMedium() {
        val movie = MovieUtils.getDomainMovie(entityManager = entityManager, id = 1)!!
            .updated()
        movie.media.clear()
        val expectedMovie = MovieUtils.getDomainMovie(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())
        expectedMovie.media.clear()

        repository.saveAndFlush(movie)

        MovieUtils.assertMovieDeepEquals(expected = expectedMovie, actual = MovieUtils.getDomainMovie(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT - MovieUtils.getDomainMovie(index = 1).media.size)
        }
    }

    /**
     * Test method for remove movie.
     */
    @Test
    fun remove() {
        repository.delete(MovieUtils.getDomainMovie(entityManager = entityManager, id = 1)!!)

        assertThat(MovieUtils.getDomainMovie(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT - 1)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT - MovieUtils.getDomainMovie(index = 1).media.size)
        }
    }

    /**
     * Test method for remove all movies.
     */
    @Test
    fun removeAll() {
        repository.deleteAll()

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(0)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(0)
        }
    }

    /**
     * Test method for count of movies.
     */
    @Test
    fun count() {
        val result = repository.count()

        assertThat(result).isEqualTo(MovieUtils.MOVIES_COUNT.toLong())

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
        }
    }

    /**
     * Test method for search movies by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..MovieUtils.MOVIES_COUNT) {
            val movie = MovieUtils.getDomainMovie(index = i)
            val filter = MovieFilter(czechName = movie.czechName, originalName = movie.originalName)

            val result = repository.findAll(filter.toSpecification())

            MovieUtils.assertDomainMoviesDeepEquals(expected = listOf(movie), actual = result.toList())
        }

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
        }
    }

    /**
     * Test method for find movie by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..MovieUtils.MOVIES_COUNT) {
            val movie = MovieUtils.getDomainMovie(index = i)

            val result = repository.findByUuid(uuid = movie.uuid).orElse(null)

            MovieUtils.assertMovieDeepEquals(expected = MovieUtils.getDomainMovie(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
        }
    }

    /**
     * Test method for get statistics for media.
     */
    @Test
    fun getMediaStatistics() {
        val result = repository.getMediaStatistics()

        MediumUtils.assertStatisticsDeepEquals(expected = MediumUtils.getStatistics(), actual = result)

        assertSoftly {
            it.assertThat(MovieUtils.getMoviesCount(entityManager = entityManager)).isEqualTo(MovieUtils.MOVIES_COUNT)
            it.assertThat(MediumUtils.getMediaCount(entityManager = entityManager)).isEqualTo(MediumUtils.MEDIA_COUNT)
        }
    }

}
