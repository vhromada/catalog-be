package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.CheatDataMapperImpl
import com.github.vhromada.catalog.mapper.impl.CheatMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.CheatUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

/**
 * A class represents test for class [CheatMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class CheatMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [CheatMapper]
     */
    private lateinit var mapper: CheatMapper

    /**
     * Initializes mappers.
     */
    @BeforeEach
    fun setUp() {
        mapper = CheatMapperImpl(cheatDataMapper = CheatDataMapperImpl(), uuidProvider = uuidProvider)
    }

    /**
     * Test method for [CheatMapper.mapCheat].
     */
    @Test
    fun mapCheat() {
        val cheat = GameUtils.getDomainGame(index = 2).cheat!!

        val result = mapper.mapCheat(source = cheat)

        CheatUtils.assertCheatDeepEquals(expected = cheat, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [CheatMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = CheatUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        CheatUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
