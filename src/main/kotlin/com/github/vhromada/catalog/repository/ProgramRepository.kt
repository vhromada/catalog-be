package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.domain.Program
import com.github.vhromada.catalog.domain.io.ProgramStatistics
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import java.util.Optional

/**
 * An interface represents repository for programs.
 *
 * @author Vladimir Hromada
 */
interface ProgramRepository : JpaRepository<Program, Int>, JpaSpecificationExecutor<Program> {

    /**
     * Finds program by UUID.
     *
     * @param uuid UUID
     * @return program
     */
    fun findByUuid(uuid: String): Optional<Program>

    /**
     * Returns statistics.
     *
     * @return statistics
     */
    @Query("SELECT new com.github.vhromada.catalog.domain.io.ProgramStatistics(COUNT(p.id), SUM(p.mediaCount)) FROM Program p")
    fun getStatistics(): ProgramStatistics

}
