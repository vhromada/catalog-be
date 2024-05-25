package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.facade.ShowFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.PageUtils
import com.github.vhromada.catalog.utils.ShowUtils
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
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * A class represents test for class [ShowController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(ShowController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class ShowControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [ShowFacade]
     */
    @MockBean
    private lateinit var facade: ShowFacade

    /**
     * Instance of [IssueMapper]
     */
    @MockBean
    private lateinit var issueMapper: IssueMapper

    /**
     * Instance of [ObjectMapper]
     */
    @Autowired
    private lateinit var objectMapper: ObjectMapper

    /**
     * Test method for [ShowController.search].
     */
    @Test
    fun search() {
        val shows = PageUtils.getPage(data = ShowUtils.getShows())
        val filterArgumentCaptor = argumentCaptor<MultipleNameFilter>()
        whenever(facade.search(filter = any())).thenReturn(shows)

        mockMvc.perform(get("/rest/shows"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(shows)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.czechName).isNull()
        assertThat(filter.originalName).isNull()
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [ShowController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val shows = PageUtils.getPage(data = ShowUtils.getShows())
        val namesFilter = TestConstants.MULTIPLE_NAMES_FILTER
        val filterArgumentCaptor = argumentCaptor<MultipleNameFilter>()
        whenever(facade.search(filter = any())).thenReturn(shows)

        val requestBuilder = get("/rest/shows")
            .queryParam("czechName", namesFilter.czechName)
            .queryParam("originalName", namesFilter.originalName)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(shows)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.czechName).isEqualTo(namesFilter.czechName)
        assertThat(filter.originalName).isEqualTo(namesFilter.originalName)
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [ShowController.get].
     */
    @Test
    fun get() {
        val show = ShowUtils.getShow(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(show)

        mockMvc.perform(get("/rest/shows/${show.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(show)))

        verify(facade).get(uuid = show.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ShowController.add].
     */
    @Test
    fun add() {
        val request = ShowUtils.newRequest()
        val show = ShowUtils.getShow(index = 1)
        whenever(facade.add(request = any())).thenReturn(show)

        val requestBuilder = post("/rest/shows")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(show)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ShowController.update].
     */
    @Test
    fun update() {
        val request = ShowUtils.newRequest()
        val show = ShowUtils.getShow(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(show)

        val requestBuilder = put("/rest/shows/${show.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(show)))

        verify(facade).update(uuid = show.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ShowController.remove].
     */
    @Test
    fun remove() {
        val show = ShowUtils.getShow(index = 1)

        val requestBuilder = delete("/rest/shows/${show.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = show.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ShowController.duplicate].
     */
    @Test
    fun duplicate() {
        val show = ShowUtils.getShow(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(show)

        val requestBuilder = post("/rest/shows/${show.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(show)))

        verify(facade).duplicate(uuid = show.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [ShowController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = ShowUtils.getStatistics()
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/shows/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
