package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.utils.SongUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [SongFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class SongFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [SongFacade]
     */
    @Autowired
    private lateinit var facade: SongFacade

    /**
     * Test method for [SongFacade.findAll].
     */
    @Test
    fun findAll() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getDomainMusic(index = i)
            val filter = PagingFilter()
            filter.page = 1
            filter.limit = SongUtils.SONGS_PER_MUSIC_COUNT

            val result = facade.findAll(music = music.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            SongUtils.assertSongListDeepEquals(expected = SongUtils.getSongs(music = i), actual = result.data)
        }

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.findAll] with not existing music.
     */
    @Test
    fun findAllNotExistingMusic() {
        assertThatThrownBy { facade.findAll(music = TestConstants.UUID, filter = PagingFilter()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.findAll] with paging.
     */
    @Test
    fun findAllPaging() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getDomainMusic(index = i)
            val filter = PagingFilter()
            filter.page = 2
            filter.limit = 1

            val result = facade.findAll(music = music.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(SongUtils.SONGS_PER_MUSIC_COUNT)
            }
            SongUtils.assertSongsDeepEquals(expected = listOf(music.songs[1]), actual = result.data)
        }

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.findAll] with invalid paging.
     */
    @Test
    fun findAllInvalidPaging() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getDomainMusic(index = i)
            val filter = PagingFilter()
            filter.page = 2
            filter.limit = SongUtils.SONGS_PER_MUSIC_COUNT

            val result = facade.findAll(music = music.uuid, filter = filter)

            assertSoftly {
                it.assertThat(result.data).isEmpty()
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
        }

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getDomainMusic(index = i)
            for (song in music.songs) {
                val result = facade.get(music = music.uuid, uuid = song.uuid)

                SongUtils.assertSongDeepEquals(expected = song, actual = result)
            }
        }

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.get] with not existing music.
     */
    @Test
    fun getNotExistingMusic() {
        assertThatThrownBy { facade.get(music = TestConstants.UUID, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NOT_EXIST")
            .hasMessageContaining("Song doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val music = MusicUtils.getDomainMusic(index = 1)
        val expectedSong = SongUtils.newSong()
        val expectedDomainSong = SongUtils.newDomainSong(id = SongUtils.SONGS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedDomainSong.music = music

        val result = facade.add(music = music.uuid, request = SongUtils.newRequest())
        entityManager.flush()

        SongUtils.assertSongDeepEquals(expected = expectedSong, actual = result, ignoreUuid = true)
        SongUtils.assertSongDeepEquals(expected = expectedDomainSong, actual = SongUtils.getDomainSong(entityManager = entityManager, id = SongUtils.SONGS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT + 1)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.add] with request with null name.
     */
    @Test
    fun addNullName() {
        val request = SongUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.add(music = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.add] with request with empty string as name.
     */
    @Test
    fun addEmptyName() {
        val request = SongUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.add(music = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.add] with song with null length of song.
     */
    @Test
    fun addNullLength() {
        val request = SongUtils.newRequest()
            .copy(length = null)

        assertThatThrownBy { facade.add(music = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_LENGTH_NULL")
            .hasMessageContaining("Length of song mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.add] with song with negative length of song.
     */
    @Test
    fun addNegativeLength() {
        val request = SongUtils.newRequest()
            .copy(length = -1)

        assertThatThrownBy { facade.add(music = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_LENGTH_NEGATIVE")
            .hasMessageContaining("Length of song mustn't be negative number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.add] with not existing music.
     */
    @Test
    fun addNotExistingMusic() {
        assertThatThrownBy { facade.add(music = TestConstants.UUID, request = SongUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val music = MusicUtils.getDomainMusic(index = 1)
        val request = SongUtils.newRequest()
        val expectedSong = SongUtils.getSong(index = 1)
            .updated()
        val expectedDomainSong = music.songs.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(music = music.uuid, uuid = music.songs.first().uuid, request = request)
        entityManager.flush()

        SongUtils.assertSongDeepEquals(expected = expectedSong, actual = result)
        SongUtils.assertSongDeepEquals(expected = expectedDomainSong, actual = SongUtils.getDomainSong(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.update] with request with null name.
     */
    @Test
    fun updateNullName() {
        val request = SongUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.update(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.update] with request with empty string as name.
     */
    @Test
    fun updateEmptyName() {
        val request = SongUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.update(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [SongFacade.update] with song with null length of song.
     */
    @Test
    fun updateNullLength() {
        val request = SongUtils.newRequest()
            .copy(length = null)

        assertThatThrownBy { facade.update(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_LENGTH_NULL")
            .hasMessageContaining("Length of song mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.update] with song with negative length of song.
     */
    @Test
    fun updateNegativeLength() {
        val request = SongUtils.newRequest()
            .copy(length = -1)

        assertThatThrownBy { facade.update(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_LENGTH_NEGATIVE")
            .hasMessageContaining("Length of song mustn't be negative number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.update] with not existing music.
     */
    @Test
    fun updateNotExistingMusic() {
        assertThatThrownBy { facade.update(music = TestConstants.UUID, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid, request = SongUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = TestConstants.UUID, request = SongUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NOT_EXIST")
            .hasMessageContaining("Song doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.remove].
     */
    @Test
    fun remove() {
        val music = MusicUtils.getDomainMusic(index = 1)

        facade.remove(music = music.uuid, uuid = music.songs.first().uuid)
        entityManager.flush()

        assertThat(SongUtils.getDomainSong(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT - 1)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.remove] with not existing music.
     */
    @Test
    fun removeNotExistingMusic() {
        assertThatThrownBy { facade.remove(music = TestConstants.UUID, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NOT_EXIST")
            .hasMessageContaining("Song doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val music = MusicUtils.getDomainMusic(index = 1)
        val expectedSong = SongUtils.getSong(index = 1)
        val expectedDomainSong = music.songs.first()
            .copy(id = SongUtils.SONGS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        music.songs.add(expectedDomainSong)

        val result = facade.duplicate(music = music.uuid, uuid = music.songs.first().uuid)
        entityManager.flush()

        SongUtils.assertSongDeepEquals(expected = expectedSong, actual = result, ignoreUuid = true)
        SongUtils.assertSongDeepEquals(expected = expectedDomainSong, actual = SongUtils.getDomainSong(entityManager = entityManager, id = SongUtils.SONGS_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT + 1)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.duplicate] with not existing music.
     */
    @Test
    fun duplicateNotExistingMusic() {
        assertThatThrownBy { facade.duplicate(music = TestConstants.UUID, uuid = MusicUtils.getDomainMusic(index = 1).songs.first().uuid) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for [SongFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(music = MusicUtils.getDomainMusic(index = 1).uuid, uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("SONG_NOT_EXIST")
            .hasMessageContaining("Song doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

}
