package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.SongMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.utils.SongUtils
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
 * A class represents test for class [SongMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class SongMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [SongMapper]
     */
    private lateinit var mapper: SongMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = SongMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [SongMapper.mapSong].
     */
    @Test
    fun mapSong() {
        val song = MusicUtils.getDomainMusic(index = 1).songs.first()

        val result = mapper.mapSong(source = song)

        SongUtils.assertSongDeepEquals(expected = song, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [SongMapper.mapSongs].
     */
    @Test
    fun mapSongs() {
        val song = MusicUtils.getDomainMusic(index = 1).songs.first()

        val result = mapper.mapSongs(source = listOf(song))

        SongUtils.assertSongsDeepEquals(expected = listOf(song), actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [SongMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = SongUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        SongUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
