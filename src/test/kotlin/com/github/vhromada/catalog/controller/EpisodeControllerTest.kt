package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.facade.EpisodeFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.EpisodeUtils
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
 * A class represents test for class [EpisodeController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(EpisodeController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class EpisodeControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [EpisodeFacade]
     */
    @MockBean
    private lateinit var facade: EpisodeFacade

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
     * Show
     */
    private val show = ShowUtils.getShow(index = 1)

    /**
     * Season
     */
    private val season = SeasonUtils.getSeason(index = 1)

    /**
     * Test method for [EpisodeController.search].
     */
    @Test
    fun search() {
        val episodes = PageUtils.getPage(data = EpisodeUtils.getEpisodes(show = 1, season = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(show = any(), season = any(), filter = any())).thenReturn(episodes)

        mockMvc.perform(get("/rest/shows/${show.uuid}/seasons/${season.uuid}/episodes"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(episodes)))

        verify(facade).findAll(show = eq(show.uuid), season = eq(season.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [EpisodeController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val episodes = PageUtils.getPage(data = EpisodeUtils.getEpisodes(show = 1, season = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(show = any(), season = any(), filter = any())).thenReturn(episodes)

        val requestBuilder = get("/rest/shows/${show.uuid}/seasons/${season.uuid}/episodes")
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(episodes)))

        verify(facade).findAll(show = eq(show.uuid), season = eq(season.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [EpisodeController.get].
     */
    @Test
    fun get() {
        val episode = EpisodeUtils.getEpisode(index = 1)
        whenever(facade.get(show = any(), season = any(), uuid = any())).thenReturn(episode)

        mockMvc.perform(get("/rest/shows/${show.uuid}/seasons/${season.uuid}/episodes/${episode.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(episode)))

        verify(facade).get(show = show.uuid, season = season.uuid, uuid = episode.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [EpisodeController.add].
     */
    @Test
    fun add() {
        val request = EpisodeUtils.newRequest()
        val episode = EpisodeUtils.getEpisode(index = 1)
        whenever(facade.add(show = any(), season = any(), request = any())).thenReturn(episode)

        val requestBuilder = post("/rest/shows/${show.uuid}/seasons/${season.uuid}/episodes")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(episode)))

        verify(facade).add(show = show.uuid, season = season.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [EpisodeController.update].
     */
    @Test
    fun update() {
        val request = EpisodeUtils.newRequest()
        val episode = EpisodeUtils.getEpisode(index = 1)
        whenever(facade.update(show = any(), season = any(), uuid = any(), request = any())).thenReturn(episode)

        val requestBuilder = put("/rest/shows/${show.uuid}/seasons/${season.uuid}/episodes/${episode.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(episode)))

        verify(facade).update(show = show.uuid, season = season.uuid, uuid = episode.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [EpisodeController.remove].
     */
    @Test
    fun remove() {
        val episode = EpisodeUtils.getEpisode(index = 1)

        val requestBuilder = delete("/rest/shows/${show.uuid}/seasons/${season.uuid}/episodes/${episode.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(show = show.uuid, season = season.uuid, uuid = episode.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [EpisodeController.duplicate].
     */
    @Test
    fun duplicate() {
        val episode = EpisodeUtils.getEpisode(index = 1)
        whenever(facade.duplicate(show = any(), season = any(), uuid = any())).thenReturn(episode)

        val requestBuilder = post("/rest/shows/${show.uuid}/seasons/${season.uuid}/episodes/${episode.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(episode)))

        verify(facade).duplicate(show = show.uuid, season = season.uuid, uuid = episode.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
