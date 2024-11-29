package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.facade.ProgramFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.PageUtils
import com.github.vhromada.catalog.utils.ProgramUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * A class represents test for class [ProgramController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(ProgramController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class ProgramControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [ProgramFacade]
     */
    @MockitoBean
    private lateinit var facade: ProgramFacade

    /**
     * Instance of [IssueMapper]
     */
    @MockitoBean
    private lateinit var issueMapper: IssueMapper

    /**
     * Instance of [ObjectMapper]
     */
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    /**
     * Test method for [ProgramController.search].
     */
    @Test
    fun search() {
        val programs = PageUtils.getPage(data = ProgramUtils.getPrograms())
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(programs)

        mockMvc.perform(get("/rest/programs"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(programs)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.name).isNull()
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [ProgramController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val programs = PageUtils.getPage(data = ProgramUtils.getPrograms())
        val nameFilter = TestConstants.NAME_FILTER
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(programs)

        val requestBuilder = get("/rest/programs")
            .queryParam("name", nameFilter.name)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(programs)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.name).isEqualTo(nameFilter.name)
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [ProgramController.get].
     */
    @Test
    fun get() {
        val program = ProgramUtils.getProgram(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(program)

        mockMvc.perform(get("/rest/programs/${program.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(program)))

        verify(facade).get(uuid = program.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ProgramController.add].
     */
    @Test
    fun add() {
        val request = ProgramUtils.newRequest()
        val program = ProgramUtils.getProgram(index = 1)
        whenever(facade.add(request = any())).thenReturn(program)

        val requestBuilder = post("/rest/programs")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(program)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ProgramController.update].
     */
    @Test
    fun update() {
        val request = ProgramUtils.newRequest()
        val program = ProgramUtils.getProgram(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(program)

        val requestBuilder = put("/rest/programs/${program.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(program)))

        verify(facade).update(uuid = program.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ProgramController.remove].
     */
    @Test
    fun remove() {
        val program = ProgramUtils.getProgram(index = 1)

        val requestBuilder = delete("/rest/programs/${program.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = program.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ProgramController.duplicate].
     */
    @Test
    fun duplicate() {
        val program = ProgramUtils.getProgram(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(program)

        val requestBuilder = post("/rest/programs/${program.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(program)))

        verify(facade).duplicate(uuid = program.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ProgramController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = ProgramUtils.getStatistics()
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/programs/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
