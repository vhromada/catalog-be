package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.utils.SongUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.fillAudit
import com.github.vhromada.catalog.utils.updated
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [SongRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class SongRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [SongRepository]
     */
    @Autowired
    private lateinit var repository: SongRepository

    /**
     * Test method for get song.
     */
    @Test
    fun getSong() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            for (j in 1..SongUtils.SONGS_PER_MUSIC_COUNT) {
                val id = (i - 1) * SongUtils.SONGS_PER_MUSIC_COUNT + j

                val song = repository.findById(id).orElse(null)

                SongUtils.assertSongDeepEquals(expected = MusicUtils.getDomainMusic(index = i).songs[j - 1], actual = song)
            }
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for add song.
     */
    @Test
    @DirtiesContext
    fun add() {
        val song = SongUtils.newDomainSong(id = null)
        song.music = MusicUtils.getDomainMusic(entityManager = entityManager, id = 1)
        val expectedSong = SongUtils.newDomainSong(id = SongUtils.SONGS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())
        expectedSong.music = MusicUtils.getDomainMusic(entityManager = entityManager, id = 1)

        repository.saveAndFlush(song)

        assertSoftly {
            it.assertThat(song.id).isEqualTo(SongUtils.SONGS_COUNT + 1)
            it.assertThat(song.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(song.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(song.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(song.updatedTime).isEqualTo(TestConstants.TIME)
        }
        SongUtils.assertSongDeepEquals(expected = expectedSong, actual = SongUtils.getDomainSong(entityManager = entityManager, id = SongUtils.SONGS_COUNT + 1))

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT + 1)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for update song.
     */
    @Test
    fun update() {
        val song = SongUtils.getDomainSong(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedSong = MusicUtils.getDomainMusic(index = 1).songs.first()
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(song)

        SongUtils.assertSongDeepEquals(expected = expectedSong, actual = SongUtils.getDomainSong(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for get songs by music ID.
     */
    @Test
    fun findAllByMusicId() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val songs = repository.findAllByMusicId(id = i, pageable = Pageable.ofSize(SongUtils.SONGS_PER_MUSIC_COUNT))

            assertSoftly {
                it.assertThat(songs.number).isEqualTo(0)
                it.assertThat(songs.totalPages).isEqualTo(1)
                it.assertThat(songs.totalElements).isEqualTo(SongUtils.SONGS_PER_MUSIC_COUNT.toLong())
            }
            SongUtils.assertDomainSongsDeepEquals(expected = MusicUtils.getDomainMusic(index = i).songs, actual = songs.content)
        }

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for get songs by music ID with invalid paging.
     */
    @Test
    fun findAllByMusicIdInvalidPaging() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val songs = repository.findAllByMusicId(id = i, pageable = PageRequest.of(2, SongUtils.SONGS_PER_MUSIC_COUNT))

            assertSoftly {
                it.assertThat(songs.content).isEmpty()
                it.assertThat(songs.number).isEqualTo(2)
                it.assertThat(songs.totalPages).isEqualTo(1)
                it.assertThat(songs.totalElements).isEqualTo(SongUtils.SONGS_PER_MUSIC_COUNT.toLong())
            }
        }

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for get songs by music ID with not existing music ID.
     */
    @Test
    fun findAllByMusicIdNotExistingMusicId() {
        val songs = repository.findAllByMusicId(id = Int.MAX_VALUE, pageable = Pageable.ofSize(SongUtils.SONGS_PER_MUSIC_COUNT))

        assertSoftly {
            it.assertThat(songs.content).isEmpty()
            it.assertThat(songs.number).isEqualTo(0)
            it.assertThat(songs.totalPages).isEqualTo(0)
            it.assertThat(songs.totalElements).isEqualTo(0L)
        }

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for find song by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getDomainMusic(index = i)
            for (song in music.songs) {
                val result = repository.findByUuid(uuid = song.uuid).orElse(null)

                SongUtils.assertSongDeepEquals(expected = song, actual = result)
            }
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

    /**
     * Test method for get statistics.
     */
    @Test
    fun getStatistics() {
        val result = repository.getStatistics()

        SongUtils.assertStatisticsDeepEquals(expected = SongUtils.getStatistics(), actual = result)

        assertSoftly {
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
        }
    }

}
