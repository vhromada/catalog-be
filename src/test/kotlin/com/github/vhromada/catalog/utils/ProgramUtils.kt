package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.filter.ProgramFilter
import com.github.vhromada.catalog.domain.io.ProgramStatistics
import com.github.vhromada.catalog.entity.Program
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeProgramRequest
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * Updates program fields.
 *
 * @return updated program
 */
fun com.github.vhromada.catalog.domain.Program.updated(): com.github.vhromada.catalog.domain.Program {
    name = "Name"
    normalizedName = "Name"
    wikiEn = "enWiki"
    wikiCz = "czWiki"
    mediaCount = 1
    format = "STEAM"
    crack = true
    serialKey = true
    otherData = "Other data"
    note = "Note"
    return this
}

/**
 * Updates program fields.
 *
 * @return updated program
 */
fun Program.updated(): Program {
    return copy(
        name = "Name",
        wikiEn = "enWiki",
        wikiCz = "czWiki",
        mediaCount = 1,
        format = "STEAM",
        crack = true,
        serialKey = true,
        otherData = "Other data",
        note = "Note"
    )
}

/**
 * A class represents utility class for programs.
 *
 * @author Vladimir Hromada
 */
object ProgramUtils {

    /**
     * Count of programs
     */
    const val PROGRAMS_COUNT = 3

    /**
     * Multiplier for media count
     */
    private const val MEDIA_COUNT_MULTIPLIER = 100

    /**
     * Returns programs.
     *
     * @return programs
     */
    fun getDomainPrograms(): List<com.github.vhromada.catalog.domain.Program> {
        val programs = mutableListOf<com.github.vhromada.catalog.domain.Program>()
        for (i in 1..PROGRAMS_COUNT) {
            programs.add(getDomainProgram(index = i))
        }

        return programs
    }

    /**
     * Returns programs.
     *
     * @return programs
     */
    fun getPrograms(): List<Program> {
        val programs = mutableListOf<Program>()
        for (i in 1..PROGRAMS_COUNT) {
            programs.add(getProgram(index = i))
        }

        return programs
    }

    /**
     * Returns program for index.
     *
     * @param index index
     * @return program for index
     */
    fun getDomainProgram(index: Int): com.github.vhromada.catalog.domain.Program {
        val name = "Program $index name"
        return com.github.vhromada.catalog.domain.Program(
            id = index,
            uuid = getUuid(index = index),
            name = name,
            normalizedName = name,
            wikiEn = if (index != 3) "Program $index English Wikipedia" else null,
            wikiCz = if (index != 3) "Program $index Czech Wikipedia" else null,
            mediaCount = index * MEDIA_COUNT_MULTIPLIER,
            format = getFormat(index = index),
            crack = index == 3,
            serialKey = index != 1,
            otherData = if (index == 3) "Program $index other data" else null,
            note = if (index == 3) "Program $index note" else null
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
            1 -> "bd6b8a6f-5983-494b-aabb-9046162f6bbb"
            2 -> "f5593664-e2c0-4219-881c-59ee040f0305"
            3 -> "860c1aa7-d4c0-40f9-9fb4-eb657c121d84"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns format for index.
     *
     * @param index index
     * @return format for index
     */
    private fun getFormat(index: Int): String {
        return when (index) {
            1 -> "ISO"
            2 -> "STEAM"
            3 -> "BINARY"
            else -> throw IllegalArgumentException("Bad index")
        }
    }

    /**
     * Returns program.
     *
     * @param entityManager entity manager
     * @param id            program ID
     * @return program
     */
    fun getDomainProgram(entityManager: EntityManager, id: Int): com.github.vhromada.catalog.domain.Program? {
        return entityManager.find(com.github.vhromada.catalog.domain.Program::class.java, id)
    }

    /**
     * Returns program for index.
     *
     * @param index index
     * @return program for index
     */
    fun getProgram(index: Int): Program {
        return Program(
            uuid = getUuid(index = index),
            name = "Program $index name",
            wikiEn = if (index != 3) "Program $index English Wikipedia" else null,
            wikiCz = if (index != 3) "Program $index Czech Wikipedia" else null,
            mediaCount = index * MEDIA_COUNT_MULTIPLIER,
            format = getFormat(index = index),
            crack = index == 3,
            serialKey = index != 1,
            otherData = if (index == 3) "Program $index other data" else null,
            note = if (index == 3) "Program $index note" else null
        )
    }

    /**
     * Returns statistics for programs.
     *
     * @return statistics for programs
     */
    fun getDomainStatistics(): ProgramStatistics {
        return ProgramStatistics(count = PROGRAMS_COUNT.toLong(), mediaCount = 600L)
    }

    /**
     * Returns statistics for programs.
     *
     * @return statistics for programs
     */
    fun getStatistics(): com.github.vhromada.catalog.entity.ProgramStatistics {
        return com.github.vhromada.catalog.entity.ProgramStatistics(count = PROGRAMS_COUNT, mediaCount = 600)
    }

    /**
     * Returns count of programs.
     *
     * @param entityManager entity manager
     * @return count of programs
     */
    fun getProgramsCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(p.id) FROM Program p", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns program.
     *
     * @param id ID
     * @return program
     */
    fun newDomainProgram(id: Int?): com.github.vhromada.catalog.domain.Program {
        return com.github.vhromada.catalog.domain.Program(
            id = id,
            uuid = TestConstants.UUID,
            name = "",
            normalizedName = "",
            wikiEn = null,
            wikiCz = null,
            mediaCount = 0,
            format = "STEAM",
            crack = false,
            serialKey = false,
            otherData = null,
            note = null
        ).updated()
    }

    /**
     * Returns program.
     *
     * @return program
     */
    fun newProgram(): Program {
        return Program(
            uuid = TestConstants.UUID,
            name = "",
            wikiEn = null,
            wikiCz = null,
            mediaCount = 0,
            format = "STEAM",
            crack = false,
            serialKey = false,
            otherData = null,
            note = null
        ).updated()
    }

    /**
     * Returns request for changing program.
     *
     * @return request for changing program
     */
    fun newRequest(): ChangeProgramRequest {
        return ChangeProgramRequest(
            name = "Name",
            wikiEn = "enWiki",
            wikiCz = "czWiki",
            mediaCount = 1,
            format = "STEAM",
            crack = true,
            serialKey = true,
            otherData = "Other data",
            note = "Note"
        )
    }

    /**
     * Asserts list of programs deep equals.
     *
     * @param expected expected list of programs
     * @param actual   actual list of programs
     */
    fun assertDomainProgramsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Program>, actual: List<com.github.vhromada.catalog.domain.Program>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertProgramDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts program deep equals.
     *
     * @param expected   expected program
     * @param actual     actual program
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertProgramDeepEquals(expected: com.github.vhromada.catalog.domain.Program?, actual: com.github.vhromada.catalog.domain.Program?, ignoreUuid: Boolean = false) {
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
                it.assertThat(actual.format).isEqualTo(expected.format)
                it.assertThat(actual.crack).isEqualTo(expected.crack)
                it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
                it.assertThat(actual.otherData).isEqualTo(expected.otherData)
                it.assertThat(actual.note).isEqualTo(expected.note)
            }
            AuditUtils.assertAuditDeepEquals(expected = expected, actual = actual!!)
        }
    }

    /**
     * Asserts list of programs deep equals.
     *
     * @param expected expected list of programs
     * @param actual   actual list of programs
     */
    fun assertProgramsDeepEquals(expected: List<com.github.vhromada.catalog.domain.Program>, actual: List<Program>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertProgramDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts program deep equals.
     *
     * @param expected   expected program
     * @param actual     actual program
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertProgramDeepEquals(expected: com.github.vhromada.catalog.domain.Program, actual: Program, ignoreUuid: Boolean = false) {
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
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.crack).isEqualTo(expected.crack)
            it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
            it.assertThat(actual.otherData).isEqualTo(expected.otherData)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts list of programs deep equals.
     *
     * @param expected expected list of programs
     * @param actual   actual list of programs
     */
    fun assertProgramListDeepEquals(expected: List<Program>, actual: List<Program>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertProgramDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts program deep equals.
     *
     * @param expected   expected program
     * @param actual     actual program
     * @param ignoreUuid true if UUID should be ignored
     */
    fun assertProgramDeepEquals(expected: Program, actual: Program, ignoreUuid: Boolean = false) {
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
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.crack).isEqualTo(expected.crack)
            it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
            it.assertThat(actual.otherData).isEqualTo(expected.otherData)
            it.assertThat(actual.note).isEqualTo(expected.note)
        }
    }

    /**
     * Asserts request and program deep equals.
     *
     * @param expected expected request for changing program
     * @param actual   actual program
     * @param uuid     UUID
     */
    fun assertRequestDeepEquals(expected: ChangeProgramRequest, actual: com.github.vhromada.catalog.domain.Program, uuid: String) {
        assertSoftly {
            it.assertThat(actual.id).isNull()
            it.assertThat(actual.uuid).isEqualTo(uuid)
            it.assertThat(actual.name).isEqualTo(expected.name)
            it.assertThat(actual.normalizedName).isEqualTo(expected.name)
            it.assertThat(actual.wikiEn).isEqualTo(expected.wikiEn)
            it.assertThat(actual.wikiCz).isEqualTo(expected.wikiCz)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
            it.assertThat(actual.format).isEqualTo(expected.format)
            it.assertThat(actual.crack).isEqualTo(expected.crack)
            it.assertThat(actual.serialKey).isEqualTo(expected.serialKey)
            it.assertThat(actual.otherData).isEqualTo(expected.otherData)
            it.assertThat(actual.note).isEqualTo(expected.note)
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
    fun assertFilterDeepEquals(expected: NameFilter, actual: ProgramFilter) {
        assertThat(actual.name).isEqualTo(expected.name)
    }

    /**
     * Asserts statistics for programs deep equals.
     *
     * @param expected expected statistics for programs
     * @param actual   actual statistics for programs
     */
    fun assertStatisticsDeepEquals(expected: ProgramStatistics, actual: ProgramStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount)
        }
    }

    /**
     * Asserts statistics for programs deep equals.
     *
     * @param expected expected statistics for programs
     * @param actual   actual statistics for programs
     */
    fun assertStatisticsDeepEquals(expected: ProgramStatistics, actual: com.github.vhromada.catalog.entity.ProgramStatistics) {
        assertSoftly {
            it.assertThat(actual.count).isEqualTo(expected.count)
            if (expected.mediaCount == null) {
                it.assertThat(actual.mediaCount).isZero
            } else {
                it.assertThat(actual.mediaCount).isEqualTo(expected.mediaCount!!.toInt())
            }
        }
    }

}
