package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Cheat
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.CheatRepository
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.service.impl.CheatServiceImpl
import com.github.vhromada.catalog.utils.CheatUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
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
 * A class represents test for class [CheatService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class CheatServiceTest {

    /**
     * Instance of [CheatRepository]
     */
    @Mock
    private lateinit var cheatRepository: CheatRepository

    /**
     * Instance of [GameRepository]
     */
    @Mock
    private lateinit var gameRepository: GameRepository

    /**
     * Instance of [CheatService]
     */
    private lateinit var service: CheatService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = CheatServiceImpl(cheatRepository = cheatRepository, gameRepository = gameRepository)
    }

    /**
     * Test method for [CheatService.getByGame] with existing cheat.
     */
    @Test
    fun getByGameExisting() {
        val game = GameUtils.getDomainGame(index = 2)
        val cheat = game.cheat!!
        whenever(cheatRepository.findByGameId(id = any())).thenReturn(Optional.of(cheat))

        val result = service.getByGame(game = game.id!!)

        assertThat(result).isEqualTo(cheat)
        verify(cheatRepository).findByGameId(id = game.id!!)
        verifyNoMoreInteractions(cheatRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [CheatService.getByGame] with not existing cheat.
     */
    @Test
    fun getByGameNotExisting() {
        whenever(cheatRepository.findByGameId(id = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getByGame(game = Int.MAX_VALUE) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_NOT_EXIST")
            .hasMessageContaining("Cheat doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(cheatRepository).findByGameId(id = Int.MAX_VALUE)
        verifyNoMoreInteractions(cheatRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [CheatService.getByUuid] with existing cheat.
     */
    @Test
    fun getByUuidExisting() {
        val cheat = GameUtils.getDomainGame(index = 2).cheat!!
        whenever(cheatRepository.findByUuid(uuid = any())).thenReturn(Optional.of(cheat))

        val result = service.getByUuid(uuid = cheat.uuid)

        assertThat(result).isEqualTo(cheat)
        verify(cheatRepository).findByUuid(uuid = cheat.uuid)
        verifyNoMoreInteractions(cheatRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [CheatService.getByUuid] with not existing cheat.
     */
    @Test
    fun getByUuidNotExisting() {
        whenever(cheatRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getByUuid(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("CHEAT_NOT_EXIST")
            .hasMessageContaining("Cheat doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(cheatRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(cheatRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [CheatService.store] for adding.
     */
    @Test
    fun add() {
        val cheat = CheatUtils.newDomainCheat(id = null)
        whenever(cheatRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Cheat
            argument.id = 2
            argument
        }

        val result = service.store(cheat = cheat)

        assertSoftly {
            it.assertThat(result).isSameAs(cheat)
            it.assertThat(result.id).isEqualTo(2)
        }
        verify(cheatRepository).save(cheat)
        verifyNoMoreInteractions(cheatRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [CheatService.store] for updating.
     */
    @Test
    fun update() {
        val cheat = GameUtils.getDomainGame(index = 2).cheat!!
        whenever(cheatRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(cheat = cheat)

        assertThat(result).isSameAs(cheat)
        verify(cheatRepository).save(cheat)
        verifyNoMoreInteractions(cheatRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [CheatService.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getDomainGame(index = 2)
        val cheat = game.cheat!!

        service.remove(cheat = cheat)

        verify(gameRepository).save(game)
        verifyNoMoreInteractions(gameRepository)
        verifyNoInteractions(cheatRepository)
    }

    /**
     * Returns any mock for domain cheat.
     *
     * @return any mock for domain cheat
     */
    private fun anyDomain(): Cheat {
        return any()
    }

}
