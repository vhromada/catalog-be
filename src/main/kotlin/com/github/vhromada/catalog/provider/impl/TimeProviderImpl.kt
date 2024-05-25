package com.github.vhromada.catalog.provider.impl

import com.github.vhromada.catalog.provider.TimeProvider
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * A class represents implementation of provider for time.
 *
 * @author Vladimir Hromada
 */
@Component("timeProvider")
class TimeProviderImpl : TimeProvider {

    override fun getTime(): LocalDateTime {
        return LocalDateTime.now()
    }

}
