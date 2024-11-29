package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.entity.GenreStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.facade.GenreFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.GenreUtils
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
 * A class represents test for class [GenreController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(GenreController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class GenreControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [GenreFacade]
     */
    @MockitoBean
    private lateinit var facade: GenreFacade

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
     * Test method for [GenreController.search].
     */
    @Test
    fun search() {
        val genres = PageUtils.getPage(data = GenreUtils.getGenres())
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(genres)

        mockMvc.perform(get("/rest/genres"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(genres)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.name).isNull()
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [GenreController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val genres = PageUtils.getPage(data = GenreUtils.getGenres())
        val nameFilter = TestConstants.NAME_FILTER
        val filterArgumentCaptor = argumentCaptor<NameFilter>()
        whenever(facade.search(filter = any())).thenReturn(genres)

        val requestBuilder = get("/rest/genres")
            .queryParam("name", nameFilter.name)
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(genres)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.name).isEqualTo(nameFilter.name)
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [GenreController.get].
     */
    @Test
    fun get() {
        val genre = GenreUtils.getGenre(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(genre)

        mockMvc.perform(get("/rest/genres/${genre.uuid}"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(genre)))

        verify(facade).get(uuid = genre.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GenreController.add].
     */
    @Test
    fun add() {
        val request = GenreUtils.newRequest()
        val genre = GenreUtils.getGenre(index = 1)
        whenever(facade.add(request = any())).thenReturn(genre)

        val requestBuilder = post("/rest/genres")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(genre)))

        verify(facade).add(request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GenreController.update].
     */
    @Test
    fun update() {
        val request = GenreUtils.newRequest()
        val genre = GenreUtils.getGenre(index = 1)
        whenever(facade.update(uuid = any(), request = any())).thenReturn(genre)

        val requestBuilder = put("/rest/genres/${genre.uuid}")
            .content(objectMapper.writeValueAsString(request))
            .contentType(MediaType.APPLICATION_JSON)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(genre)))

        verify(facade).update(uuid = genre.uuid, request = request)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GenreController.remove].
     */
    @Test
    fun remove() {
        val genre = GenreUtils.getGenre(index = 1)

        val requestBuilder = delete("/rest/genres/${genre.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = genre.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GenreController.duplicate].
     */
    @Test
    fun duplicate() {
        val genre = GenreUtils.getGenre(index = 1)
        whenever(facade.duplicate(uuid = any())).thenReturn(genre)

        val requestBuilder = post("/rest/genres/${genre.uuid}/duplicate")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())
            .andExpect(content().json(objectMapper.writeValueAsString(genre)))

        verify(facade).duplicate(uuid = genre.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [GenreController.getStatistics].
     */
    @Test
    fun getStatistics() {
        val statistics = GenreStatistics(count = GenreUtils.GENRES_COUNT)
        whenever(facade.getStatistics()).thenReturn(statistics)

        mockMvc.perform(get("/rest/genres/statistics"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(statistics)))

        verify(facade).getStatistics()
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
