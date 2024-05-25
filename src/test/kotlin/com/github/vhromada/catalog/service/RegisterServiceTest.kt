package com.github.vhromada.catalog.service

import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.repository.RegisterRepository
import com.github.vhromada.catalog.service.impl.RegisterServiceImpl
import com.github.vhromada.catalog.utils.RegisterUtils
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
import org.springframework.http.HttpStatus
import java.util.Optional

/**
 * A class represents test for class [RegisterService].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class RegisterServiceTest {

    /**
     * Instance of [RegisterRepository]
     */
    @Mock
    private lateinit var repository: RegisterRepository

    /**
     * Instance of [RegisterService]
     */
    private lateinit var service: RegisterService

    /**
     * Initializes service.
     */
    @BeforeEach
    fun setUp() {
        service = RegisterServiceImpl(repository = repository)
    }

    /**
     * Test method for [RegisterService.getAll].
     */
    @Test
    fun getAll() {
        val registers = listOf(RegisterUtils.getRegister(index = 1), RegisterUtils.getRegister(index = 2))
        whenever(repository.findAll()).thenReturn(registers)

        val result = service.getAll()

        assertThat(result).isEqualTo(registers)
        verify(repository).findAll()
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [RegisterService.get] with existing register.
     */
    @Test
    fun getExisting() {
        val register = RegisterUtils.getRegister(index = 1)
        whenever(repository.findByNumber(number = any())).thenReturn(Optional.of(register))

        val result = service.get(type = RegisterType.SUBTITLES)

        assertThat(result).isEqualTo(register)
        verify(repository).findByNumber(number = RegisterType.SUBTITLES.number)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [RegisterService.get] with not existing register.
     */
    @Test
    fun getNotExisting() {
        whenever(repository.findByNumber(number = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.get(type = RegisterType.SUBTITLES) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_NOT_EXIST")
            .hasMessageContaining("Register doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByNumber(number = RegisterType.SUBTITLES.number)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [RegisterService.checkValue] with existing data.
     */
    @Test
    fun checkValueExisting() {
        val register = RegisterUtils.getRegister(index = 1)
        val value = register.values.first()
        whenever(repository.findByNumber(number = any())).thenReturn(Optional.of(register))

        service.checkValue(type = RegisterType.SUBTITLES, code = value.code)

        verify(repository).findByNumber(number = RegisterType.SUBTITLES.number)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [RegisterService.checkValue] with not existing register.
     */
    @Test
    fun checkValueNotExistingRegister() {
        whenever(repository.findByNumber(number = any())).thenReturn(Optional.empty())

        assertThatThrownBy { service.checkValue(type = RegisterType.SUBTITLES, code = RegisterUtils.getRegister(index = 1).values.first().code) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_NOT_EXIST")
            .hasMessageContaining("Register doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByNumber(number = RegisterType.SUBTITLES.number)
        verifyNoMoreInteractions(repository)
    }

    /**
     * Test method for [RegisterService.get] with not existing register's value.
     */
    @Test
    fun getNotExistingValue() {
        val register = RegisterUtils.getRegister(index = 1)
        whenever(repository.findByNumber(number = any())).thenReturn(Optional.of(register))

        assertThatThrownBy { service.checkValue(type = RegisterType.SUBTITLES, code = TestConstants.UUID) }
            .isInstanceOf(InputException::class.java)
            .hasMessageContaining("REGISTER_VALUE_NOT_EXIST")
            .hasMessageContaining("Register's value doesn't exist.")
            .hasFieldOrPropertyWithValue("httpStatus", HttpStatus.NOT_FOUND)

        verify(repository).findByNumber(number = RegisterType.SUBTITLES.number)
        verifyNoMoreInteractions(repository)
    }

}
