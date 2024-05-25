package com.github.vhromada.catalog.utils

import com.github.vhromada.catalog.domain.Register
import com.github.vhromada.catalog.domain.RegisterValue
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions.assertSoftly

/**
 * A class represents utility class for registers.
 *
 * @author Vladimir Hromada
 */
object RegisterUtils {

    /**
     * Count of registers
     */
    const val REGISTERS_COUNT = 4

    /**
     * Count of register's values
     */
    const val REGISTER_VALUES_COUNT = 15

    /**
     * Returns registers.
     *
     * @return registers
     */
    fun getRegisters(): List<Register> {
        val registers = mutableListOf<Register>()
        for (i in 1..REGISTERS_COUNT) {
            registers.add(getRegister(index = i))
        }

        return registers
    }

    /**
     * Returns register for index.
     *
     * @param index index
     * @return register for index
     */
    fun getRegister(index: Int): Register {
        val name = when (index) {
            1 -> "Program formats"
            2 -> "Languages"
            3 -> "Subtitles"
            4 -> "Book item formats"
            else -> throw IllegalArgumentException("Bad index")
        }

        val values = when (index) {
            1 -> getGameFormats()
            2 -> getLanguages()
            3 -> getSubtitles()
            4 -> getBookItemFormats()
            else -> throw IllegalArgumentException("Bad index")
        }

        val register = Register(
            id = index,
            number = index,
            name = name,
            values = values
        )
        register.values.forEach { it.register = register }
        return register
    }

    /**
     * Returns formats for game.
     *
     * @return formats for game
     */
    private fun getGameFormats(): List<RegisterValue> {
        return listOf(
            RegisterValue(id = 1, code = "ISO", order = 1),
            RegisterValue(id = 2, code = "BINARY", order = 2),
            RegisterValue(id = 3, code = "STEAM", order = 3),
            RegisterValue(id = 4, code = "BATTLE_NET", order = 4)
        )
    }

    /**
     * Returns languages.
     *
     * @return languages
     */
    private fun getLanguages(): List<RegisterValue> {
        return listOf(
            RegisterValue(id = 5, code = "CZ", order = 1),
            RegisterValue(id = 6, code = "EN", order = 2),
            RegisterValue(id = 7, code = "FR", order = 3),
            RegisterValue(id = 8, code = "JP", order = 4),
            RegisterValue(id = 9, code = "SK", order = 5)
        )
    }

    /**
     * Returns subtitles.
     *
     * @return subtitles
     */
    private fun getSubtitles(): List<RegisterValue> {
        return listOf(
            RegisterValue(id = 10, code = "CZ", order = 1),
            RegisterValue(id = 11, code = "EN", order = 2)
        )
    }

    /**
     * Returns formats for book item.
     *
     * @return formats for book item
     */
    private fun getBookItemFormats(): List<RegisterValue> {
        return listOf(
            RegisterValue(id = 12, code = "PAPER", order = 1),
            RegisterValue(id = 13, code = "PDF", order = 2),
            RegisterValue(id = 14, code = "DOC", order = 3),
            RegisterValue(id = 15, code = "TXT", order = 4)
        )
    }

    /**
     * Returns count of registers.
     *
     * @param entityManager entity manager
     * @return count of registers
     */
    fun getRegistersCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(r.id) FROM Register r", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Returns count of register's values.
     *
     * @param entityManager entity manager
     * @return count of register's values
     */
    fun getRegisterValuesCount(entityManager: EntityManager): Int {
        return entityManager.createQuery("SELECT COUNT(v.id) FROM RegisterValue v", java.lang.Long::class.java).singleResult.toInt()
    }

    /**
     * Asserts list of registers deep equals.
     *
     * @param expected expected list of registers
     * @param actual   actual list of registers
     */
    fun assertRegistersDeepEquals(expected: List<Register>, actual: List<Register>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertRegisterDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts register deep equals.
     *
     * @param expected    expected register
     * @param actual      actual register
     * @param checkValues true if values should be checked
     */
    fun assertRegisterDeepEquals(expected: Register?, actual: Register?, checkValues: Boolean = true) {
        if (expected == null) {
            assertThat(actual).isNull()
        } else {
            assertThat(actual).isNotNull
            assertSoftly {
                it.assertThat(actual!!.id).isEqualTo(expected.id)
                it.assertThat(actual.number).isEqualTo(expected.number)
                it.assertThat(actual.name).isEqualTo(expected.name)
            }
            if (checkValues) {
                assertRegisterValuesDeepEquals(expected = expected.values, actual = actual!!.values)
            }
        }
    }

    /**
     * Asserts list of register's values deep equals.
     *
     * @param expected expected list of register's values
     * @param actual   actual list of register's values
     */
    private fun assertRegisterValuesDeepEquals(expected: List<RegisterValue>, actual: List<RegisterValue>) {
        assertThat(expected.size).isEqualTo(actual.size)
        if (expected.isNotEmpty()) {
            for (i in expected.indices) {
                assertRegisterValueDeepEquals(expected = expected[i], actual = actual[i])
            }
        }
    }

    /**
     * Asserts register's value deep equals.
     *
     * @param expected expected register's value
     * @param actual   actual register's value
     */
    private fun assertRegisterValueDeepEquals(expected: RegisterValue?, actual: RegisterValue?) {
        if (expected == null) {
            assertThat(actual).isNull()
        } else {
            assertThat(actual).isNotNull
            assertSoftly {
                it.assertThat(actual!!.id).isEqualTo(expected.id)
                it.assertThat(actual.code).isEqualTo(expected.code)
                it.assertThat(actual.order).isEqualTo(expected.order)
            }
            if (expected.register != null) {
                assertThat(actual!!.register).isNotNull
                assertThat(actual.register!!.values).hasSameSizeAs(expected.register!!.values)
                assertRegisterDeepEquals(expected = expected.register!!, actual = actual.register!!, checkValues = false)
            }
        }
    }

}
