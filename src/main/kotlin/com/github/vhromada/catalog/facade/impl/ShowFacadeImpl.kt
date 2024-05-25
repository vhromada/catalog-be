package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Show
import com.github.vhromada.catalog.entity.ShowStatistics
import com.github.vhromada.catalog.entity.filter.MultipleNameFilter
import com.github.vhromada.catalog.entity.io.ChangeShowRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.exception.InputException
import com.github.vhromada.catalog.facade.ShowFacade
import com.github.vhromada.catalog.mapper.ShowMapper
import com.github.vhromada.catalog.service.GenreService
import com.github.vhromada.catalog.service.PictureService
import com.github.vhromada.catalog.service.ShowService
import com.github.vhromada.catalog.validator.ShowValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for shows.
 *
 * @author Vladimir Hromada
 */
@Component("showFacade")
class ShowFacadeImpl(

    /**
     * Service for shows
     */
    private val showService: ShowService,

    /**
     * Service for genres
     */
    private val genreService: GenreService,

    /**
     * Service for pictures
     */
    private val pictureService: PictureService,

    /**
     * Mapper for shows
     */
    private val mapper: ShowMapper,

    /**
     * Validator for shows
     */
    private val validator: ShowValidator

) : ShowFacade {

    override fun search(filter: MultipleNameFilter): Page<Show> {
        val shows = showService.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedCzechName", "id")))
        val data = shows.content.map {
            mapper.mapShow(source = it)
                .copy(picture = getPicture(show = it)?.uuid)
        }
        return Page(data = data, page = shows)
    }

    override fun get(uuid: String): Show {
        val show = showService.get(uuid = uuid)
        return mapper.mapShow(source = show)
            .copy(picture = getPicture(show = show)?.uuid)
    }

    override fun add(request: ChangeShowRequest): Show {
        validator.validateRequest(request = request)
        val show = mapper.mapRequest(source = request)
        show.picture = getPicture(request = request)?.id
        show.genres.addAll(getGenres(request = request))
        val addedShow = showService.store(show = show)
        return mapper.mapShow(source = addedShow)
            .copy(picture = getPicture(show = addedShow)?.uuid)
    }

    override fun update(uuid: String, request: ChangeShowRequest): Show {
        validator.validateRequest(request = request)
        val show = showService.get(uuid = uuid)
        show.merge(show = mapper.mapRequest(source = request))
        show.picture = getPicture(request = request)?.id
        show.genres.clear()
        show.genres.addAll(getGenres(request = request))
        val updatedShow = showService.store(show = show)
        return mapper.mapShow(source = updatedShow)
            .copy(picture = getPicture(show = updatedShow)?.uuid)
    }

    override fun remove(uuid: String) {
        showService.remove(show = showService.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Show {
        val duplicatedShow = showService.duplicate(show = showService.get(uuid = uuid))
        return mapper.mapShow(source = duplicatedShow)
            .copy(picture = getPicture(show = duplicatedShow)?.uuid)
    }

    override fun getStatistics(): ShowStatistics {
        return showService.getStatistics()
    }

    /**
     * Returns picture.
     *
     * @param show show
     * @returns picture
     */
    private fun getPicture(show: com.github.vhromada.catalog.domain.Show): com.github.vhromada.catalog.domain.Picture? {
        return if (show.picture == null) null else pictureService.getById(id = show.picture!!)
    }

    /**
     * Returns picture.
     *
     * @param request request for changing show
     * @returns picture
     * @throws InputException if picture doesn't exist in data storage
     */
    private fun getPicture(request: ChangeShowRequest): com.github.vhromada.catalog.domain.Picture? {
        return if (request.picture == null) null else pictureService.getByUuid(uuid = request.picture)
    }

    /**
     * Returns genres.
     *
     * @param request request for changing show
     * @returns genres
     * @throws InputException if genre doesn't exist in data storage
     */
    private fun getGenres(request: ChangeShowRequest): List<com.github.vhromada.catalog.domain.Genre> {
        return request.genres!!.filterNotNull().map { genreService.get(uuid = it) }
    }

}
