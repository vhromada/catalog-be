package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.JokeStatistics
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.facade.JokeFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.JokeUtils
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
 * A class represents test for class [JokeController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(JokeController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class JokeControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [JokeFacade]
     */
    @MockitoBean
    private lateinit var facade: JokeFacade

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
     * Test method for [JokeController.search].
     */
    @Test
    fun search() {
        val jokes = PageUtils.getPage(data = JokeUtils.getJokes())
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.search(filter = any())).thenReturn(jokes)

        mockMvc.perform(get("/rest/jokes"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(jokes)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [JokeController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val jokes = PageUtils.getPage(data = JokeUtils.getJokes())
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.search(filter = any())).thenReturn(jokes)

        val requestBuilder = get("/rest/jokes")
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(jokes)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [JokeController.get].
     */
    @Test
    fun get() {
        val joke = JokeUtils.getJoke(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(joke)

        mockMvc.perform(get("/rest/jokes/${joke.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(joke)))

        verify(facade).get(uuid = joke.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [JokeController.add].
     */
    @Test
    fun add() {
        val request = JokeUtils.newRequest()
        val joke = JokeUtils.getJoke(index = 1)
        whenever(facade.add(request = any())).thenReturn(joke)

        val requestBuilder = post("/rest/jokes")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(joke)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [JokeController.update].
     */
    @Test
    fun update() {
        val request = JokeUtils.newRequest()
        val joke = JokeUtils.getJoke(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(joke)

        val requestBuilder = put("/rest/jokes/${joke.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(joke)))

        verify(facade).update(uuid = joke.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [JokeController.remove].
     */
    @Test
    fun remove() {
        val joke = JokeUtils.getJoke(index = 1)

        val requestBuilder = delete("/rest/jokes/${joke.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = joke.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [JokeController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = JokeStatistics(count = JokeUtils.JOKES_COUNT)
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/jokes/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
