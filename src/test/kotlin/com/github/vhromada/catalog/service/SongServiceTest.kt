package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Song
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.MusicRepository
import com.github.vhromada.catalog.repository.SongRepository
import com.github.vhromada.catalog.service.impl.SongServiceImpl
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.utils.SongUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [SongService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class SongServiceTest {

    /**
     * Instance of [SongRepository]
     */
    @Mock
    private lateinit var songRepository: SongRepository

    /**
     * Instance of [MusicRepository]
     */
    @Mock
    private lateinit var musicRepository: MusicRepository

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [SongService]
     */
    private lateinit var service: SongService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = SongServiceImpl(songRepository = songRepository, musicRepository = musicRepository, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [SongService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(MusicUtils.getDomainMusic(index = 1).songs)
        whenever(songRepository.findAllByMusicId(id = any(), pageable = any())).thenReturn(page)

        val result = service.search(music = 1, pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(songRepository).findAllByMusicId(id = 1, pageable = pageable)
        verifyNoMoreInteractions(songRepository)
        verifyNoInteractions(musicRepository, uuidProvider)
    }

    /**
     * Test method for [SongService.get] with existing song.
     */
    @Test
    fun getExisting() {
        val song = MusicUtils.getDomainMusic(index = 1).songs.first()
        whenever(songRepository.findByUuid(uuid = any())).thenReturn(Optional.of(song))

        val result = service.get(uuid = song.uuid)

        assertThat(result).isEqualTo(song)
        verify(songRepository).findByUuid(uuid = song.uuid)
        verifyNoMoreInteractions(songRepository)
        verifyNoInteractions(musicRepository, uuidProvider)
    }

    /**
     * Test method for [SongService.get] with not existing song.
     */
    @Test
    fun getNotExisting() {
        whenever(songRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NOT_EXIST")
            .hasMessageContaining("Song doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(songRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(songRepository)
        verifyNoInteractions(musicRepository, uuidProvider)
    }

    /**
     * Test method for [SongService.store].
     */
    @Test
    fun store() {
        val song = MusicUtils.getDomainMusic(index = 1).songs.first()
        whenever(songRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(song = song)

        assertThat(result).isSameAs(song)
        verify(songRepository).save(song)
        verifyNoMoreInteractions(songRepository)
        verifyNoInteractions(musicRepository, uuidProvider)
    }

    /**
     * Test method for [SongService.remove].
     */
    @Test
    fun remove() {
        val music = MusicUtils.getDomainMusic(index = 1)
        val song = music.songs.first()

        service.remove(song = song)

        verify(musicRepository).save(music)
        verifyNoMoreInteractions(musicRepository)
        verifyNoInteractions(songRepository, uuidProvider)
    }

    /**
     * Test method for [SongService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedMusic = MusicUtils.getDomainMusic(index = 1)
        val expectedSong = expectedMusic.songs.first()
            .copy(id = 0, uuid = TestConstants.UUID)
        expectedMusic.songs.add(expectedSong)
        val copyArgumentCaptor = argumentCaptor<Song>()
        whenever(songRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Song
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(song = MusicUtils.getDomainMusic(index = 1).songs.first())

        SongUtils.assertSongDeepEquals(expected = expectedSong, actual = result)
        verify(songRepository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(songRepository, uuidProvider)
        verifyNoInteractions(musicRepository)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Returns any mock for domain song.
     *
     * @return any mock for domain song
     */
    private fun anyDomain(): Song {
        return any()
    }

}
