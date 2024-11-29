package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.facade.SeasonFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.PageUtils
import com.github.vhromada.catalog.utils.SeasonUtils
import com.github.vhromada.catalog.utils.ShowUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
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
 * A class represents test for class [SeasonController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(SeasonController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class SeasonControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [SeasonFacade]
     */
    @MockitoBean
    private lateinit var facade: SeasonFacade

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
     * Show
     */
    private val show = ShowUtils.getShow(index = 1)

    /**
     * Test method for [SeasonController.search].
     */
    @Test
    fun search() {
        val seasons = PageUtils.getPage(data = SeasonUtils.getSeasons(show = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(show = any(), filter = any())).thenReturn(seasons)

        mockMvc.perform(get("/rest/shows/${show.uuid}/seasons"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(seasons)))

        verify(facade).findAll(show = eq(show.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [SeasonController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val seasons = PageUtils.getPage(data = SeasonUtils.getSeasons(show = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(show = any(), filter = any())).thenReturn(seasons)

        val requestBuilder = get("/rest/shows/${show.uuid}/seasons")
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(seasons)))

        verify(facade).findAll(show = eq(show.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [SeasonController.get].
     */
    @Test
    fun get() {
        val season = SeasonUtils.getSeason(index = 1)
        whenever(facade.get(show = any(), uuid = any())).thenReturn(season)

        mockMvc.perform(get("/rest/shows/${show.uuid}/seasons/${season.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(season)))

        verify(facade).get(show = show.uuid, uuid = season.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SeasonController.add].
     */
    @Test
    fun add() {
        val request = SeasonUtils.newRequest()
        val season = SeasonUtils.getSeason(index = 1)
        whenever(facade.add(show = any(), request = any())).thenReturn(season)

        val requestBuilder = post("/rest/shows/${show.uuid}/seasons")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(season)))

        verify(facade).add(show = show.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SeasonController.update].
     */
    @Test
    fun update() {
        val request = SeasonUtils.newRequest()
        val season = SeasonUtils.getSeason(index = 1)
        whenever(facade.update(show = any(), uuid = any(), request = any())).thenReturn(season)

        val requestBuilder = put("/rest/shows/${show.uuid}/seasons/${season.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(season)))

        verify(facade).update(show = show.uuid, uuid = season.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SeasonController.remove].
     */
    @Test
    fun remove() {
        val season = SeasonUtils.getSeason(index = 1)

        val requestBuilder = delete("/rest/shows/${show.uuid}/seasons/${season.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(show = show.uuid, uuid = season.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SeasonController.duplicate].
     */
    @Test
    fun duplicate() {
        val season = SeasonUtils.getSeason(index = 1)
        whenever(facade.duplicate(show = any(), uuid = any())).thenReturn(season)

        val requestBuilder = post("/rest/shows/${show.uuid}/seasons/${season.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(season)))

        verify(facade).duplicate(show = show.uuid, uuid = season.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
