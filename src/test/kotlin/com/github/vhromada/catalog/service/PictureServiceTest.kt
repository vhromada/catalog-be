package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.domain.Picture
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.PictureRepository
import com.github.vhromada.catalog.service.impl.PictureServiceImpl
import com.github.vhromada.catalog.utils.PictureUtils
import com.github.vhromada.catalog.utils.TestConstants
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [PictureService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class PictureServiceTest {

    /**
     * Instance of [PictureRepository]
     */
    @Mock
    private lateinit var repository: PictureRepository

    /**
     * Instance of [PictureService]
     */
    private lateinit var service: PictureService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = PictureServiceImpl(repository = repository)
    }

    /**
     * Test method for [PictureService.search].
     */
    @Test
    fun search() {
        val pageable = Pageable.ofSize(1)
        val page = PageImpl(listOf(PictureUtils.getDomainPicture(index = 1), PictureUtils.getDomainPicture(index = 2)))
        whenever(repository.findAll(any<Pageable>())).thenReturn(page)

        val result = service.search(pageable = pageable)

        assertThat(result).isEqualTo(page)
        verify(repository).findAll(pageable)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [PictureService.getById] with existing picture.
     */
    @Test
    fun getByIdExisting() {
        val picture = PictureUtils.getDomainPicture(index = 1)
        whenever(repository.findById(any())).thenReturn(Optional.of(picture))

        val result = service.getById(id = picture.id!!)

        assertThat(result).isEqualTo(picture)
        verify(repository).findById(picture.id!!)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [PictureService.getById] with not existing picture.
     */
    @Test
    fun getByIdNotExisting() {
        whenever(repository.findById(any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getById(id = Int.MAX_VALUE) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findById(Int.MAX_VALUE)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [PictureService.getByUuid] with existing picture.
     */
    @Test
    fun getByUuidExisting() {
        val picture = PictureUtils.getDomainPicture(index = 1)
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.of(picture))

        val result = service.getByUuid(uuid = picture.uuid)

        assertThat(result).isEqualTo(picture)
        verify(repository).findByUuid(uuid = picture.uuid)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [PictureService.getByUuid] with not existing picture.
     */
    @Test
    fun getByUuidNotExisting() {
        whenever(repository.findByUuid(uuid = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.getByUuid(uuid = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("PICTURE_NOT_EXIST")
            .hasMessageContaining("Picture doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByUuid(uuid = TestConstants.UUID)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [PictureService.store].
     */
    @Test
    fun update() {
        val picture = PictureUtils.getDomainPicture(index = 1)
        whenever(repository.save(anyDomain())).thenAnswer { it.arguments[0] }

        val result = service.store(picture = picture)

        assertThat(result).isSameAs(picture)
        verify(repository).save(picture)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [PictureService.remove].
     */
    @Test
    fun remove() {
        val picture = PictureUtils.getDomainPicture(index = 1)

        service.remove(picture = picture)

        verify(repository).delete(picture)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Returns any mock for domain picture.
     *
     * @return any mock for domain picture
     */
    private fun anyDomain(): Picture {
        return any()
    }

}
