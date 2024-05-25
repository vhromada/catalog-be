package com.github.vhromada.catalog.facade.impl

import com.github.vhromada.catalog.entity.Music
import com.github.vhromada.catalog.entity.MusicStatistics
import com.github.vhromada.catalog.entity.filter.NameFilter
import com.github.vhromada.catalog.entity.io.ChangeMusicRequest
import com.github.vhromada.catalog.entity.paging.Page
import com.github.vhromada.catalog.facade.MusicFacade
import com.github.vhromada.catalog.mapper.MusicMapper
import com.github.vhromada.catalog.service.MusicService
import com.github.vhromada.catalog.validator.MusicValidator
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

/**
 * A class represents implementation of facade for music.
 *
 * @author Vladimir Hromada
 */
@Component("musicFacade")
class MusicFacadeImpl(

    /**
     * Service for music
     */
    private val service: MusicService,

    /**
     * Mapper for music
     */
    private val mapper: MusicMapper,

    /**
     * Validator for music
     */
    private val validator: MusicValidator

) : MusicFacade {

    override fun search(filter: NameFilter): Page<Music> {
        val music = service.search(filter = mapper.mapFilter(source = filter), pageable = filter.toPageable(sort = Sort.by("normalizedName", "id")))
        return Page(data = mapper.mapMusicList(source = music.content), page = music)
    }

    override fun get(uuid: String): Music {
        return mapper.mapMusic(source = service.get(uuid = uuid))
    }

    override fun add(request: ChangeMusicRequest): Music {
        validator.validateRequest(request = request)
        return mapper.mapMusic(source = service.store(music = mapper.mapRequest(source = request)))
    }

    override fun update(uuid: String, request: ChangeMusicRequest): Music {
        validator.validateRequest(request = request)
        val music = service.get(uuid = uuid)
        music.merge(music = mapper.mapRequest(source = request))
        return mapper.mapMusic(source = service.store(music = music))
    }

    override fun remove(uuid: String) {
        service.remove(music = service.get(uuid = uuid))
    }

    override fun duplicate(uuid: String): Music {
        return mapper.mapMusic(source = service.duplicate(music = service.get(uuid = uuid)))
    }

    override fun getStatistics(): MusicStatistics {
        return service.getStatistics()
    }

}
