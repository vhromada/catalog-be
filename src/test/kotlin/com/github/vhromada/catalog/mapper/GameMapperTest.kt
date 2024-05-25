package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.GameMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
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

/**
 * A class represents test for class [GameMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class GameMapperTest {

    /**
     * Instance of [NormalizerService]
     */
    @Mock
    private lateinit var normalizerService: NormalizerService

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [GameMapper]
     */
    private lateinit var mapper: GameMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = GameMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [GameMapper.mapGame].
     */
    @Test
    fun mapGame() {
        val game = GameUtils.getDomainGame(index = 1)

        val result = mapper.mapGame(source = game)

        GameUtils.assertGameDeepEquals(expected = game, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GameMapper.mapGames].
     */
    @Test
    fun mapGames() {
        val game = GameUtils.getDomainGame(index = 1)

        val result = mapper.mapGames(source = listOf(game))

        GameUtils.assertGamesDeepEquals(expected = listOf(game), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GameMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = GameUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        GameUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.name!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GameMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = TestConstants.NAME_FILTER

        val result = mapper.mapFilter(source = source)

        GameUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GameMapper.mapStatistics].
     */
    @Test
    fun mapStatistics() {
        val source = GameUtils.getDomainStatistics()

        val result = mapper.mapStatistics(source = source)

        GameUtils.assertStatisticsDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [GameMapper.mapStatistics] with null media count.
     */
    @Test
    fun mapStatisticsNullMediaCount() {
        val source = GameUtils.getDomainStatistics()
            .copy(mediaCount = null)

        val result = mapper.mapStatistics(source = source)

        GameUtils.assertStatisticsDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
