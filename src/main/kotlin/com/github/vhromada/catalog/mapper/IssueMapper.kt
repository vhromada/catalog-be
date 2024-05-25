package com.github.vhromada.catalog.mapper

import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.entity.IssueList

/**
 * An interface represents mapper between result and issues.
 *
 * @author Vladimir Hromada
 */
interface IssueMapper {

    /**
     * Maps result to issues.
     *
     * @param source result
     * @return mapped issues
     */
    fun map(source: Result<*>): IssueList

}
