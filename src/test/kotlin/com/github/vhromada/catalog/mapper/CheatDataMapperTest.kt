package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.mapper.impl.CheatDataMapperImpl
import com.github.vhromada.catalog.utils.CheatDataUtils
import com.github.vhromada.catalog.utils.GameUtils
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * A class represents test for class [CheatDataMapper].
 *
 * @author Vladimir Hromada
 */
class CheatDataMapperTest {

    /**
     * Instance of [CheatDataMapper]
     */
    private lateinit var mapper: CheatDataMapper

    /**
     * Initializes mapper.
     */
    @BeforeEach
    fun setUp() {
        mapper = CheatDataMapperImpl()
    }

    /**
     * Test method for [CheatDataMapper.mapCheatDataList].
     */
    @Test
    fun mapCheatDataList() {
        val cheatData = GameUtils.getDomainGame(index = 2).cheat!!.data

        val result = mapper.mapCheatDataList(source = cheatData)

        CheatDataUtils.assertCheatDataDeepEquals(expected = cheatData, actual = result)
    }

    /**
     * Test method for [CheatDataMapper.mapRequest].
     */
    @Test
    fun mapRequest() {
        val request = CheatDataUtils.newRequest()

        val result = mapper.mapRequest(source = request)

        CheatDataUtils.assertRequestDeepEquals(expected = request, actual = result)
    }

    /**
     * Test method for [CheatDataMapper.mapRequests].
     */
    @Test
    fun mapRequests() {
        val request = CheatDataUtils.newRequest()

        val result = mapper.mapRequests(source = listOf(request))

        CheatDataUtils.assertRequestsDeepEquals(expected = listOf(request), actual = result)
    }

}
