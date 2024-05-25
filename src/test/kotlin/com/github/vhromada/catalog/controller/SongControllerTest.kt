package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.facade.SongFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.MusicUtils
import com.github.vhromada.catalog.utils.PageUtils
import com.github.vhromada.catalog.utils.SongUtils
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
 * A class represents test for class [SongController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(SongController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class SongControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [SongFacade]
     */
    @MockBean
    private lateinit var facade: SongFacade

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
     * Music
     */
    private val music = MusicUtils.getMusic(index = 1)

    /**
     * Test method for [SongController.search].
     */
    @Test
    fun search() {
        val songs = PageUtils.getPage(data = SongUtils.getSongs(music = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(music = any(), filter = any())).thenReturn(songs)

        mockMvc.perform(get("/rest/music/${music.uuid}/songs"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(songs)))

        verify(facade).findAll(music = eq(music.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [SongController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val songs = PageUtils.getPage(data = SongUtils.getSongs(music = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(music = any(), filter = any())).thenReturn(songs)

        val requestBuilder = get("/rest/music/${music.uuid}/songs")
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(songs)))

        verify(facade).findAll(music = eq(music.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [SongController.get].
     */
    @Test
    fun get() {
        val song = SongUtils.getSong(index = 1)
        whenever(facade.get(music = any(), uuid = any())).thenReturn(song)

        mockMvc.perform(get("/rest/music/${music.uuid}/songs/${song.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(song)))

        verify(facade).get(music = music.uuid, uuid = song.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SongController.add].
     */
    @Test
    fun add() {
        val request = SongUtils.newRequest()
        val song = SongUtils.getSong(index = 1)
        whenever(facade.add(music = any(), request = any())).thenReturn(song)

        val requestBuilder = post("/rest/music/${music.uuid}/songs")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(song)))

        verify(facade).add(music = music.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SongController.update].
     */
    @Test
    fun update() {
        val request = SongUtils.newRequest()
        val song = SongUtils.getSong(index = 1)
        whenever(facade.update(music = any(), uuid = any(), request = any())).thenReturn(song)

        val requestBuilder = put("/rest/music/${music.uuid}/songs/${song.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(song)))

        verify(facade).update(music = music.uuid, uuid = song.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SongController.remove].
     */
    @Test
    fun remove() {
        val song = SongUtils.getSong(index = 1)

        val requestBuilder = delete("/rest/music/${music.uuid}/songs/${song.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(music = music.uuid, uuid = song.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [SongController.duplicate].
     */
    @Test
    fun duplicate() {
        val song = SongUtils.getSong(index = 1)
        whenever(facade.duplicate(music = any(), uuid = any())).thenReturn(song)

        val requestBuilder = post("/rest/music/${music.uuid}/songs/${song.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(song)))

        verify(facade).duplicate(music = music.uuid, uuid = song.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
