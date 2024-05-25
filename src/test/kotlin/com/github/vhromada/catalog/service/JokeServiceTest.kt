package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Joke
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.JokeRepository
import com.github.vhromada.catalog.service.impl.JokeServiceImpl
import com.github.vhromada.catalog.utils.JokeUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [JokeService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class JokeServiceTest {

    /**
     * Instance of [JokeRepository]
     */
    @Mock
    private lateinit var repository: JokeRepository

    /**
     * Instance of [JokeService]
     */
    private lateinit var service: JokeService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = JokeServiceImpl(repository = repository)
    }

    /**
     * Test method for [JokeService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(JokeUtils.getDomainJoke(index = 1), JokeUtils.getDomainJoke(index = 2)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(eq(pageable))
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [JokeService.get] with existing joke.
     */
    @Test
    fun getExisting() {
        val joke = JokeUtils.getDomainJoke(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(joke))

        val result = service.get(uuid = joke.uuid)

        assertThat(result).isEqualTo(joke)
        verify(repository).findByUuid(uuid = joke.uuid)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [JokeService.get] with not existing joke.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("JOKE_NOT_EXIST")
            .hasMessageContaining("Joke doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [JokeService.store].
     */
    @Test
    fun store() {
        val joke = JokeUtils.getDomainJoke(index = 1)
        whenever(repository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(joke = joke)

        assertThat(result).isSameAs(joke)
        verify(repository).save(joke)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [JokeService.remove].
     */
    @Test
    fun remove() {
        val joke = JokeUtils.getDomainJoke(index = 1)

        service.remove(joke = joke)

        verify(repository).delete(joke)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [JokeService.getCount].
     */
    @Test
    fun getCount() {
        whenever(repository.count()).thenReturn(JokeUtils.JOKES_COUNT.toLong())

        val result = service.getCount()

        assertThat(result).isEqualTo(JokeUtils.JOKES_COUNT.toLong())
        verify(repository).count()
        verifyNoMoreInteractions(repository)
    }

    /**
     * Returns any mock for domain joke.
     *
     * @return any mock for domain joke
     */
    private fun anyDomain(): Joke {
        return any()
    }

}
