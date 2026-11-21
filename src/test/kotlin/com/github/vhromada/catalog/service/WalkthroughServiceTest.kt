package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Walkthrough
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.GameRepository
import com.github.vhromada.catalog.repository.WalkthroughRepository
import com.github.vhromada.catalog.service.impl.WalkthroughServiceImpl
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.WalkthroughUtils
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
 * A class represents test for class [WalkthroughService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class WalkthroughServiceTest {

    /**
     * Instance of [WalkthroughRepository]
     */
    @Mock
    private lateinit var walkthroughRepository: WalkthroughRepository

    /**
     * Instance of [GameRepository]
     */
    @Mock
    private lateinit var gameRepository: GameRepository

    /**
     * Instance of [WalkthroughService]
     */
    private lateinit var service: WalkthroughService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = WalkthroughServiceImpl(walkthroughRepository = walkthroughRepository, gameRepository = gameRepository)
    }

    /**
     * Test method for [WalkthroughService.getByGame] with existing walkthrough.
     */
    @Test
    fun getByGameExisting() {
        val game = GameUtils.getDomainGame(index = 1)
        val walkthrough = game.walkthrough!!
        whenever(walkthroughRepository.findByGameId(id = any())).thenReturn(Optional.of(walkthrough))

        val result = service.getByGame(game = game.id!!)

        assertThat(result).isEqualTo(walkthrough)
        verify(walkthroughRepository).findByGameId(id = game.id!!)
        verifyNoMoreInteractions(walkthroughRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [WalkthroughService.getByGame] with not existing walkthrough.
     */
    @Test
    fun getByGameNotExisting() {
        whenever(walkthroughRepository.findByGameId(id = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getByGame(game = Int.MAX_VALUE) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_NOT_EXIST")
            .hasMessageContaining("Walkthrough doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(walkthroughRepository).findByGameId(id = Int.MAX_VALUE)
        verifyNoMoreInteractions(walkthroughRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [WalkthroughService.getByUuid] with existing walkthrough.
     */
    @Test
    fun getByUuidExisting() {
        val walkthrough = GameUtils.getDomainGame(index = 1).walkthrough!!
        whenever(walkthroughRepository.findByUuid(uuid = any())).thenReturn(Optional.of(walkthrough))

        val result = service.getByUuid(uuid = walkthrough.uuid)

        assertThat(result).isEqualTo(walkthrough)
        verify(walkthroughRepository).findByUuid(uuid = walkthrough.uuid)
        verifyNoMoreInteractions(walkthroughRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [WalkthroughService.getByUuid] with not existing walkthrough.
     */
    @Test
    fun getByUuidNotExisting() {
        whenever(walkthroughRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getByUuid(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("WALKTHROUGH_NOT_EXIST")
            .hasMessageContaining("Walkthrough doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(walkthroughRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(walkthroughRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [WalkthroughService.store] for adding.
     */
    @Test
    fun add() {
        val walkthrough = WalkthroughUtils.newDomainWalkthrough(id = null)
        whenever(walkthroughRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Walkthrough
            argument.id = 3
            argument
        }

        val result = service.store(walkthrough = walkthrough)

        assertSoftly {
            it.assertThat(result).isSameAs(walkthrough)
            it.assertThat(result.id).isEqualTo(3)
        }
        verify(walkthroughRepository).save(walkthrough)
        verifyNoMoreInteractions(walkthroughRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [WalkthroughService.store] for updating.
     */
    @Test
    fun update() {
        val walkthrough = GameUtils.getDomainGame(index = 1).walkthrough!!
        whenever(walkthroughRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(walkthrough = walkthrough)

        assertThat(result).isSameAs(walkthrough)
        verify(walkthroughRepository).save(walkthrough)
        verifyNoMoreInteractions(walkthroughRepository)
        verifyNoInteractions(gameRepository)
    }

    /**
     * Test method for [WalkthroughService.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getDomainGame(index = 1)
        val walkthrough = game.walkthrough!!

        service.remove(walkthrough = walkthrough)

        verify(gameRepository).save(game)
        verifyNoMoreInteractions(gameRepository)
        verifyNoInteractions(walkthroughRepository)
    }

    /**
     * Returns any mock for domain walkthrough.
     *
     * @return any mock for domain walkthrough
     */
    private fun anyDomain(): Walkthrough {
        return any()
    }

}
