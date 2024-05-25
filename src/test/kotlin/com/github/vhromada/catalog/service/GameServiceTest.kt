package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Game
import com.github.vhromada.catalog.domain.filter.GameFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.GameMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.service.impl.GameServiceImpl
import com.github.vhromada.catalog.utils.GameUtils
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
 * A class represents test for class [GameService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class GameServiceTest {

    /**
     * Instance of [GameRepository]
     */
    @Mock
    private lateinit var repository: GameRepository

    /**
     * Instance of [GameMapper]
     */
    @Mock
    private lateinit var mapper: GameMapper

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [GameService]
     */
    private lateinit var service: GameService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = GameServiceImpl(repository = repository, mapper = mapper, uuidProvider = uuidProvider)
    }


    /**
     * Test method for [GameService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(GameUtils.getDomainGame(index = 1), GameUtils.getDomainGame(index = 2)))
        whenever(repository.findAll(any<Specification<Game>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = GameFilter(name = "Name"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(any<Specification<Game>>(), eq(pageable))
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [GameService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(GameUtils.getDomainGame(index = 1), GameUtils.getDomainGame(index = 2)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = GameFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(pageable)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [GameService.get] with existing game.
     */
    @Test
    fun getExisting() {
        val game = GameUtils.getDomainGame(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(game))

        val result = service.get(uuid = game.uuid)

        assertThat(result).isEqualTo(game)
        verify(repository).findByUuid(uuid = game.uuid)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [GameService.get] with not existing game.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("GAME_NOT_EXIST")
            .hasMessageContaining("Game doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [GameService.store].
     */
    @Test
    fun store() {
        val game = GameUtils.getDomainGame(index = 1)
        whenever(repository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(game = game)

        assertThat(result).isSameAs(game)
        verify(repository).save(game)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [GameService.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getDomainGame(index = 1)

        service.remove(game = game)

        verify(repository).delete(game)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [GameService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedGame = GameUtils.getDomainGame(index = 2)
            .copy(id = 0, uuid = TestConstants.UUID, cheat = null)
        val copyArgumentCaptor = argumentCaptor<Game>()
        whenever(repository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Game
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(game = GameUtils.getDomainGame(index = 2))

        GameUtils.assertGameDeepEquals(expected = expectedGame, actual = result)
        verify(repository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(repository, uuidProvider)
        verifyNoInteractions(mapper)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [GameService.getStatistics].
     */
    @Test
    fun getStatistics() {
        val domain = GameUtils.getDomainStatistics()
        val entity = GameUtils.getStatistics()
        whenever(repository.getStatistics()).thenReturn(domain)
        whenever(mapper.mapStatistics(source = any())).thenReturn(entity)

        val result = service.getStatistics()

        assertThat(result).isEqualTo(entity)
        verify(repository).getStatistics()
        verify(mapper).mapStatistics(domain)
        verifyNoMoreInteractions(repository, mapper)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain game.
     *
     * @return any mock for domain game
     */
    private fun anyDomain(): Game {
        return any()
    }

}
