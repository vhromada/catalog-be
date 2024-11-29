package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.facade.CheatFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.CheatUtils
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
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
 * A class represents test for class [CheatController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(CheatController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class CheatControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [CheatFacade]
     */
    @MockitoBean
    private lateinit var facade: CheatFacade

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
     * Game
     */
    private val game = GameUtils.getGame(index = 1)

    /**
     * Test method for [CheatController.find].
     */
    @Test
    fun find() {
        val cheat = CheatUtils.getCheat(index = 1)
        whenever(facade.find(game = any())).thenReturn(cheat)

        mockMvc.perform(get("/rest/games/${game.uuid}/cheats"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(cheat)))

        verify(facade).find(game = game.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [CheatController.get].
     */
    @Test
    fun get() {
        val cheat = CheatUtils.getCheat(index = 1)
        whenever(facade.get(game = any(), uuid = any())).thenReturn(cheat)

        mockMvc.perform(get("/rest/games/${game.uuid}/cheats/${cheat.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(cheat)))

        verify(facade).get(game = game.uuid, uuid = cheat.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [CheatController.add].
     */
    @Test
    fun add() {
        val request = CheatUtils.newRequest()
        val cheat = CheatUtils.getCheat(index = 1)
        whenever(facade.add(game = any(), request = any())).thenReturn(cheat)

        val requestBuilder = post("/rest/games/${game.uuid}/cheats")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(cheat)))

        verify(facade).add(game = game.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [CheatController.update].
     */
    @Test
    fun update() {
        val request = CheatUtils.newRequest()
        val cheat = CheatUtils.getCheat(index = 1)
        whenever(facade.update(game = any(), uuid = any(), request = any())).thenReturn(cheat)

        val requestBuilder = put("/rest/games/${game.uuid}/cheats/${cheat.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(cheat)))

        verify(facade).update(game = game.uuid, uuid = cheat.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [CheatController.remove].
     */
    @Test
    fun remove() {
        val cheat = CheatUtils.getCheat(index = 1)

        val requestBuilder = delete("/rest/games/${game.uuid}/cheats/${cheat.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(game = game.uuid, uuid = cheat.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
