package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.facade.GameFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.GameUtils
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
 * A class represents test for class [GameController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(GameController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class GameControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [GameFacade]
     */
    @MockBean
    private lateinit var facade: GameFacade

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
     * Test method for [GameController.search].
     */
    @Test
    fun search() {
        val games = PageUtils.getPage(data = GameUtils.getGames())
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(games)

        mockMvc.perform(get("/rest/games"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(games)))

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
     * Test method for [GameController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val games = PageUtils.getPage(data = GameUtils.getGames())
        val nameFilter = TestConstants.NAME_FILTER
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(games)

        val requestBuilder = get("/rest/games")
            .queryParam("name", nameFilter.name)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(games)))

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
     * Test method for [GameController.get].
     */
    @Test
    fun get() {
        val game = GameUtils.getGame(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(game)

        mockMvc.perform(get("/rest/games/${game.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(game)))

        verify(facade).get(uuid = game.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GameController.add].
     */
    @Test
    fun add() {
        val request = GameUtils.newRequest()
        val game = GameUtils.getGame(index = 1)
        whenever(facade.add(request = any())).thenReturn(game)

        val requestBuilder = post("/rest/games")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(game)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GameController.update].
     */
    @Test
    fun update() {
        val request = GameUtils.newRequest()
        val game = GameUtils.getGame(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(game)

        val requestBuilder = put("/rest/games/${game.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(game)))

        verify(facade).update(uuid = game.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GameController.remove].
     */
    @Test
    fun remove() {
        val game = GameUtils.getGame(index = 1)

        val requestBuilder = delete("/rest/games/${game.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = game.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GameController.duplicate].
     */
    @Test
    fun duplicate() {
        val game = GameUtils.getGame(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(game)

        val requestBuilder = post("/rest/games/${game.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(game)))

        verify(facade).duplicate(uuid = game.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GameController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = GameUtils.getStatistics()
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/games/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
