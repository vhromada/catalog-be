package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.MusicFilter
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
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [MusicRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class MusicRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [MusicRepository]
     */
    @Autowired
    private lateinit var repository: MusicRepository

    /**
     * Test method for get list of music.
     */
    @Test
    fun getMusicList() {
        val musicList = repository.findAll()

        MusicUtils.assertDomainMusicDeepEquals(expected = MusicUtils.getDomainMusicList(), actual = musicList)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for get music.
     */
    @Test
    fun getMusic() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = repository.findById(i).orElse(null)

            MusicUtils.assertMusicDeepEquals(expected = MusicUtils.getDomainMusic(index = i), actual = music)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for add music.
     */
    @Test
    @DirtiesContext
    fun add() {
        val music = MusicUtils.newDomainMusic(id = null)
        val expectedMusic = MusicUtils.newDomainMusic(id = MusicUtils.MUSIC_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(music)

        assertSoftly {
            it.assertThat(music.id).isEqualTo(MusicUtils.MUSIC_COUNT + 1)
            it.assertThat(music.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(music.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(music.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(music.updatedTime).isEqualTo(TestConstants.TIME)
        }
        MusicUtils.assertMusicDeepEquals(expected = expectedMusic, actual = MusicUtils.getDomainMusic(entityManager = entityManager, id = MusicUtils.MUSIC_COUNT + 1))

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT + 1)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for update music.
     */
    @Test
    fun update() {
        val music = MusicUtils.getDomainMusic(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedMusic = MusicUtils.getDomainMusic(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(music)

        MusicUtils.assertMusicDeepEquals(expected = expectedMusic, actual = MusicUtils.getDomainMusic(entityManager = entityManager, id = 1))

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for remove music.
     */
    @Test
    fun remove() {
        repository.delete(MusicUtils.getDomainMusic(entityManager = entityManager, id = 1)!!)

        assertThat(MusicUtils.getDomainMusic(entityManager = entityManager, id = 1)).isNull()

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT - 1)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT - SongUtils.SONGS_PER_MUSIC_COUNT)
        }
    }

    /**
     * Test method for remove all music.
     */
    @Test
    fun removeAll() {
        repository.deleteAll()

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(0)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(0)
        }
    }

    /**
     * Test method for search music by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getDomainMusic(index = i)
            val filter = MusicFilter(name = music.name)

            val result = repository.findAll(filter.toSpecification())

            MusicUtils.assertDomainMusicDeepEquals(expected = listOf(music), actual = result.toList())
        }

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for find music by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..MusicUtils.MUSIC_COUNT) {
            val music = MusicUtils.getDomainMusic(index = i)

            val result = repository.findByUuid(uuid = music.uuid).orElse(null)

            MusicUtils.assertMusicDeepEquals(expected = MusicUtils.getDomainMusic(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

    /**
     * Test method for get statistics.
     */
    @Test
    fun getStatistics() {
        val result = repository.getStatistics()

        MusicUtils.assertStatisticsDeepEquals(expected = MusicUtils.getDomainStatistics(), actual = result)

        assertSoftly {
            it.assertThat(MusicUtils.getMusicCount(entityManager = entityManager)).isEqualTo(MusicUtils.MUSIC_COUNT)
            it.assertThat(SongUtils.getSongsCount(entityManager = entityManager)).isEqualTo(SongUtils.SONGS_COUNT)
        }
    }

}
