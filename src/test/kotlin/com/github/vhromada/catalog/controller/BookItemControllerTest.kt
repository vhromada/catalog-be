package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.facade.BookItemFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.BookItemUtils
import com.github.vhromada.catalog.utils.BookUtils
import com.github.vhromada.catalog.utils.PageUtils
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
 * A class represents test for class [BookItemController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(BookItemController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class BookItemControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [BookItemFacade]
     */
    @MockitoBean
    private lateinit var facade: BookItemFacade

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
     * Book
     */
    private val book = BookUtils.getBook(index = 1)

    /**
     * Test method for [BookItemController.search].
     */
    @Test
    fun search() {
        val bookItems = PageUtils.getPage(data = BookItemUtils.getBookItems(book = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(book = any(), filter = any())).thenReturn(bookItems)

        mockMvc.perform(get("/rest/books/${book.uuid}/items"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(bookItems)))

        verify(facade).findAll(book = eq(book.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [BookItemController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val bookItems = PageUtils.getPage(data = BookItemUtils.getBookItems(book = 1))
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.findAll(book = any(), filter = any())).thenReturn(bookItems)

        val requestBuilder = get("/rest/books/${book.uuid}/items")
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(bookItems)))

        verify(facade).findAll(book = eq(book.uuid), filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [BookItemController.get].
     */
    @Test
    fun get() {
        val bookItem = BookItemUtils.getBookItem(index = 1)
        whenever(facade.get(book = any(), uuid = any())).thenReturn(bookItem)

        mockMvc.perform(get("/rest/books/${book.uuid}/items/${bookItem.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(bookItem)))

        verify(facade).get(book = book.uuid, uuid = bookItem.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookItemController.add].
     */
    @Test
    fun add() {
        val request = BookItemUtils.newRequest()
        val bookItem = BookItemUtils.getBookItem(index = 1)
        whenever(facade.add(book = any(), request = any())).thenReturn(bookItem)

        val requestBuilder = post("/rest/books/${book.uuid}/items")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(bookItem)))

        verify(facade).add(book = book.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookItemController.update].
     */
    @Test
    fun update() {
        val request = BookItemUtils.newRequest()
        val bookItem = BookItemUtils.getBookItem(index = 1)
        whenever(facade.update(book = any(), uuid = any(), request = any())).thenReturn(bookItem)

        val requestBuilder = put("/rest/books/${book.uuid}/items/${bookItem.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(bookItem)))

        verify(facade).update(book = book.uuid, uuid = bookItem.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookItemController.remove].
     */
    @Test
    fun remove() {
        val bookItem = BookItemUtils.getBookItem(index = 1)

        val requestBuilder = delete("/rest/books/${book.uuid}/items/${bookItem.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(book = book.uuid, uuid = bookItem.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [BookItemController.duplicate].
     */
    @Test
    fun duplicate() {
        val bookItem = BookItemUtils.getBookItem(index = 1)
        whenever(facade.duplicate(book = any(), uuid = any())).thenReturn(bookItem)

        val requestBuilder = post("/rest/books/${book.uuid}/items/${bookItem.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(bookItem)))

        verify(facade).duplicate(book = book.uuid, uuid = bookItem.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
