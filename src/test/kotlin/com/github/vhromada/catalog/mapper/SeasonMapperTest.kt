package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.mapper.impl.SeasonMapperImpl
import com.github.vhromada.catalog.utils.SeasonUtils
import com.github.vhromada.catalog.utils.ShowUtils
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
 * A class represents test for class [SeasonMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class SeasonMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [SeasonMapper]
     */
    private lateinit var mapper: SeasonMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = SeasonMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [SeasonMapper.mapSeason].
     */
    @Test
    fun mapSeason() {
        val season = ShowUtils.getDomainShow(index = 1).seasons.first()

        val result = mapper.mapSeason(source = season)

        SeasonUtils.assertSeasonDeepEquals(expected = season, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [SeasonMapper.mapSeasons].
     */
    @Test
    fun mapSeasons() {
        val season = ShowUtils.getDomainShow(index = 1).seasons.first()

        val result = mapper.mapSeasons(source = listOf(season))

        SeasonUtils.assertSeasonsDeepEquals(expected = listOf(season), actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [SeasonMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = SeasonUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        SeasonUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
