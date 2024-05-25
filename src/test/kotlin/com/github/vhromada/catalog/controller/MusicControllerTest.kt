package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.facade.MusicFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.utils.PageUtils
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
 * A class represents test for class [MusicController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(MusicController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class MusicControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [MusicFacade]
     */
    @MockBean
    private lateinit var facade: MusicFacade

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
     * Test method for [MusicController.search].
     */
    @Test
    fun search() {
        val music = PageUtils.getPage(data = MusicUtils.getMusicList())
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(music)

        mockMvc.perform(get("/rest/music"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(music)))

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
     * Test method for [MusicController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val music = PageUtils.getPage(data = MusicUtils.getMusicList())
        val nameFilter = TestConstants.NAME_FILTER
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(music)

        val requestBuilder = get("/rest/music")
            .queryParam("name", nameFilter.name)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(music)))

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
     * Test method for [MusicController.get].
     */
    @Test
    fun get() {
        val music = MusicUtils.getMusic(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(music)

        mockMvc.perform(get("/rest/music/${music.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(music)))

        verify(facade).get(uuid = music.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MusicController.add].
     */
    @Test
    fun add() {
        val request = MusicUtils.newRequest()
        val music = MusicUtils.getMusic(index = 1)
        whenever(facade.add(request = any())).thenReturn(music)

        val requestBuilder = post("/rest/music")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(music)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MusicController.update].
     */
    @Test
    fun update() {
        val request = MusicUtils.newRequest()
        val music = MusicUtils.getMusic(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(music)

        val requestBuilder = put("/rest/music/${music.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(music)))

        verify(facade).update(uuid = music.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MusicController.remove].
     */
    @Test
    fun remove() {
        val music = MusicUtils.getMusic(index = 1)

        val requestBuilder = delete("/rest/music/${music.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = music.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MusicController.duplicate].
     */
    @Test
    fun duplicate() {
        val music = MusicUtils.getMusic(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(music)

        val requestBuilder = post("/rest/music/${music.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(music)))

        verify(facade).duplicate(uuid = music.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MusicController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = MusicUtils.getStatistics()
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/music/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
    