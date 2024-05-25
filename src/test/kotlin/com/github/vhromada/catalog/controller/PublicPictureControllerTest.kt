package com.github.vhromada.catalog.controller

import com.github.vhromada.catalog.facade.PictureFacade
import com.github.vhromada.catalog.mapper.IssueMapper
import com.github.vhromada.catalog.utils.PictureUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * A class represents test for class [PublicPictureController].
 *
 * @author Vladimir Hromada
 */
@WebMvcTest(PublicPictureController::class)
@WithMockUser(value = TestConstants.USERNAME, password = TestConstants.PASSWORD)
class PublicPictureControllerTest {

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
     * Test method for [PublicPictureController.get].
     */
    @Test
    fun get() {
        val picture = PictureUtils.getPicture(index = 1)
        whenever(facade.get(uuid = any())).thenReturn(picture)

        mockMvc.perform(get("/rest/public/pictures/${picture.uuid}"))
            .andExpect(status().isOk())
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"picture.jpg\""))
            .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "image/jpg"))
            .andExpect(content().contentType("image/jpg"))
            .andExpect(content().bytes(picture.content))

        verify(facade).get(uuid = picture.uuid)
        verifyNoMoreInteractions(facade)
        verifyNoInteractions(issueMapper)
    }

}
