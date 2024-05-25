package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.EpisodeMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.EpisodeUtils
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
 * A class represents test for class [EpisodeMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class EpisodeMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [EpisodeMapper]
     */
    private lateinit var mapper: EpisodeMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = EpisodeMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [EpisodeMapper.mapEpisode].
     */
    @Test
    fun mapEpisode() {
        val episode = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first()

        val result = mapper.mapEpisode(source = episode)

        EpisodeUtils.assertEpisodeDeepEquals(expected = episode, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [EpisodeMapper.mapEpisodes].
     */
    @Test
    fun mapEpisodes() {
        val episode = ShowUtils.getDomainShow(index = 1).seasons.first().episodes.first()

        val result = mapper.mapEpisodes(source = listOf(episode))

        EpisodeUtils.assertEpisodesDeepEquals(expected = listOf(episode), actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [EpisodeMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = EpisodeUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        EpisodeUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
