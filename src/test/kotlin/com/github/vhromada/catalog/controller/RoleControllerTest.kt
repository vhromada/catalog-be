package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.facade.RoleFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.RoleUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * A class represents test for class [RoleController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(RoleController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class RoleControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [RoleFacade]
     */
    @MockBean
    private lateinit var facade: RoleFacade

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
     * Test method for [RoleController.getAll].
     */
    @Test
    fun getAll() {
        val roles = RoleUtils.getRoles().map { it.name }
        whenever(facade.getAll()).thenReturn(roles)

        mockMvc.perform(get("/rest/roles"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(roles)))

        verify(facade).getAll()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
