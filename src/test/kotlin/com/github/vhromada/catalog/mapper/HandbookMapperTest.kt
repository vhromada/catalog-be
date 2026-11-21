package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.HandbookMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.HandbookUtils
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
 * A class represents test for class [HandbookMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class HandbookMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [HandbookMapper]
     */
    private lateinit var mapper: HandbookMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = HandbookMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [HandbookMapper.mapHandbook].
     */
    @Test
    fun mapHandbook() {
        val handbook = GameUtils.getDomainGame(index = 1).handbook!!

        val result = mapper.mapHandbook(source = handbook)

        HandbookUtils.assertHandbookDeepEquals(expected = handbook, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [HandbookMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = HandbookUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        HandbookUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
