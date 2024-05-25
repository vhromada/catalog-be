package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Program
import com.github.vhromada.catalog.domain.filter.ProgramFilter
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.mapper.ProgramMapper
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.repository.ProgramRepository
import com.github.vhromada.catalog.service.impl.ProgramServiceImpl
import com.github.vhromada.catalog.utils.ProgramUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [ProgramService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ProgramServiceTest {

    /**
     * Instance of [ProgramRepository]
     */
    @Mock
    private lateinit var repository: ProgramRepository

    /**
     * Instance of [ProgramMapper]
     */
    @Mock
    private lateinit var mapper: ProgramMapper

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [ProgramService]
     */
    private lateinit var service: ProgramService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = ProgramServiceImpl(repository = repository, mapper = mapper, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [ProgramService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(ProgramUtils.getDomainProgram(index = 1), ProgramUtils.getDomainProgram(index = 2)))
        whenever(repository.findAll(any<Specification<Program>>(), any<Pageable>())).thenReturn(page)

        val result = service.search(filter = ProgramFilter(name = "Name"), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(any<Specification<Program>>(), eq(pageable))
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [ProgramService.search] with empty filter.
     */
    @Test
    fun searchEmptyFilter() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(ProgramUtils.getDomainProgram(index = 1), ProgramUtils.getDomainProgram(index = 2)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(filter = ProgramFilter(), pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(pageable)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [ProgramService.get] with existing program.
     */
    @Test
    fun getExisting() {
        val program = ProgramUtils.getDomainProgram(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(program))

        val result = service.get(uuid = program.uuid)

        assertThat(result).isEqualTo(program)
        verify(repository).findByUuid(uuid = program.uuid)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [ProgramService.get] with not existing program.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PROGRAM_NOT_EXIST")
            .hasMessageContaining("Program doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [ProgramService.store].
     */
    @Test
    fun store() {
        val program = ProgramUtils.getDomainProgram(index = 1)
        whenever(repository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(program = program)

        assertThat(result).isSameAs(program)
        verify(repository).save(program)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [ProgramService.remove].
     */
    @Test
    fun remove() {
        val program = ProgramUtils.getDomainProgram(index = 1)

        service.remove(program = program)

        verify(repository).delete(program)
        verifyNoMoreInteractions(repository)
        verifyNoInteractions(mapper, uuidProvider)
    }

    /**
     * Test method for [ProgramService.duplicate].
     */
    @Test
    fun duplicate() {
        val expectedProgram = ProgramUtils.getDomainProgram(index = 1)
            .copy(id = 0, uuid = TestConstants.UUID)
        val copyArgumentCaptor = argumentCaptor<Program>()
        whenever(repository.save(anyDomain())).thenAnswer {
            val argument = it.arguments[0] as Program
            argument.id = 0
            argument
        }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = service.duplicate(program = ProgramUtils.getDomainProgram(index = 1))

        ProgramUtils.assertProgramDeepEquals(expected = expectedProgram, actual = result)
        verify(repository).save(copyArgumentCaptor.capture())
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(repository, uuidProvider)
        verifyNoInteractions(mapper)
        assertThat(result).isSameAs(copyArgumentCaptor.lastValue)
    }

    /**
     * Test method for [ProgramService.getStatistics].
     */
    @Test
    fun getStatistics() {
        val domain = ProgramUtils.getDomainStatistics()
        val entity = ProgramUtils.getStatistics()
        whenever(repository.getStatistics()).thenReturn(domain)
        whenever(mapper.mapStatistics(source = any())).thenReturn(entity)

        val result = service.getStatistics()

        assertThat(result).isEqualTo(entity)
        verify(repository).getStatistics()
        verify(mapper).mapStatistics(domain)
        verifyNoMoreInteractions(repository, mapper)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Returns any mock for domain program.
     *
     * @return any mock for domain program
     */
    private fun anyDomain(): Program {
        return any()
    }

}
