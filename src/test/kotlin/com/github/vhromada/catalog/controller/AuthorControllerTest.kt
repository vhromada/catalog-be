package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.AuthorStatistics
import com.github.vhromada.catalog.entity.filter.AuthorFilter
import com.github.vhromada.catalog.facade.AuthorFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.AuthorUtils
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
 * A class represents test for class [AuthorController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(AuthorController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class AuthorControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [AuthorFacade]
     */
    @MockBean
    private lateinit var facade: AuthorFacade

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
     * Test method for [AuthorController.search].
     */
    @Test
    fun search() {
        val authors = PageUtils.getPage(data = AuthorUtils.getAuthors())
        val filterArgumentCaptor = argumentCaptor<AuthorFilter>()
        whenever(facade.search(filter = any())).thenReturn(authors)

        mockMvc.perform(get("/rest/authors"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(authors)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.firstName).isNull()
        assertThat(filter.middleName).isNull()
        assertThat(filter.lastName).isNull()
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [AuthorController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val authors = PageUtils.getPage(data = AuthorUtils.getAuthors())
        val authorFilter = AuthorUtils.newFilter()
        val filterArgumentCaptor = argumentCaptor<AuthorFilter>()
        whenever(facade.search(filter = any())).thenReturn(authors)

        val requestBuilder = get("/rest/authors")
            .queryParam("firstName", authorFilter.firstName)
            .queryParam("middleName", authorFilter.middleName)
            .queryParam("lastName", authorFilter.lastName)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(authors)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.firstName).isEqualTo(authorFilter.firstName)
        assertThat(filter.middleName).isEqualTo(authorFilter.middleName)
        assertThat(filter.lastName).isEqualTo(authorFilter.lastName)
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [AuthorController.get].
     */
    @Test
    fun get() {
        val author = AuthorUtils.getAuthor(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(author)

        mockMvc.perform(get("/rest/authors/${author.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(author)))

        verify(facade).get(uuid = author.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AuthorController.add].
     */
    @Test
    fun add() {
        val request = AuthorUtils.newRequest()
        val author = AuthorUtils.getAuthor(index = 1)
        whenever(facade.add(request = any())).thenReturn(author)

        val requestBuilder = post("/rest/authors")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(author)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AuthorController.update].
     */
    @Test
    fun update() {
        val request = AuthorUtils.newRequest()
        val author = AuthorUtils.getAuthor(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(author)

        val requestBuilder = put("/rest/authors/${author.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(author)))

        verify(facade).update(uuid = author.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AuthorController.remove].
     */
    @Test
    fun remove() {
        val author = AuthorUtils.getAuthor(index = 1)

        val requestBuilder = delete("/rest/authors/${author.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = author.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AuthorController.duplicate].
     */
    @Test
    fun duplicate() {
        val author = AuthorUtils.getAuthor(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(author)

        val requestBuilder = post("/rest/authors/${author.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(author)))

        verify(facade).duplicate(uuid = author.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [AuthorController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = AuthorStatistics(count = AuthorUtils.AUTHORS_COUNT)
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/authors/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
