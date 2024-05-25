package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.common.Time
import com.github.vhromada.catalog.domain.filter.MusicFilter
import com.github.vhromada.catalog.domain.io.MusicStatistics
import com.github.vhromada.catalog.entity.Music
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeMusicRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates music fields.
 *
 * @return updated music
 */
fun com.github.vhromada.catalog.domain.Music.updated(): com.github.vhromada.catalog.domain.Music {
    name = "Name"
    normalizedName = "Name"
    wikiEn = "enWiki"
    wikiCz = "czWiki"
    mediaCount = 1
    note = "Note"
    return this
}

/**
 * Updates music fields.
 *
 * @return updated music
 */
fun Music.updated(): Music {
    return copy(
        name = "Name",
        wikiEn = "enWiki",
        wikiCz = "czWiki",
        mediaCount = 1,
        note = "Note"
    )
}

/**
 * A class represents utility class for music.
 *
 * @author Vladimir Hromada
 */
object MusicUtils {

    /**
     * Count of music
     */
    const val MUSIC_COUNT = 3

    /**
     * Multiplier for media count
     */
    private const val MEDIA_COUNT_MULTIPLIER = 10

    /**
     * Returns list of music.
     *
     * @return list of music
     */
    fun getDomainMusicList(): List<com.github.vhromada.catalog.domain.Music> {
        val musicList = mutableListOf<com.github.vhromada.catalog.domain.Music>()
        for (i in 1..MUSIC_COUNT) {
            musicList.add(getDomainMusic(index = i))
        }

        return musicList
    }

    /**
     * Returns list of music.
     *
     * @return list of music
     */
    fun getMusicList(): List<Music> {
        val musicList = mutableListOf<Music>()
        for (i in 1..MUSIC_COUNT) {
            musicList.add(getMusic(index = i))
        }

        return musicList
    }

    /**
     * Returns music for index.
     *
     * @param index index
     * @return music for index
     */
    fun getDomainMusic(index: Int): com.github.vhromada.catalog.domain.Music {
        val name = "Music $index name"
        val music = com.github.vhromada.catalog.domain.Music(
            id = index,
            uuid = getUuid(index = index),
            name = name,
            normalizedName = name,
            wikiEn = if (index != 1) "Music $index English Wikipedia" else null,
            wikiCz = if (index != 1) "Music $index Czech Wikipedia" else null,
            mediaCount = index * MEDIA_COUNT_MULTIPLIER,
            note = if (index == 2) "Music $index note" else null,
            songs = SongUtils.getDomainSongs(music = index)
        ).fillAudit(audit = AuditUtils.getAudit())
        music.songs.forEach { it.music = music }
        return music
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "9c9f53ce-5576-4d18-8470-5863dbb49c46"
            2 -> "21d837eb-258e-4170-b27d-b81c880e9260"
            3 -> "66fc3034-223a-486e-8c03-c3f71a733ca9"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns music.
     *
     * @param entityManager entity manager
     * @param id            music ID
     * @return music
     */
    fun getDomainMusic(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Music? {
        return entityManager.find(com.github.vhromada.catalog.domain.Music::class.java, id)
    }

    /**
     * Returns music for index.
     *
     * @param index index
     * @return music for index
     */
    fun getMusic(index: Int): Music {
        return Music(
            uuid = getUuid(index = index),
            name = "Music $index name",
            wikiEn = if (index != 1) "Music $index English Wikipedia" else null,
            wikiCz = if (index != 1) "Music $index Czech Wikipedia" else null,
            mediaCount = index * MEDIA_COUNT_MULTIPLIER,
            note = if (index == 2) "Music $index note" else null,
            songsCount = SongUtils.SONGS_PER_MUSIC_COUNT,
            length = SongUtils.getSongs(music = index).sumOf { it.length }
        )
    }

    /**
     * Returns statistics for music.
     *
     * @return statistics for music
     */
    fun getDomainStatistics(): MusicStatistics {
        return MusicStatistics(count = MUSIC_COUNT.toLong(), mediaCount = 60L)
    }

    /**
     * Returns statistics for music.
     *
     * @return statistics for music
     */
    fun getStatistics(): com.github.vhromada.catalog.entity.MusicStatistics {
        return com.github.vhromada.catalog.entity.MusicStatistics(count = MUSIC_COUNT, songsCount = SongUtils.SONGS_COUNT, mediaCount = 60, length = Time(length = 666).toString())
    }

    /**
     * Returns count of music.
     *
     * @param entityManager entity manager
     * @return count of music
     */
    fun getMusicCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(m.id) FROM Music m", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns music.
     *
     * @param id ID
     * @return music
     */
    fun newDomainMusic(id: Int?): com.github.vhromada.catalog.domain.Music {
        return com.github.vhromada.catalog.domain.Music(
            id = id,
            uuid = TestConstants.UUID,
            name = "",
            normalizedName = "",
            wikiEn = null,
            wikiCz = null,
            mediaCount = 0,
            note = null,
            songs = mutableListOf()
        ).updated()
    }

    /**
     * Returns music.
     *
     * @return music
     */
    fun newMusic(): Music {
        return Music(
            uuid = TestConstants.UUID,
            name = "",
            wikiEn = null,
            wikiCz = null,
            mediaCount = 0,
            note = null,
            songsCount = 0,
            length = 0
        ).updated()
    }

    /**
     * Returns request for changing music.
     *
     * @return request for changing music
     */
    fun newRequest(): ChangeMusicRequest {
        return ChangeMusicRequest(
            name = "Name",
            wikiEn = "enWiki",
            wikiCz = "czWiki",
            mediaCount = 1,
            note = "Note"
        )
    }

    /**
     * Asserts list of music deep equals.
     *
     * @param expected expected list of music
     * @param actual   actual list of music
     */
    fun assertDomainMusicDeepEquals(expected: List<com.github.vhromada.catalog.domain.Music>, actual: List<com.github.vhromada.catalog.domain.Music>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMusicDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts music deep equals.
     *
     * @param expected   expected music
     * @param actual     actual music
     * @param checkSongs true if songs should be checked
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertMusicDeepEquals(expected: com.github.vhromada.catalog.domain.Music?, actual: com.github.vhromada.catalog.domain.Music?, checkSongs: Boolean = true, ignoreUuid: Boolean = false) {
        if (expected == null) {
            assertThat(actual).isNull()
        } else {
            assertThat(actual).isNotNull
            assertSoftly {
                it.assertThat(actual!!.id).isEqualTo(expected.id)
                if (ignoreUuid) {
                    it.assertThat(actual.uuid).isNotEmpty
                } else {
                    it.assertThat(actual.uuid).isEqualTo(expected.uuid)
                }
                it.assertThat(actual.name).isEqualTo(expected.name)
                it.assertThat(actual.normalizedName).isEqualTo(expected.normalizedName)
                it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
                it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
                it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            if (checkSongs) {
                SongUtils.assertDomainSongsDeepEquals(expected = expected.songs, actual = actual.songs, ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts list of music deep equals.
     *
     * @param expected expected list of music
     * @param actual   actual list of music
     */
    fun assertMusicDeepEquals(expected: List<com.github.vhromada.catalog.domain.Music>, actual: List<Music>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMusicDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts music deep equals.
     *
     * @param expected   expected music
     * @param actual     actual music
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertMusicDeepEquals(expected: com.github.vhromada.catalog.domain.Music, actual: Music, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.songsCount).isEqualTo(expected.songs.size)
            it.assertThat(actual.length).isEqualTo(expected.songs.sumOf { song -> song.length })
        }
    }

    /**
     * Asserts list of music deep equals.
     *
     * @param expected expected list of music
     * @param actual   actual list of music
     */
    fun assertMusicListDeepEquals(expected: List<Music>, actual: List<Music>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertMusicDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts music deep equals.
     *
     * @param expected   expected music
     * @param actual     actual music
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertMusicDeepEquals(expected: Music, actual: Music, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.songsCount).isEqualTo(expected.songsCount)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

    /**
     * Asserts request and music deep equals.
     *
     * @param expected expected request for changing music
     * @param actual   actual music
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeMusicRequest, actual: com.github.vhromada.catalog.domain.Music, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.normalizedName).isEqualTo(expected.name)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.songs).isEmpty()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

    /**
     * Asserts filter deep equals.
     *
     * @param expected expected filter
     * @param actual   actual filter
     */
    fun assertFilterDeepEquals(expected: NameFilter, actual: MusicFilter) {
        assertThat(actual.name).isEqualTo(expected.name)
    }

    /**
     * Asserts statistics for musics deep equals.
     *
     * @param expected expected statistics for musics
     * @param actual   actual statistics for musics
     */
    fun assertStatisticsDeepEquals(expected: MusicStatistics, actual: MusicStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
        }
    }

    /**
     * Asserts statistics for musics deep equals.
     *
     * @param expected expected statistics for musics
     * @param actual   actual statistics for musics
     */
    fun assertStatisticsDeepEquals(expected: com.github.vhromada.catalog.entity.MusicStatistics, actual: com.github.vhromada.catalog.entity.MusicStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.songsCount).isEqualTo(expected.songsCount)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

}
