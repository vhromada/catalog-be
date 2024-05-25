package com.github.vhromada.catalog.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity
import com.github.vhromada.catalog.common.result.Status
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangePictureRequest
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.PictureFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.PageUtils
import com.github.vhromada.catalog.utils.PictureUtils
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
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * A class represents test for class [PictureController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(PictureController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class PictureControllerTest {

    /**
     * Instance of [MockMvc]
     */
    @Autowired
    private lateinit var mockMvc: MockMvc

    /**
     * Instance of [PictureFacade]
     */
    @MockBean
    private lateinit var facade: PictureFacade

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
     * Test method for [PictureController.search].
     */
    @Test
    fun search() {
        val pictures = PageUtils.getPage(data = PictureUtils.getPictures().map { it.uuid })
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.search(filter = any())).thenReturn(pictures)

        mockMvc.perform(get("/rest/pictures"))
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(pictures)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isNull()
        assertThat(filter.limit).isNull()
    }

    /**
     * Test method for [PictureController.search] with filter.
     */
    @Test
    fun searchFilter() {
        val pictures = PageUtils.getPage(data = PictureUtils.getPictures().map { it.uuid })
        val filterArgumentCaptor = argumentCaptor<PagingFilter>()
        whenever(facade.search(filter = any())).thenReturn(pictures)

        val requestBuilder = get("/rest/pictures")
            .queryParam("page", PageUtils.PAGE.toString())
            .queryParam("limit", PageUtils.LIMIT.toString())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(pictures)))

        verify(facade).search(filter = filterArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val filter = filterArgumentCaptor.lastValue
        assertThat(filter).isNotNull
        assertThat(filter.page).isEqualTo(PageUtils.PAGE)
        assertThat(filter.limit).isEqualTo(PageUtils.LIMIT)
    }

    /**
     * Test method for [PictureController.get].
     */
    @Test
    fun get() {
        val picture = PictureUtils.getPicture(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(picture)

        mockMvc.perform(get("/rest/pictures/${picture.uuid}"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"picture.jpg\""))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/jpg"))
            .andExpect(content().contentType("image/jpg"))
            .andExpect(content().bytes(picture.content))

        verify(facade).get(uuid = picture.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

    /**
     * Test method for [PictureController.add].
     */
    @Test
    fun add() {
        val content = PictureUtils.newRequest().content!!
        val picture = PictureUtils.getPicture(index = 1)
        val changePictureRequestArgumentCaptor = argumentCaptor<ChangePictureRequest>()
        whenever(facade.add(request = any())).thenReturn(picture)

        val requestBuilder = multipart(HttpMethod.POST, "/rest/pictures")
            .file("file", content)
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isCreated())

        verify(facade).add(request = changePictureRequestArgumentCaptor.capture())
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
        val request = changePictureRequestArgumentCaptor.lastValue
        assertThat(request).isNotNull
        assertThat(request.content).isEqualTo(content)
    }

    /**
     * Test method for [PictureController.add] with no file.
     */
    @Test
    fun addNoFile() {
        val requestBuilder = multipart(HttpMethod.POST, "/rest/pictures")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect { assertThat(it.resolvedException?.message).contains("Required part 'file' is not present.") }

        verifyNoInteractions(facade, issueMapper)
    }

    /**
     * Test method for [PictureController.add] with empty file.
     */
    @Test
    fun addEmptyFile() {
        val resultArgumentCaptor = argumentCaptor<Result<*>>()

        val requestBuilder = multipart(HttpMethod.POST, "/rest/pictures")
            .file("file", ByteArray(0))
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isUnprocessableEntity())
            .andExpect { assertThat(it.resolvedException).isInstanceOf(InputException::class.java) }
            .andExpect { assertThat(it.resolvedException?.message).contains("File mustn't be empty.") }

        verify(issueMapper).map(resultArgumentCaptor.capture())
        verifyNoMoreInteractions(issueMapper)
        verifyNoInteractions(facade)
        val result = resultArgumentCaptor.lastValue
        assertThat(result).isNotNull
        assertThat(result.data).isNull()
        assertThat(result.status).isEqualTo(Status.ERROR)
        assertThat(result.events()).hasSize(1)
        val event = result.events().first()
        assertThat(event.severity).isEqualTo(Severity.ERROR)
        assertThat(event.key).isEqualTo("FILE_EMPTY")
        assertThat(event.message).isEqualTo("File mustn't be empty.")
    }

    /**
     * Test method for [PictureController.remove].
     */
    @Test
    fun remove() {
        val picture = PictureUtils.getPicture(index = 1)

        val requestBuilder = delete("/rest/pictures/${picture.uuid}")
            .with(csrf())
        mockMvc.perform(requestBuilder)
            .andExpect(status().isNoContent())

        verify(facade).remove(uuid = picture.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
