package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.JokeMapperImpl
import com.github.vhromada.catalog.provider.UuidProvider
import com.github.vhromada.catalog.utils.JokeUtils
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
 * A class represents test for class [JokeMapper].
 *
 * @author Vladimir Hromada
 */
@ExtendWith(MockitoExtension::class)
class JokeMapperTest {

    /**
     * Instance of [UuidProvider]
     */
    @Mock
    private lateinit var uuidProvider: UuidProvider

    /**
     * Instance of [JokeMapper]
     */
    private lateinit var mapper: JokeMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = JokeMapperImpl(uuidProvider = uuidProvider)
    }

    /**
     * Test method for [JokeMapper.mapJoke].
     */
    @Test
    fun mapJoke() {
        val joke = JokeUtils.getDomainJoke(index = 1)

        val result = mapper.mapJoke(source = joke)

        JokeUtils.assertJokeDeepEquals(expected = joke, actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [JokeMapper.mapJokes].
     */
    @Test
    fun mapJokes() {
        val joke = JokeUtils.getDomainJoke(index = 1)

        val result = mapper.mapJokes(source = listOf(joke))

        JokeUtils.assertJokesDeepEquals(expected = listOf(joke), actual = result)
        verifyNoInteractions(uuidProvider)
    }

    /**
     * Test method for [JokeMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = JokeUtils.newRequest()
        whenever(uuidProvider.getUuid()).thenReturn(TestConstants.UUID)

        val result = mapper.mapRequest(source = request)

        JokeUtils.assertRequestDeepEquals(expected = request, actual = result, uuid = TestConstants.UUID)
        verify(uuidProvider).getUuid()
        verifyNoMoreInteractions(uuidProvider)
    }

}
