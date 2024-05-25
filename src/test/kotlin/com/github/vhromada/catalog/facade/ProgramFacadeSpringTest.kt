package com.github.vhromada.catalog.facade

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.utils.AuditUtils
import com.github.vhromada.catalog.utils.ProgramUtils
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
 * A class represents test for class [ProgramFacade].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class ProgramFacadeSpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [ProgramFacade]
     */
    @Autowired
    private lateinit var facade: ProgramFacade

    /**
     * Test method for [ProgramFacade.search].
     */
    @Test
    fun search() {
        val filter = NameFilter()
        filter.page = 1
        filter.limit = ProgramUtils.PROGRAMS_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }
        ProgramUtils.assertProgramListDeepEquals(expected = ProgramUtils.getPrograms(), actual = result.data)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.search] with paging.
     */
    @Test
    fun searchPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = 1
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
        }
        ProgramUtils.assertProgramListDeepEquals(expected = listOf(ProgramUtils.getProgram(index = 2)), actual = result.data)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.search] with invalid paging.
     */
    @Test
    fun searchInvalidPaging() {
        val filter = NameFilter()
        filter.page = 2
        filter.limit = ProgramUtils.PROGRAMS_COUNT
        val result = facade.search(filter = filter)

        assertSoftly {
            it.assertThat(result.data).isEmpty()
            it.assertThat(result.pagingInfo.pageNumber).isEqualTo(2)
            it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
        }

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.search] with filter.
     */
    @Test
    fun searchFilter() {
        for (i in 1..ProgramUtils.PROGRAMS_COUNT) {
            val program = ProgramUtils.getProgram(index = i)
            val filter = NameFilter(name = program.name)
            filter.page = 1
            filter.limit = ProgramUtils.PROGRAMS_COUNT

            val result = facade.search(filter = filter)

            assertSoftly {
                it.assertThat(result.pagingInfo.pageNumber).isEqualTo(1)
                it.assertThat(result.pagingInfo.pagesCount).isEqualTo(1)
            }
            ProgramUtils.assertProgramListDeepEquals(expected = listOf(program), actual = result.data)
        }

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.get].
     */
    @Test
    fun get() {
        for (i in 1..ProgramUtils.PROGRAMS_COUNT) {
            val program = ProgramUtils.getProgram(index = i)

            val result = facade.get(uuid = program.uuid)

            ProgramUtils.assertProgramDeepEquals(expected = ProgramUtils.getProgram(index = i), actual = result)
        }

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.get] with not existing UUID.
     */
    @Test
    fun getNotExisting() {
        assertThatThrownBy { facade.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NOT_EXIST")
            .hasMessageContaining("Program doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add].
     */
    @Test
    @DirtiesContext
    fun add() {
        val expectedProgram = ProgramUtils.newProgram()
        val expectedDomainProgram = ProgramUtils.newDomainProgram(id = ProgramUtils.PROGRAMS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.add(request = ProgramUtils.newRequest())
        entityManager.flush()

        ProgramUtils.assertProgramDeepEquals(expected = expectedProgram, actual = result, ignoreUuid = true)
        ProgramUtils.assertProgramDeepEquals(
            expected = expectedDomainProgram,
            actual = ProgramUtils.getDomainProgram(entityManager = entityManager, id = ProgramUtils.PROGRAMS_COUNT + 1),
            ignoreUuid = true
        )

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT + 1)
    }

    /**
     * Test method for [ProgramFacade.add] with request with null name.
     */
    @Test
    fun addNullName() {
        val request = ProgramUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add] with request with empty string as name.
     */
    @Test
    fun addEmptyName() {
        val request = ProgramUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add] with program with null count of media.
     */
    @Test
    fun addNullMediaCount() {
        val request = ProgramUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add] with program with not positive count of media.
     */
    @Test
    fun addNotPositiveMediaCount() {
        val request = ProgramUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add] with program with null format.
     */
    @Test
    fun addNullFormat() {
        val request = ProgramUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add] with program with null crack.
     */
    @Test
    fun addNullCrack() {
        val request = ProgramUtils.newRequest()
            .copy(crack = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_CRACK_NULL")
            .hasMessageContaining("Crack mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add] with program with null serial key.
     */
    @Test
    fun addNullSerialKey() {
        val request = ProgramUtils.newRequest()
            .copy(serialKey = null)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_SERIAL_KEY_NULL")
            .hasMessageContaining("Serial key mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.add] with program with not existing format.
     */
    @Test
    fun addNotExistingFormat() {
        val request = ProgramUtils.newRequest()
            .copy(format = TestConstants.UUID)

        assertThatThrownBy { facade.add(request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update].
     */
    @Test
    @DirtiesContext
    fun update() {
        val request = ProgramUtils.newRequest()
        val expectedProgram = ProgramUtils.getProgram(index = 1)
            .updated()
        val expectedDomainProgram = ProgramUtils.getDomainProgram(index = 1)
            .updated()
            .fillAudit(audit = AuditUtils.updatedAudit())

        val result = facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request)
        entityManager.flush()

        ProgramUtils.assertProgramDeepEquals(expected = expectedProgram, actual = result)
        ProgramUtils.assertProgramDeepEquals(expected = expectedDomainProgram, actual = ProgramUtils.getDomainProgram(entityManager = entityManager, id = 1))

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with request with null name.
     */
    @Test
    fun updateNullName() {
        val request = ProgramUtils.newRequest()
            .copy(name = null)

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NAME_NULL")
            .hasMessageContaining("Name mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with request with empty string as name.
     */
    @Test
    fun updateEmptyName() {
        val request = ProgramUtils.newRequest()
            .copy(name = "")

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NAME_EMPTY")
            .hasMessageContaining("Name mustn't be empty string.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)
    }

    /**
     * Test method for [ProgramFacade.update] with program with null count of media.
     */
    @Test
    fun updateNullMediaCount() {
        val request = ProgramUtils.newRequest()
            .copy(mediaCount = null)

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_MEDIA_COUNT_NULL")
            .hasMessageContaining("Count of media mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with program with not positive count of media.
     */
    @Test
    fun updateNotPositiveMediaCount() {
        val request = ProgramUtils.newRequest()
            .copy(mediaCount = 0)

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_MEDIA_COUNT_NOT_POSITIVE")
            .hasMessageContaining("Count of media must be positive number.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with program with null format.
     */
    @Test
    fun updateNullFormat() {
        val request = ProgramUtils.newRequest()
            .copy(format = null)

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_FORMAT_NULL")
            .hasMessageContaining("Format mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with program with null crack.
     */
    @Test
    fun updateNullCrack() {
        val request = ProgramUtils.newRequest()
            .copy(crack = null)

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_CRACK_NULL")
            .hasMessageContaining("Crack mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with program with null serial key.
     */
    @Test
    fun updateNullSerialKey() {
        val request = ProgramUtils.newRequest()
            .copy(serialKey = null)

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_SERIAL_KEY_NULL")
            .hasMessageContaining("Serial key mustn't be null.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.UNPROCESSABLE_ENTITY)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with program with not existing format.
     */
    @Test
    fun updateNotExistingFormat() {
        val request = ProgramUtils.newRequest()
            .copy(format = TestConstants.UUID)

        assertThatThrownBy { facade.update(uuid = ProgramUtils.getDomainProgram(index = 1).uuid, request = request) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.update] with not existing UUID.
     */
    @Test
    fun updateNotExisting() {
        assertThatThrownBy { facade.update(uuid = TestConstants.UUID, request = ProgramUtils.newRequest()) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NOT_EXIST")
            .hasMessageContaining("Program doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.remove].
     */
    @Test
    fun remove() {
        facade.remove(uuid = ProgramUtils.getProgram(index = 1).uuid)
        entityManager.flush()

        assertThat(ProgramUtils.getDomainProgram(entityManager = entityManager, id = 1)).isNull()

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT - 1)
    }

    /**
     * Test method for [ProgramFacade.remove] with not existing UUID.
     */
    @Test
    fun removeNotExisting() {
        assertThatThrownBy { facade.remove(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NOT_EXIST")
            .hasMessageContaining("Program doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.duplicate].
     */
    @Test
    @DirtiesContext
    fun duplicate() {
        val expectedProgram = ProgramUtils.getProgram(index = 1)
        val expectedDomainProgram = ProgramUtils.getDomainProgram(index = 1)
            .copy(id = ProgramUtils.PROGRAMS_COUNT + 1)
            .fillAudit(audit = AuditUtils.newAudit())

        val result = facade.duplicate(uuid = ProgramUtils.getProgram(index = 1).uuid)
        entityManager.flush()

        ProgramUtils.assertProgramDeepEquals(expected = expectedProgram, actual = result, ignoreUuid = true)
        ProgramUtils.assertProgramDeepEquals(
            expected = expectedDomainProgram,
            actual = ProgramUtils.getDomainProgram(entityManager = entityManager, id = ProgramUtils.PROGRAMS_COUNT + 1),
            ignoreUuid = true
        )

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT + 1)
    }

    /**
     * Test method for [ProgramFacade.duplicate] with not existing UUID.
     */
    @Test
    fun duplicateNotExisting() {
        assertThatThrownBy { facade.duplicate(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NOT_EXIST")
            .hasMessageContaining("Program doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

    /**
     * Test method for [ProgramFacade.getStatistics].
     */
    @Test
    fun getStatistics() {
        val result = facade.getStatistics()

        ProgramUtils.assertStatisticsDeepEquals(expected = ProgramUtils.getDomainStatistics(), actual = result)

        assertThat(ProgramUtils.getProgramsCount(entityManager = entityManager)).isEqualTo(ProgramUtils.PROGRAMS_COUNT)
    }

}
