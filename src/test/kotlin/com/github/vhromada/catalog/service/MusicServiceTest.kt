package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Music
import com.github.vhromada.catalog.domain.filter.MusicFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.MusicMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.MusicRepository
import com.github.vhromada.catalog.repository.SongRepository
import com.github.vhromada.catalog.service.impl.MusicServiceImpl
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
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [MusicService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class MusicServiceTest {

    /**
     * Instance of [MusicRepository]
     */
    @Mock
    private lateinit var musicRepository: MusicRepository

    /**
     * Instance of [SongRepository]
     */
    @Mock
    private lateinit var songRepository: SongRepository

    /**
     * Instance of [MusicMapper]
     */
    @Mock
    private lateinit var mapper: MusicMapper

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [MusicService]
     */
    private lateinit var service: MusicService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = MusicServiceImpl(musicRepository = musicRepository, songRepository = songRepository, mapper = mapper, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [MusicService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(MusicUtils.getDomainMusic(index = 1), MusicUtils.getDomainMusic(index = 2)))
        whenever(musicRepository.findAll(any<Specification<Music>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = MusicFilter(name = "Name"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(musicRepository).findAll(any<Specification<Music>>(), eq(pageable))
        verifyNoMoreInteractions(musicRepository)
        verifyNoInteractions(songRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [MusicService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(MusicUtils.getDomainMusic(index = 1), MusicUtils.getDomainMusic(index = 2)))
        whenever(musicRepository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = MusicFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(musicRepository).findAll(pageable)
        verifyNoMoreInteractions(musicRepository)
        verifyNoInteractions(songRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [MusicService.get] with existing music.
     */
    @Test
    fun getExisting() {
        val music = MusicUtils.getDomainMusic(index = 1)
        whenever(musicRepository.findByUuid(uuid = any())).thenReturn(Optional.of(music))

        val result = service.get(uuid = music.uuid)

        assertThat(result).isEqualTo(music)
        verify(musicRepository).findByUuid(uuid = music.uuid)
        verifyNoMoreInteractions(musicRepository)
        verifyNoInteractions(songRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [MusicService.get] with not existing music.
     */
    @Test
    fun getNotExisting() {
        whenever(musicRepository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(musicRepository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(musicRepository)
        verifyNoInteractions(songRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [MusicService.store].
     */
    @Test
    fun store() {
        val music = MusicUtils.getDomainMusic(index = 1)
        whenever(musicRepository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(music = music)

        assertThat(result).isSameAs(music)
        verify(musicRepository).save(music)
        verifyNoMoreInteractions(musicRepository)
        verifyNoInteractions(songRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [MusicService.remove].
     */
    @Test
    fun remove() {
        val music = MusicUtils.getDomainMusic(index = 1)

        service.remove(music = music)

        verify(musicRepository).delete(music)
        verifyNoMoreInteractions(musicRepository)
        verifyNoInteractions(songRepository, mapper, uuidProvider)
    }

    /**
     * Test method for [MusicService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedMusic = MusicUtils.getDomainMusic(index = 1)
            .copy(id = 0, uuid = TestConstants.UUID)
        val expectedSongs = expectedMusic.songs.map { it.copy(id = null, uuid = TestConstants.UUID, music = expectedMusic) }
        expectedMusic.songs.clear()
        expectedMusic.songs.addAll(expectedSongs)
        val copyArgumentCaptor = argumentCaptor<Music>()
        whenever(musicRepository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Music
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(music = MusicUtils.getDomainMusic(index = 1))

        MusicUtils.assertMusicDeepEquals(expected = expectedMusic, actual = result)
        verify(musicRepository).save(copyArgumentCaptor.capture())
        verify(uuidProvider, times(SongUtils.SONGS_PER_MUSIC_COUNT + 1)).getUuid()
        verifyNoMoreInteractions(musicRepository, uuidProvider)
        verifyNoInteractions(songRepository, mapper)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [MusicService.getStatistics].
     */
    @Test
    fun getStatistics() {
        val songStatistics = SongUtils.getStatistics()
        val domain = MusicUtils.getDomainStatistics()
        val entity = MusicUtils.getStatistics()
        whenever(musicRepository.getStatistics()).thenReturn(domain)
        whenever(songRepository.getStatistics()).thenReturn(songStatistics)
        whenever(mapper.mapStatistics(musicStatistics = any(), songStatistics = any())).thenReturn(entity)

        val result = service.getStatistics()

        assertThat(result).isEqualTo(entity)
        verify(musicRepository).getStatistics()
        verify(songRepository).getStatistics()
        verify(mapper).mapStatistics(musicStatistics = domain, songStatistics = songStatistics)
        verifyNoMoreInteractions(musicRepository, songRepository, mapper)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain music.
     *
     * @return any mock for domain music
     */
    private fun anyDomain(): Music {
        return any()
    }

}
