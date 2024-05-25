package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.domain.filter.ProgramFilter
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.ProgramUtils
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
 * A class represents test for class [ProgramRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class ProgramRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [ProgramRepository]
     */
    @Autowired
    private lateinit var repository: ProgramRepository

    /**
     * Test method for get programs.
     */
    @Test
    fun getPrograms() {
        val programs = repository.findAll()

        ProgramUtils.assertDomainProgramsDeepEquals(expected = ProgramUtils.getDomainPrograms(), actual = programs)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for get program.
     */
    @Test
    fun getProgram() {
        for (i in 1..ProgramUtils.PROGRAMS_COUNT) {
            val program = repository.findById(i).orElse(null)

            ProgramUtils.assertProgramDeepEquals(expected = ProgramUtils.getDomainProgram(index = i), actual = program)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for add program.
     */
    @Test
    @DirtiesContext
    fun add() {
        val program = ProgramUtils.newDomainProgram(id = null)
        val expectedProgram = ProgramUtils.newDomainProgram(id = ProgramUtils.PROGRAMS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        repository.saveAndFlush(program)

        assertSoftly {
            it.assertThat(program.id).isEqualTo(ProgramUtils.PROGRAMS_COUNT + 1)
            it.assertThat(program.createdUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(program.createdTime).isEqualTo(TestConstants.TIME)
            it.assertThat(program.updatedUser).isEqualTo(AccountUtils.getDomainAccount(index = 2).uuid)
            it.assertThat(program.updatedTime).isEqualTo(TestConstants.TIME)
        }
        ProgramUtils.assertProgramDeepEquals(expected = expectedProgram, actual = ProgramUtils.getDomainProgram(entityManager = entityManager, id = ProgramUtils.PROGRAMS_COUNT + 1))

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT + 1)
    }

    /**
     * Test method for update program.
     */
    @Test
    fun update() {
        val program = ProgramUtils.getDomainProgram(entityManager = entityManager, id = 1)!!
            .updated()
        val expectedProgram = ProgramUtils.getDomainProgram(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        repository.saveAndFlush(program)

        ProgramUtils.assertProgramDeepEquals(expected = expectedProgram, actual = ProgramUtils.getDomainProgram(entityManager = entityManager, id = 1))

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for remove program.
     */
    @Test
    fun remove() {
        repository.delete(ProgramUtils.getDomainProgram(entityManager = entityManager, id = 1)!!)

        assertThat(ProgramUtils.getDomainProgram(entityManager = entityManager, id = 1)).isNull()

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT - 1)
    }

    /**
     * Test method for remove all programs.
     */
    @Test
    fun removeAll() {
        repository.deleteAll()

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(0)
    }

    /**
     * Test method for search programs by filter.
     */
    @Test
    fun searchByFilter() {
        for (i in 1..ProgramUtils.PROGRAMS_COUNT) {
            val program = ProgramUtils.getDomainProgram(index = i)
            val filter = ProgramFilter(name = program.name)

            val result = repository.findAll(filter.toSpecification())

            ProgramUtils.assertDomainProgramsDeepEquals(expected = listOf(program), actual = result.toList())
        }

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for find program by UUID.
     */
    @Test
    fun findByUuid() {
        for (i in 1..ProgramUtils.PROGRAMS_COUNT) {
            val program = ProgramUtils.getDomainProgram(index = i)

            val result = repository.findByUuid(uuid = program.uuid).orElse(null)

            ProgramUtils.assertProgramDeepEquals(expected = ProgramUtils.getDomainProgram(index = i), actual = result)
        }

        assertThat(repository.findByUuid(uuid = TestConstants.UUID)).isNotPresent

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for get statistics.
     */
    @Test
    fun getStatistics() {
        val result = repository.getStatistics()

        ProgramUtils.assertStatisticsDeepEquals(expected = ProgramUtils.getDomainStatistics(), actual = result)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

}
