package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Song
import com.github.vhromada.catalog.domain.io.SongStatistics
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.Optional

/**
 * An interface represents repository for songs.
 *
 * @author Vladimir Hromada
 */
interface SongRepository : JpaRepository<Song, Int> {

    /**
     * Returns songs by music's ID.
     *
     * @param id       music's ID
     * @param pageable paging information
     * @return songs
     */
    fun findAllByMusicId(id: Int, pageable: Pageable): Page<Song>

    /**
     * Finds song by UUID.
     *
     * @param uuid UUID
     * @return song
     */
    fun findByUuid(uuid: String): Optional<Song>

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @Query("SELECT new com.github.vhromada.catalog.domain.io.SongStatistics(COUNT(s.id), SUM(s.length)) FROM Song s")
    fun getStatistics(): SongStatistics

}
