package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.io.SongStatistics
import com.github.vhromada.catalog.entity.Song
import com.github.vhromada.catalog.entity.io.ChangeSongRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates song fields.
 *
 * @return updated song
 */
fun com.github.vhromada.catalog.domain.Song.updated(): com.github.vhromada.catalog.domain.Song {
    name = "Name"
    length = 5
    note = "Note"
    return this
}

/**
 * Updates song fields.
 *
 * @return updated song
 */
fun Song.updated(): Song {
    return copy(
        name = "Name",
        length = 5,
        note = "Note"
    )
}

/**
 * A class represents utility class for songs.
 *
 * @author Vladimir Hromada
 */
object SongUtils {

    /**
     * Count of songs
     */
    const val SONGS_COUNT = 9

    /**
     * Count of songs in music
     */
    const val SONGS_PER_MUSIC_COUNT = 3

    /**
     * Multipliers for length
     */
    private val LENGTH_MULTIPLIERS = intArrayOf(1, 10, 100)

    /**
     * Returns songs.
     *
     * @param music music ID
     * @return songs
     */
    fun getDomainSongs(music: Int): MutableList<com.github.vhromada.catalog.domain.Song> {
        val songs = mutableListOf<com.github.vhromada.catalog.domain.Song>()
        for (i in 1..SONGS_PER_MUSIC_COUNT) {
            songs.add(getDomainSong(musicIndex = music, songIndex = i))
        }

        return songs
    }

    /**
     * Returns songs.
     *
     * @param music music ID
     * @return songs
     */
    fun getSongs(music: Int): List<Song> {
        val songs = mutableListOf<Song>()
        for (i in 1..SONGS_PER_MUSIC_COUNT) {
            songs.add(getSong(musicIndex = music, songIndex = i))
        }

        return songs
    }

    /**
     * Returns song for indexes.
     *
     * @param musicIndex music index
     * @param songIndex  song index
     * @return song for indexes
     */
    private fun getDomainSong(musicIndex: Int, songIndex: Int): com.github.vhromada.catalog.domain.Song {
        return com.github.vhromada.catalog.domain.Song(
            id = (musicIndex - 1) * SONGS_PER_MUSIC_COUNT + songIndex,
            uuid = getUuid(index = (musicIndex - 1) * SONGS_PER_MUSIC_COUNT + songIndex),
            name = "Music $musicIndex Song $songIndex",
            length = songIndex * LENGTH_MULTIPLIERS[musicIndex - 1],
            note = if (songIndex == 2) "Music $musicIndex Song 2 note" else null
        ).fillAudit(audit = AuditUtils.getAudit())
    }

    /**
     * Returns UUID for index.
     *
     * @param index index
     * @return UUID for index
     */
    private fun getUuid(index: Int): String {
        return when (index) {
            1 -> "ec0533df-4b3d-40c5-95df-8ae7450387dd"
            2 -> "c9343de7-d1ed-4ee4-9f5e-ef5e8ce872c6"
            3 -> "c914c98f-6a25-4369-acff-f932206267d3"
            4 -> "3c5dd648-4752-4fd6-9e6b-6e778e4f212d"
            5 -> "496eea76-e42b-4684-ab29-2075fae3ea56"
            6 -> "d72d90fb-7f5a-4272-b739-6bd130f8aa3e"
            7 -> "5d470927-2d07-4261-9af1-9e29dd5850c5"
            8 -> "b2d3c5c8-8127-4136-9764-3b4794eb2caa"
            9 -> "5fdb59ca-307f-48d3-97bf-ee73422dabd5"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns song.
     *
     * @param entityManager entity manager
     * @param id            song ID
     * @return song
     */
    fun getDomainSong(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Song? {
        return entityManager.find(com.github.vhromada.catalog.domain.Song::class.java, id)
    }

    /**
     * Returns song for index.
     *
     * @param index song index
     * @return song for index
     */
    fun getSong(index: Int): Song {
        val musicNumber = (index - 1) / SONGS_PER_MUSIC_COUNT + 1
        val songNumber = (index - 1) % SONGS_PER_MUSIC_COUNT + 1

        return getSong(musicIndex = musicNumber, songIndex = songNumber)
    }

    /**
     * Returns song for indexes.
     *
     * @param musicIndex music index
     * @param songIndex  song index
     * @return song for indexes
     */
    private fun getSong(musicIndex: Int, songIndex: Int): Song {
        return Song(
            uuid = getUuid(index = (musicIndex - 1) * SONGS_PER_MUSIC_COUNT + songIndex),
            name = "Music $musicIndex Song $songIndex",
            length = songIndex * LENGTH_MULTIPLIERS[musicIndex - 1],
            note = if (songIndex == 2) "Music $musicIndex Song 2 note" else null
        )
    }

    /**
     * Returns statistics for songs.
     *
     * @return statistics for songs
     */
    fun getStatistics(): SongStatistics {
        return SongStatistics(count = SONGS_COUNT.toLong(), length = 666L)
    }

    /**
     * Returns count of songs.
     *
     * @param entityManager entity manager
     * @return count of songs
     */
    fun getSongsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(s.id) FROM Song s", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns song.
     *
     * @param id ID
     * @return song
     */
    fun newDomainSong(id: Int?): com.github.vhromada.catalog.domain.Song {
        return com.github.vhromada.catalog.domain.Song(
            id = id,
            uuid = TestConstants.UUID,
            name = "",
            length = 0,
            note = null
        ).updated()
    }

    /**
     * Returns song.
     *
     * @return song
     */
    fun newSong(): Song {
        return Song(
            uuid = TestConstants.UUID,
            name = "",
            length = 0,
            note = null
        ).updated()
    }

    /**
     * Returns request for changing song.
     *
     * @return request for changing song
     */
    fun newRequest(): ChangeSongRequest {
        return ChangeSongRequest(
            name = "Name",
            length = 5,
            note = "Note"
        )
    }

    /**
     * Asserts list of songs deep equals.
     *
     * @param expected   expected list of songs
     * @param actual     actual list of songs
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertDomainSongsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Song>, actual: List<com.github.vhromada.catalog.domain.Song>, ignoreUuid: Boolean = false) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertSongDeepEquals(expected = expected[i], actual = actual[i], ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts song deep equals.
     *
     * @param expected   expected song
     * @param actual     actual song
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertSongDeepEquals(expected: com.github.vhromada.catalog.domain.Song?, actual: com.github.vhromada.catalog.domain.Song?, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.length).isEqualTo(expected.length)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
            if (expected.music != null) {
                assertThat(actual.music).isNotNull
                assertThat(actual.music!!.songs).hasSameSizeAs(expected.music!!.songs)
                MusicUtils.assertMusicDeepEquals(expected = expected.music!!, actual = actual.music!!, checkSongs = false, ignoreUuid = ignoreUuid)
            }
        }
    }

    /**
     * Asserts list of songs deep equals.
     *
     * @param expected expected list of songs
     * @param actual   actual list of songs
     */
    fun assertSongsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Song>, actual: List<Song>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertSongDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts song deep equals.
     *
     * @param expected   expected song
     * @param actual     actual song
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertSongDeepEquals(expected: com.github.vhromada.catalog.domain.Song, actual: Song, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.length).isEqualTo(expected.length)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts list of songs deep equals.
     *
     * @param expected expected list of songs
     * @param actual   actual list of songs
     */
    fun assertSongListDeepEquals(expected: List<Song>, actual: List<Song>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertSongDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts song deep equals.
     *
     * @param expected   expected song
     * @param actual     actual song
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertSongDeepEquals(expected: Song, actual: Song, ignoreUuid: Boolean = false) {
        assertSoftly {
            if (ignoreUuid) {
                it.assertThat(actual.uuid).isNotEmpty
            } else {
                it.assertThat(actual.uuid).isEqualTo(expected.uuid)
            }
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.length).isEqualTo(expected.length)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts request and song deep equals.
     *
     * @param expected expected request for changing song
     * @param actual   actual song
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeSongRequest, actual: com.github.vhromada.catalog.domain.Song, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.length).isEqualTo(expected.length)
            it.assertThat(actual.note).isEqualTo(expected.note)
            it.assertThat(actual.music).isNull()
            it.assertThat(actual.createdUser).isNull()
            it.assertThat(actual.createdTime).isNull()
            it.assertThat(actual.updatedUser).isNull()
            it.assertThat(actual.updatedTime).isNull()
        }
    }

    /**
     * Asserts statistics for songs deep equals.
     *
     * @param expected expected statistics for songs
     * @param actual   actual statistics for songs
     */
    fun assertStatisticsDeepEquals(expected: SongStatistics, actual: SongStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.length).isEqualTo(expected.length)
        }
    }

}
