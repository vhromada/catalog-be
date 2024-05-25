package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.ProgramMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.service.NormalizerService
import com.github.vhromada.catalog.utils.ProgramUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

/**
 * A class represents test for class [ProgramMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class ProgramMapperTest {

    /**
     * Instance of [NormalizerService]
     */
    @Mock
    private lateinit var normalizerService: NormalizerService

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [ProgramMapper]
     */
    private lateinit var mapper: ProgramMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = ProgramMapperImpl(normalizerService = normalizerService, uuidProvider = uuidProvider)
    }

    /**
     * Test method for [ProgramMapper.mapProgram].
     */
    @Test
    fun mapProgram() {
        val program = ProgramUtils.getDomainProgram(index = 1)

        val result = mapper.mapProgram(source = program)

        ProgramUtils.assertProgramDeepEquals(expected = program, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ProgramMapper.mapPrograms].
     */
    @Test
    fun mapPrograms() {
        val program = ProgramUtils.getDomainProgram(index = 1)

        val result = mapper.mapPrograms(source = listOf(program))

        ProgramUtils.assertProgramsDeepEquals(expected = listOf(program), actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ProgramMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = ProgramUtils.newRequest()
        whenever(normalizerService.normalize(any())).thenAnswer { it.arguments[0] }
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        ProgramUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(normalizerService).normalize(request.name!!)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ProgramMapper.mapFilter].
     */
    @Test
    fun mapFilter() {
        val source = TestConstants.NAME_FILTER

        val result = mapper.mapFilter(source = source)

        ProgramUtils.assertFilterDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ProgramMapper.mapStatistics].
     */
    @Test
    fun mapStatistics() {
        val source = ProgramUtils.getDomainStatistics()

        val result = mapper.mapStatistics(source = source)

        ProgramUtils.assertStatisticsDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

    /**
     * Test method for [ProgramMapper.mapStatistics] with null media count.
     */
    @Test
    fun mapStatisticsNullMediaCount() {
        val source = ProgramUtils.getDomainStatistics()
            .copy(mediaCount = null)

        val result = mapper.mapStatistics(source = source)

        ProgramUtils.assertStatisticsDeepEquals(expected = source, actual = result)
        verifyNoInteractions(normalizerService, uuidProvider)
    }

}
