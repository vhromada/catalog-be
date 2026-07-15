package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.facade.RegisterFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import tools.jackson.databind.ObjectMapper

/**
 * A class represents test for class [RegisterController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(RegisterController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class RegisterControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [RegisterFacade]
     */
    @MockitoBean
    private lateinit var facade: RegisterFacade

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
     * Test method for [RegisterController.getProgramFormats].
     */
    @Test
    fun getProgramFormats() {
        val response = listOf("ISO", "BINARY", "STEAM", "BATTLE_NET")
        whenever(facade.getProgramFormats()).thenReturn(response)

        mockMvc.perform(get("/rest/registers/formats/programs"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(facade).getProgramFormats()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [RegisterController.getBookItemFormats].
     */
    @Test
    fun getBookItemFormats() {
        val response = listOf("PAPER", "PDF", "DOC", "TXT")
        whenever(facade.getBookItemFormats()).thenReturn(response)

        mockMvc.perform(get("/rest/registers/formats/book-items"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(facade).getBookItemFormats()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [RegisterController.getLanguages].
     */
    @Test
    fun getLanguages() {
        val response = listOf("CZ", "EN", "FR", "JP", "SK")
        whenever(facade.getLanguages()).thenReturn(response)

        mockMvc.perform(get("/rest/registers/languages"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(facade).getLanguages()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [RegisterController.getSubtitles].
     */
    @Test
    fun getSubtitles() {
        val response = listOf("CZ", "EN")
        whenever(facade.getSubtitles()).thenReturn(response)

        mockMvc.perform(get("/rest/registers/subtitles"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(facade).getSubtitles()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
