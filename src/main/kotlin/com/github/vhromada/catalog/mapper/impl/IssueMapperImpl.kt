package com.github.vhromada.catalog.mapper.impl

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.entity.Issue
import com.github.vhromada.catalog.entity.IssueList
import com.github.vhromada.catalog.mapper.IssueMapper
import org.springframework.stereotype.Component

/**
 * A class represents implementation of mapper between result and issues.
 *
 * @author Vladimir Hromada
 */
@Component("issueMapper")
class IssueMapperImpl : IssueMapper {

    override fun map(source: Result<*>): IssueList {
        return IssueList(issues = source.events().map { mapIssue(event = it) })
    }

    /**
     * Maps event to issue.
     *
     * @param event event
     * @return mapped issue
     */
    private fun mapIssue(event: Event): Issue {
        return Issue(code = event.key, message = event.message)
    }

}
