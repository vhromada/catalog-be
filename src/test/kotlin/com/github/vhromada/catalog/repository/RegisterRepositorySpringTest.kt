package com.github.vhromada.catalog.repository

import com.github.vhromada.catalog.TestConfiguration
import com.github.vhromada.catalog.utils.RegisterUtils
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.transaction.annotation.Transactional

/**
 * A class represents test for class [RegisterRepository].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfiguration::class])
@Transactional
@Rollback
class RegisterRepositorySpringTest {

    /**
     * Instance of [EntityManager]
     */
    @PersistenceContext
    private lateinit var entityManager: EntityManager

    /**
     * Instance of [RegisterRepository]
     */
    @Autowired
    private lateinit var repository: RegisterRepository

    /**
     * Test method for get registers.
     */
    @Test
    fun getRegisters() {
        val registers = repository.findAll()

        RegisterUtils.assertRegistersDeepEquals(expected = RegisterUtils.getRegisters(), actual = registers)

        assertSoftly {
            it.assertThat(RegisterUtils.getRegistersCount(entityManager = entityManager)).isEqualTo(RegisterUtils.REGISTERS_COUNT)
            it.assertThat(RegisterUtils.getRegisterValuesCount(entityManager = entityManager)).isEqualTo(RegisterUtils.REGISTER_VALUES_COUNT)
        }
    }

    /**
     * Test method for get register.
     */
    @Test
    fun getRegister() {
        for (i in 1..RegisterUtils.REGISTERS_COUNT) {
            val register = repository.findById(i).orElse(null)

            RegisterUtils.assertRegisterDeepEquals(expected = RegisterUtils.getRegister(index = i), actual = register)
        }

        assertThat(repository.findById(Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(RegisterUtils.getRegistersCount(entityManager = entityManager)).isEqualTo(RegisterUtils.REGISTERS_COUNT)
            it.assertThat(RegisterUtils.getRegisterValuesCount(entityManager = entityManager)).isEqualTo(RegisterUtils.REGISTER_VALUES_COUNT)
        }
    }

    /**
     * Test method for find register by number.
     */
    @Test
    fun findByNumber() {
        for (i in 1..RegisterUtils.REGISTERS_COUNT) {
            val register = RegisterUtils.getRegister(index = i)

            val result = repository.findByNumber(number = register.number).orElse(null)

            RegisterUtils.assertRegisterDeepEquals(expected = register, actual = result)
        }

        assertThat(repository.findByNumber(number = Int.MAX_VALUE)).isNotPresent

        assertSoftly {
            it.assertThat(RegisterUtils.getRegistersCount(entityManager = entityManager)).isEqualTo(RegisterUtils.REGISTERS_COUNT)
            it.assertThat(RegisterUtils.getRegisterValuesCount(entityManager = entityManager)).isEqualTo(RegisterUtils.REGISTER_VALUES_COUNT)
        }
    }

}
