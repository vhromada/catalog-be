package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Handbook
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.repository.HandbookRepository
import com.github.vhromada.catalog.service.impl.HandbookServiceImpl
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.HandbookUtils
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [HandbookService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class HandbookServiceTest {

    /**
     * Instance of [HandbookRepository]
     */
    @Mock
    private lateinit var handbookRepository: HandbookRepository

    /**
     * Instance of [GameRepository]
     */
    @Mock
    private lateinit var gameRepository: GameRepository

    /**
     * Instance of [HandbookService]
     */
    private lateinit var service: HandbookService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = HandbookServiceImpl(handbookRepository = handbookRepository, gameRepository = gameRepository)
    }

    /**
     * Test method for [HandbookService.getByGame] with existing handbook.
     */
    @Test
    fun getByGameExisting() {
        val game = GameUtils.getDomainGame(index = 1)
        val handbook = game.handbook!!
        whenever(handbookRepository.findByGameId(id = any())).thenReturn(Optional.of(handbook))

        val result = service.getByGame(game = game.id!!)

        assertThat(result).isEqualTo(handbook)
        verify(handbookRepository).findByGameId(id = game.id!!)
        verifyNoMoreInteractions(handbookRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [HandbookService.getByGame] with not existing handbook.
     */
    @Test
    fun getByGameNotExisting() {
        whenever(handbookRepository.findByGameId(id = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getByGame(game = Int.MAX_VALUE) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_NOT_EXIST")
            .hasMessageContaining("Handbook doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(handbookRepository).findByGameId(id = Int.MAX_VALUE)
        verifyNoMoreInteractions(handbookRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [HandbookService.getByUuid] with existing handbook.
     */
    @Test
    fun getByUuidExisting() {
        val handbook = GameUtils.getDomainGame(index = 1).handbook!!
        whenever(handbookRepository.findByUuid(uuid = any())).thenReturn(Optional.of(handbook))

        val result = service.getByUuid(uuid = handbook.uuid)

        assertThat(result).isEqualTo(handbook)
        verify(handbookRepository).findByUuid(uuid = handbook.uuid)
        verifyNoMoreInteractions(handbookRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [HandbookService.getByUuid] with not existing handbook.
     */
    @Test
    fun getByUuidNotExisting() {
        whenever(handbookRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getByUuid(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("HANDBOOK_NOT_EXIST")
            .hasMessageContaining("Handbook doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(handbookRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(handbookRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [HandbookService.store] for adding.
     */
    @Test
    fun add() {
        val handbook = HandbookUtils.newDomainHandbook(id = null)
        whenever(handbookRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Handbook
            argument.id = 3
            argument
        }

        val result = service.store(handbook = handbook)

        assertSoftly {
            it.assertThat(result).isSameAs(handbook)
            it.assertThat(result.id).isEqualTo(3)
        }
        verify(handbookRepository).save(handbook)
        verifyNoMoreInteractions(handbookRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [HandbookService.store] for updating.
     */
    @Test
    fun update() {
        val handbook = GameUtils.getDomainGame(index = 1).handbook!!
        whenever(handbookRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(handbook = handbook)

        assertThat(result).isSameAs(handbook)
        verify(handbookRepository).save(handbook)
        verifyNoMoreInteractions(handbookRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [HandbookService.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getDomainGame(index = 1)
        val handbook = game.handbook!!

        service.remove(handbook = handbook)

        verify(gameRepository).save(game)
        verifyNoMoreInteractions(gameRepository)
        verifyNoInteractions(handbookRepository)
    }

    /**
     * Returns any mock for domain handbook.
     *
     * @return any mock for domain handbook
     */
    private fun anyDomain(): Handbook {
        return any()
    }

}
