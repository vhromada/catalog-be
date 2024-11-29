package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.service.RegisterService
import com.github.vhromada.catalog.utils.RegisterUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
     * Instance of [RegisterService]
     */
    @MockitoBean
    private lateinit var service: RegisterService

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
        val register = RegisterUtils.getRegister(index = 1)
        val response = register.values.map { it.code }
        whenever(service.get(type = any())).thenReturn(register)

        mockMvc.perform(get("/rest/registers/formats/programs"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(service).get(RegisterType.PROGRAM_FORMATS)
        verifyNoMoreInteractions(service)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [RegisterController.getBookItemFormats].
     */
    @Test
    fun getBookItemFormats() {
        val register = RegisterUtils.getRegister(index = 1)
        val response = register.values.map { it.code }
        whenever(service.get(type = any())).thenReturn(register)

        mockMvc.perform(get("/rest/registers/formats/book-items"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(service).get(RegisterType.BOOK_ITEM_FORMATS)
        verifyNoMoreInteractions(service)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [RegisterController.getLanguages].
     */
    @Test
    fun getLanguages() {
        val register = RegisterUtils.getRegister(index = 1)
        val response = register.values.map { it.code }
        whenever(service.get(type = any())).thenReturn(register)

        mockMvc.perform(get("/rest/registers/languages"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(service).get(RegisterType.LANGUAGES)
        verifyNoMoreInteractions(service)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [RegisterController.getSubtitles].
     */
    @Test
    fun getSubtitles() {
        val register = RegisterUtils.getRegister(index = 1)
        val response = register.values.map { it.code }
        whenever(service.get(type = any())).thenReturn(register)

        mockMvc.perform(get("/rest/registers/subtitles"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))

        verify(service).get(RegisterType.SUBTITLES)
        verifyNoMoreInteractions(service)
        verifyNoInteractions(issueMapper)
    }

}
