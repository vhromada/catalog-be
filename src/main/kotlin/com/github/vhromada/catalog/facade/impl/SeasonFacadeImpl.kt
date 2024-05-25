package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.RegisterType
import com.github.vhromada.catalog.entity.Season
import com.github.vhromada.catalog.entity.filter.PagingFilter
import com.github.vhromada.catalog.entity.io.ChangeSeasonRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.SeasonFacade
import com.github.vhromada.catalog.mapper.SeasonMapper
import com.github.vhromada.catalog.service.RegisterService
import com.github.vhromada.catalog.service.SeasonService
import com.github.vhromada.catalog.service.ShowService
import com.github.vhromada.catalog.validator.SeasonValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for seasons.
 *
 * @author Vladimir Hromada
 */
@Component("seasonFacade")
class SeasonFacadeImpl(

    /**
     * Service for seasons
     */
    private val seasonService: SeasonService,

    /**
     * Service for shows
     */
    private val showService: ShowService,

    /**
     * Service for registers
     */
    private val registerService: RegisterService,

    /**
     * Mapper for seasons
     */
    private val mapper: SeasonMapper,

    /**
     * Validator for seasons
     */
    private val validator: SeasonValidator

) : SeasonFacade {

    override fun findAll(show: String, filter: PagingFilter): Page<Season> {
        val seasons = seasonService.search(show = showService.get(uuid = show).id!!, pageable = filter.toPageable(sort = Sort.by("number")))
        return Page(data = mapper.mapSeasons(seasons.content), page = seasons)
    }

    override fun get(show: String, uuid: String): Season {
        showService.get(uuid = show)
        return mapper.mapSeason(source = seasonService.get(uuid = uuid))
    }

    override fun add(show: String, request: ChangeSeasonRequest): Season {
        val domainShow = showService.get(uuid = show)
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.LANGUAGES, code = request.language!!)
        request.subtitles!!.filterNotNull().forEach { registerService.checkValue(type = RegisterType.SUBTITLES, code = it) }
        return mapper.mapSeason(source = seasonService.store(season = mapper.mapRequest(source = request).copy(show = domainShow)))
    }

    @Suppress("DuplicatedCode")
    override fun update(show: String, uuid: String, request: ChangeSeasonRequest): Season {
        showService.get(uuid = show)
        validator.validateRequest(request = request)
        registerService.checkValue(type = RegisterType.LANGUAGES, code = request.language!!)
        request.subtitles!!.filterNotNull().forEach { registerService.checkValue(type = RegisterType.SUBTITLES, code = it) }
        val season = seasonService.get(uuid = uuid)
        season.merge(season = mapper.mapRequest(source = request))
        return mapper.mapSeason(source = seasonService.store(season = season))
    }

    override fun remove(show: String, uuid: String) {
        showService.get(uuid = show)
        seasonService.remove(season = seasonService.get(uuid = uuid))
    }

    override fun duplicate(show: String, uuid: String): Season {
        showService.get(uuid = show)
        return mapper.mapSeason(source = seasonService.duplicate(season = seasonService.get(uuid = uuid)))
    }

}
