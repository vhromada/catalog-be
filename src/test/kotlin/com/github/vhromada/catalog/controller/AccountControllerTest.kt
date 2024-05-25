package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.AccountStatistics
import com.github.vhromada.catalog.entity.filter.AccountFilter
import com.github.vhromada.catalog.facade.AccountFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.AccountUtils
import com.github.vhromada.catalog.utils.PageUtils
import com.github.vhromada.catalog.utils.RoleUtils
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * A class represents test for class [AccountController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(AccountController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class AccountControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [AccountFacade]
     */
    @MockBean
    private lateinit var facade: AccountFacade

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
     * Test method for [AccountController.search].
     */
    @Test
    fun search() {
        val accounts = PageUtils.getPage(data = AccountUtils.getAccounts())
        val filterArgumentCaptor = argumentCaptor<AccountFilter>()
        whenever(facade.search(filter = any())).thenReturn(accounts)

        mockMvc.perform(get("/rest/accounts"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(accounts)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.uuid).isNull()
        assertThat(filter.username).isNull()
        assertThat(filter.usernameOperation).isNull()
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [AccountController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val accounts = PageUtils.getPage(data = AccountUtils.getAccounts())
        val accountFilter = AccountUtils.newFilter()
        val filterArgumentCaptor = argumentCaptor<AccountFilter>()
        whenever(facade.search(filter = any())).thenReturn(accounts)

        val requestBuilder = get("/rest/accounts")
            .queryParam("uuid", accountFilter.uuid)
            .queryParam("username", accountFilter.username)
            .queryParam("usernameOperation", accountFilter.usernameOperation.toString())
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(accounts)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.uuid).isEqualTo(accountFilter.uuid)
        assertThat(filter.username).isEqualTo(accountFilter.username)
        assertThat(filter.usernameOperation).isEqualTo(accountFilter.usernameOperation)
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [AccountController.get].
     */
    @Test
    fun get() {
        val account = AccountUtils.getAccount(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(account)

        mockMvc.perform(get("/rest/accounts/${account.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(account)))

        verify(facade).get(uuid = account.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AccountController.updateCredentials].
     */
    @Test
    fun updateCredentials() {
        val account = AccountUtils.getAccount(index = 1)
        val credentials = AccountUtils.getAdminCredentials()
        whenever(facade.updateCredentials(uuid = any(), credentials = any())).thenReturn(account)

        val requestBuilder = put("/rest/accounts/${account.uuid}/credentials")
            .content(objectMapper.writeValueAsString(credentials))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(account)))

        verify(facade).updateCredentials(uuid = account.uuid, credentials = credentials)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AccountController.updateRoles].
     */
    @Test
    fun updateRoles() {
        val account = AccountUtils.getAccount(index = 1)
        val request = RoleUtils.newRequest()
        whenever(facade.updateRoles(uuid = any(), request = any())).thenReturn(account)

        val requestBuilder = put("/rest/accounts/${account.uuid}/roles")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(account)))

        verify(facade).updateRoles(uuid = account.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AccountController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = AccountStatistics(count = AccountUtils.ACCOUNTS_COUNT)
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/accounts/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AccountController.addCredentials].
     */
    @Test
    fun addCredentials() {
        val account = AccountUtils.getAccount(index = 1)
        val credentials = AccountUtils.getAdminCredentials()
        whenever(facade.addCredentials(credentials = any())).thenReturn(account)

        val requestBuilder = post("/rest/accounts/credentials")
            .content(objectMapper.writeValueAsString(credentials))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(account)))

        verify(facade).addCredentials(credentials = credentials)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AccountController.checkCredentials].
     */
    @Test
    fun checkCredentials() {
        val account = AccountUtils.getAccount(index = 1)
        val credentials = AccountUtils.getAdminCredentials()
        whenever(facade.checkCredentials(credentials = any())).thenReturn(account)

        val requestBuilder = post("/rest/accounts/credentials/check")
            .content(objectMapper.writeValueAsString(credentials))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(account)))

        verify(facade).checkCredentials(credentials = credentials)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
