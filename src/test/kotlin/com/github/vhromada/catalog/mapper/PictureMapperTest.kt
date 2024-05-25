package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.PictureMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.PictureUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

/**
 * A class represents test for class [PictureMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class PictureMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [PictureMapper]
     */
    private lateinit var mapper: PictureMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = PictureMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [PictureMapper.mapPicture].
     */
    @Test
    fun mapPicture() {
        val picture = PictureUtils.getDomainPicture(index = 1)

        val result = mapper.mapPicture(source = picture)

        PictureUtils.assertPictureDeepEquals(expected = picture, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [PictureMapper.mapPictures].
     */
    @Test
    fun mapPictures() {
        val picture = PictureUtils.getDomainPicture(index = 1)

        val result = mapper.mapPictures(source = listOf(picture))

        PictureUtils.assertPicturesDeepEquals(expected = listOf(picture), actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [PictureMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = PictureUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        PictureUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
