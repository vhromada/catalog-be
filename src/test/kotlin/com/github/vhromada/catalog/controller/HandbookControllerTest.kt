package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.facade.HandbookFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.GameUtils
import com.github.vhromada.catalog.utils.TestConstants
import com.github.vhromada.catalog.utils.HandbookUtils
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
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
import tools.jackson.databind.ObjectMapper

/**
 * A class represents test for class [HandbookController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(HandbookController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class HandbookControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [HandbookFacade]
     */
    @MockitoBean
    private lateinit var facade: HandbookFacade

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
     * Test method for [HandbookController.find].
     */
    @Test
    fun find() {
        val handbook = HandbookUtils.getHandbook(index = 1)
        whenever(facade.find(game = any())).thenReturn(handbook)

        mockMvc.perform(get("/rest/games/${game.uuid}/handbooks"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(handbook)))

        verify(facade).find(game = game.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [HandbookController.get].
     */
    @Test
    fun get() {
        val handbook = HandbookUtils.getHandbook(index = 1)
        whenever(facade.get(game = any(), uuid = any())).thenReturn(handbook)

        mockMvc.perform(get("/rest/games/${game.uuid}/handbooks/${handbook.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(handbook)))

        verify(facade).get(game = game.uuid, uuid = handbook.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [HandbookController.add].
     */
    @Test
    fun add() {
        val request = HandbookUtils.newRequest()
        val handbook = HandbookUtils.getHandbook(index = 1)
        whenever(facade.add(game = any(), request = any())).thenReturn(handbook)

        val requestBuilder = post("/rest/games/${game.uuid}/handbooks")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(handbook)))

        verify(facade).add(game = game.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [HandbookController.update].
     */
    @Test
    fun update() {
        val request = HandbookUtils.newRequest()
        val handbook = HandbookUtils.getHandbook(index = 1)
        whenever(facade.update(game = any(), uuid = any(), request = any())).thenReturn(handbook)

        val requestBuilder = put("/rest/games/${game.uuid}/handbooks/${handbook.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(handbook)))

        verify(facade).update(game = game.uuid, uuid = handbook.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [HandbookController.remove].
     */
    @Test
    fun remove() {
        val handbook = HandbookUtils.getHandbook(index = 1)

        val requestBuilder = delete("/rest/games/${game.uuid}/handbooks/${handbook.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(game = game.uuid, uuid = handbook.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
