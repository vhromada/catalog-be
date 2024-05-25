package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.NameFilter
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
 * A class represents test for class [MusicFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class MusicFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [MusicFacade]
     */
    @Autowired
    private lateinit var facade: MusicFacade

    /**
     * Test method for [MusicFacade.search].
     */
    @Test
    fun search() {
        val filter = NameFilter()
        filter.page = 1
        filter.limit = MusicUtils.MUSIC_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        MusicUtils.assertMusicListDeepEquals(expected = MusicUtils.getMusicList(), actual = result.data)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = 1
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
        MusicUtils.assertMusicListDeepEquals(expected = listOf(MusicUtils.getMusic(index = 2)), actual = result.data)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = MusicUtils.MUSIC_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getMusic(index = i)
            val filter = NameFilter(name = music.name)
            filter.page = 1
            filter.limit = MusicUtils.MUSIC_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            MusicUtils.assertMusicListDeepEquals(expected = listOf(music), actual = result.data)
        }

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getMusic(index = i)

            val result = facade.get(uuid = music.uuid)

            MusicUtils.assertMusicDeepEquals(expected = MusicUtils.getMusic(index = i), actual = result)
        }

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedMusic = MusicUtils.newMusic()
        val expectedDomainMusic = MusicUtils.newDomainMusic(id = MusicUtils.MUSIC_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = MusicUtils.newRequest())
        entityManager.flush()

        MusicUtils.assertMusicDeepEquals(expected = expectedMusic, actual = result, ignoreUuid = true)
        MusicUtils.assertMusicDeepEquals(expected = expectedDomainMusic, actual = MusicUtils.getDomainMusic(entityManager = entityManager, id = MusicUtils.MUSIC_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT + 1)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.add] with request with null name.
     */
    @Test
    fun addNullName() {
        val request = MusicUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.add] with request with empty string as name.
     */
    @Test
    fun addEmptyName() {
        val request = MusicUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.add] with music with null count of media.
     */
    @Test
    fun addNullMediaCount() {
        val request = MusicUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.add] with music with not positive count of media.
     */
    @Test
    fun addNotPositiveMediaCount() {
        val request = MusicUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = MusicUtils.newRequest()
        val expectedMusic = MusicUtils.getMusic(index = 1)
            .updated()
        val expectedDomainMusic = MusicUtils.getDomainMusic(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(uuid = MusicUtils.getDomainMusic(index = 1).uuid, request = request)
        entityManager.flush()

        MusicUtils.assertMusicDeepEquals(expected = expectedMusic, actual = result)
        MusicUtils.assertMusicDeepEquals(expected = expectedDomainMusic, actual = MusicUtils.getDomainMusic(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.update] with request with null name.
     */
    @Test
    fun updateNullName() {
        val request = MusicUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.update(uuid = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.update] with request with empty string as name.
     */
    @Test
    fun updateEmptyName() {
        val request = MusicUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.update(uuid = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [MusicFacade.update] with music with null count of media.
     */
    @Test
    fun updateNullMediaCount() {
        val request = MusicUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { facade.update(uuid = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.update] with music with not positive count of media.
     */
    @Test
    fun updateNotPositiveMediaCount() {
        val request = MusicUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { facade.update(uuid = MusicUtils.getDomainMusic(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = MusicUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.remove].
     */
    @Test
    fun remove() {
        facade.remove(uuid = MusicUtils.getMusic(index = 1).uuid)
        entityManager.flush()

        assertThat(MusicUtils.getDomainMusic(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT - 1)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT - SongUtils.SONGS_PER_MUSIC_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedMusic = MusicUtils.getMusic(index = 1)
        val expectedDomainMusic = MusicUtils.getDomainMusic(index = 1)
            .copy(id = MusicUtils.MUSIC_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        val expectedDomainSongs = expectedDomainMusic.songs.mapIndexed { index, song ->
            song.copy(id = SongUtils.SONGS_COUNT + index + 1, music = expectedDomainMusic)
                .fillAudit(audit = AuditUtils.newAudit())
        }
        expectedDomainMusic.songs.clear()
        expectedDomainMusic.songs.addAll(expectedDomainSongs)

        val result = facade.duplicate(uuid = MusicUtils.getMusic(index = 1).uuid)
        entityManager.flush()

        MusicUtils.assertMusicDeepEquals(expected = expectedMusic, actual = result, ignoreUuid = true)
        MusicUtils.assertMusicDeepEquals(expected = expectedDomainMusic, actual = MusicUtils.getDomainMusic(entityManager = entityManager, id = MusicUtils.MUSIC_COUNT + 1), ignoreUuid = true)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT + 1)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT + SongUtils.SONGS_PER_MUSIC_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("MUSIC_NOT_EXIST")
            .hasMessageContaining("Music doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for [MusicFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        val result = facade.getStatistics()

        MusicUtils.assertStatisticsDeepEquals(expected = MusicUtils.getStatistics(), actual = result)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

}
