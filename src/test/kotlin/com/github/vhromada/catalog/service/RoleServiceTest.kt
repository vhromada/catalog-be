package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.repository.RoleRepository
import com.github.vhromada.catalog.service.impl.RoleServiceImpl
import com.github.vhromada.catalog.utils.RoleUtils
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
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [RoleService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class RoleServiceTest {

    /**
     * Instance of [RoleRepository]
     */
    @Mock
    private lateinit var repository: RoleRepository

    /**
     * Instance of [RoleService]
     */
    private lateinit var service: RoleService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = RoleServiceImpl(repository = repository)
    }

    /**
     * Test method for [RoleService.getAll].
     */
    @Test
    fun getAll() {
        val role = RoleUtils.getDomainRole(index = 1)
        whenever(repository.findAll()).thenReturn(listOf(role))

        val result = service.getAll()

        assertThat(result).isEqualTo(listOf(role))
        verify(repository).findAll()
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [RoleService.get].
     */
    @Test
    fun get() {
        val role = RoleUtils.getDomainRole(index = 1)
        whenever(repository.findByName(name = any())).thenReturn(Optional.of(role))

        val result = service.get(name = role.name)

        assertThat(result).isEqualTo(role)
        verify(repository).findByName(name = role.name)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [RoleService.get] with not existing name.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByName(name = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(name = "name") }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("ROLE_NOT_EXIST")
            .hasMessageContaining("Role doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)
        verify(repository).findByName(name = "name")
        verifyNoMoreInteractions(repository)
    }

}
