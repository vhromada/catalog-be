package com.github.vhromada.catalog.entity

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * A class represents list of issues.
 *
 * @author Vladimir Hromada
 */
data class IssueList(

    /**
     * Issues
     */
    @JsonProperty("errors")
    val issues: List<Issue>

)
