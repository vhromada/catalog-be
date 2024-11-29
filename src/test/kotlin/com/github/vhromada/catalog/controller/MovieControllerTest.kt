package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.facade.MovieFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.MovieUtils
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
 * A class represents test for class [MovieController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(MovieController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class MovieControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [MovieFacade]
     */
    @MockitoBean
    private lateinit var facade: MovieFacade

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
     * Test method for [MovieController.search].
     */
    @Test
    fun search() {
        val movies = PageUtils.getPage(data = MovieUtils.getMovies())
        val filterArgumentCaptor = argumentCaptor<MultipleNameFilter>()
        whenever(facade.search(filter = any())).thenReturn(movies)

        mockMvc.perform(get("/rest/movies"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(movies)))

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
     * Test method for [MovieController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val movies = PageUtils.getPage(data = MovieUtils.getMovies())
        val namesFilter = TestConstants.MULTIPLE_NAMES_FILTER
        val filterArgumentCaptor = argumentCaptor<MultipleNameFilter>()
        whenever(facade.search(filter = any())).thenReturn(movies)

        val requestBuilder = get("/rest/movies")
            .queryParam("czechName", namesFilter.czechName)
            .queryParam("originalName", namesFilter.originalName)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(movies)))

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
     * Test method for [MovieController.get].
     */
    @Test
    fun get() {
        val movie = MovieUtils.getMovie(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(movie)

        mockMvc.perform(get("/rest/movies/${movie.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(movie)))

        verify(facade).get(uuid = movie.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MovieController.add].
     */
    @Test
    fun add() {
        val request = MovieUtils.newRequest()
        val movie = MovieUtils.getMovie(index = 1)
        whenever(facade.add(request = any())).thenReturn(movie)

        val requestBuilder = post("/rest/movies")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(movie)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MovieController.update].
     */
    @Test
    fun update() {
        val request = MovieUtils.newRequest()
        val movie = MovieUtils.getMovie(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(movie)

        val requestBuilder = put("/rest/movies/${movie.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(movie)))

        verify(facade).update(uuid = movie.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MovieController.remove].
     */
    @Test
    fun remove() {
        val movie = MovieUtils.getMovie(index = 1)

        val requestBuilder = delete("/rest/movies/${movie.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = movie.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MovieController.duplicate].
     */
    @Test
    fun duplicate() {
        val movie = MovieUtils.getMovie(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(movie)

        val requestBuilder = post("/rest/movies/${movie.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(movie)))

        verify(facade).duplicate(uuid = movie.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [MovieController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = MovieUtils.getStatistics()
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/movies/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
