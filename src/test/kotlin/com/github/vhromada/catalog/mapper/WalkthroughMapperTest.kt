package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.WalkthroughMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.WalkthroughUtils
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
 * A class represents test for class [WalkthroughMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class WalkthroughMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [WalkthroughMapper]
     */
    private lateinit var mapper: WalkthroughMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = WalkthroughMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [WalkthroughMapper.mapWalkthrough].
     */
    @Test
    fun mapWalkthrough() {
        val walkthrough = GameUtils.getDomainGame(index = 1).walkthrough!!

        val result = mapper.mapWalkthrough(source = walkthrough)

        WalkthroughUtils.assertWalkthroughDeepEquals(expected = walkthrough, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [WalkthroughMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = WalkthroughUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        WalkthroughUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
