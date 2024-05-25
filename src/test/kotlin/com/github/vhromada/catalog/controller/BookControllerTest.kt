package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.facade.BookFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.BookUtils
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
 * A class represents test for class [BookController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(BookController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class BookControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [BookFacade]
     */
    @MockBean
    private lateinit var facade: BookFacade

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
     * Test method for [BookController.search].
     */
    @Test
    fun search() {
        val books = PageUtils.getPage(data = BookUtils.getBooks())
        val filterArgumentCaptor = argumentCaptor<MultipleNameFilter>()
        whenever(facade.search(filter = any())).thenReturn(books)

        mockMvc.perform(get("/rest/books"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(books)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.czechName).isNull()
        assertThat(filter.originalName).isNull()
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [BookController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val books = PageUtils.getPage(data = BookUtils.getBooks())
        val namesFilter = TestConstants.MULTIPLE_NAMES_FILTER
        val filterArgumentCaptor = argumentCaptor<MultipleNameFilter>()
        whenever(facade.search(filter = any())).thenReturn(books)

        val requestBuilder = get("/rest/books")
            .queryParam("czechName", namesFilter.czechName)
            .queryParam("originalName", namesFilter.originalName)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(books)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.czechName).isEqualTo(namesFilter.czechName)
        assertThat(filter.originalName).isEqualTo(namesFilter.originalName)
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [BookController.get].
     */
    @Test
    fun get() {
        val book = BookUtils.getBook(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(book)

        mockMvc.perform(get("/rest/books/${book.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(book)))

        verify(facade).get(uuid = book.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookController.add].
     */
    @Test
    fun add() {
        val request = BookUtils.newRequest()
        val book = BookUtils.getBook(index = 1)
        whenever(facade.add(request = any())).thenReturn(book)

        val requestBuilder = post("/rest/books")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(book)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookController.update].
     */
    @Test
    fun update() {
        val request = BookUtils.newRequest()
        val book = BookUtils.getBook(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(book)

        val requestBuilder = put("/rest/books/${book.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(book)))

        verify(facade).update(uuid = book.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookController.remove].
     */
    @Test
    fun remove() {
        val book = BookUtils.getBook(index = 1)

        val requestBuilder = delete("/rest/books/${book.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = book.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookController.duplicate].
     */
    @Test
    fun duplicate() {
        val book = BookUtils.getBook(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(book)

        val requestBuilder = post("/rest/books/${book.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(book)))

        verify(facade).duplicate(uuid = book.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = BookUtils.getStatistics()
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/books/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
