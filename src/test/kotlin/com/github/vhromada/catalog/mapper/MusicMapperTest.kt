package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.mapper.impl.MusicMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.utils.SongUtils
import com.github.vhromada.catalog.utils.TestConstants
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

/**
 * A class represents test for class [MusicMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class MusicMapperTest {

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
     * Instance of [MusicMapper]
     */
    private lateinit var mapper: MusicMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = MusicMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [MusicMapper.mapMusic].
     */
    @Test
    fun mapMusic() {
        val music = MusicUtils.getDomainMusic(index = 1)

        val result = mapper.mapMusic(source = music)

        MusicUtils.assertMusicDeepEquals(expected = music, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MusicMapper.mapMusicList].
     */
    @Test
    fun mapMusicList() {
        val music = MusicUtils.getDomainMusic(index = 1)

        val result = mapper.mapMusicList(source = listOf(music))

        MusicUtils.assertMusicDeepEquals(expected = listOf(music), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MusicMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = MusicUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        MusicUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.name!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MusicMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = TestConstants.NAME_FILTER

        val result = mapper.mapFilter(source = source)

        MusicUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MusicMapper.mapStatistics].
     */
    @Test
    fun mapStatistics() {
        val musicStatistics = MusicUtils.getDomainStatistics()
        val songStatistics = SongUtils.getStatistics()

        val result = mapper.mapStatistics(musicStatistics = musicStatistics, songStatistics = songStatistics)

        assertSoftly {
            it.assertThat(result.count).isEqualTo(musicStatistics.count.toInt())
            it.assertThat(result.songsCount).isEqualTo(songStatistics.count.toInt())
            it.assertThat(result.mediaCount).isEqualTo(musicStatistics.mediaCount!!.toInt())
            it.assertThat(result.length).isEqualTo(Time(length = songStatistics.length!!.toInt()).toString())
        }
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [MusicMapper.mapStatistics] with null data.
     */
    @Test
    fun mapStatisticsNulLData() {
        val musicStatistics = MusicUtils.getDomainStatistics()
            .copy(mediaCount = null)
        val songStatistics = SongUtils.getStatistics()
            .copy(length = null)

        val result = mapper.mapStatistics(musicStatistics = musicStatistics, songStatistics = songStatistics)

        assertSoftly {
            it.assertThat(result.count).isEqualTo(musicStatistics.count.toInt())
            it.assertThat(result.songsCount).isEqualTo(songStatistics.count.toInt())
            it.assertThat(result.mediaCount).isZero
            it.assertThat(result.length).isEqualTo(Time(0).toString())
        }
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
