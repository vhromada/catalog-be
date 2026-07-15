package com.github.vhromada.catalog.validator.utils

import com.github.vhromada.catalog.common.result.Event
import com.github.vhromada.catalog.common.result.Result
import com.github.vhromada.catalog.common.result.Severity

/**
 * An object represents support for validators.
 *
 * @author Vladimir Hromada
 */
object ValidationSupport {

    /**
     * Validates czech and original name.
     * <br></br>
     * Validation errors:
     *
     *  * Czech name is null
     *  * Czech name is empty string
     *  * Original name is null
     *  * Original name is empty string
     *
     * @param czechName    czech name
     * @param originalName original name
     * @param prefix       prefix for event key
     * @param result       result with validation errors
     */
    fun validateNames(czechName: String?, originalName: String?, prefix: String, result: Result<Unit>) {
        when {
            czechName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "${prefix}CZECH_NAME_NULL", message = "Czech name mustn't be null."))
            }

            czechName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "${prefix}CZECH_NAME_EMPTY", message = "Czech name mustn't be empty string."))
            }
        }
        when {
            originalName == null -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "${prefix}ORIGINAL_NAME_NULL", message = "Original name mustn't be null."))
            }

            originalName.isBlank() -> {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "${prefix}ORIGINAL_NAME_EMPTY", message = "Original name mustn't be empty string."))
            }
        }
    }

    /**
     * Validates list of items.
     * <br></br>
     * Validation errors:
     *
     *  * Items are null
     *  * Items contain null value
     *  * Item is empty string
     *
     * @param items           items
     * @param collectionKey   event key prefix for the collection
     * @param itemKey         event key prefix for an item
     * @param collectionLabel label for the collection in messages
     * @param itemLabel       label for an item in messages
     * @param result          result with validation errors
     */
    fun validateItems(items: List<String?>?, collectionKey: String, itemKey: String, collectionLabel: String, itemLabel: String, result: Result<Unit>) {
        if (items == null) {
            result.addEvent(event = Event(severity = Severity.ERROR, key = "${collectionKey}_NULL", message = "$collectionLabel mustn't be null."))
        } else {
            if (items.contains(null)) {
                result.addEvent(event = Event(severity = Severity.ERROR, key = "${collectionKey}_CONTAIN_NULL", message = "$collectionLabel mustn't contain null value."))
            }
            items.filterNotNull().forEach {
                if (it.isBlank()) {
                    result.addEvent(event = Event(severity = Severity.ERROR, key = "${itemKey}_EMPTY", message = "$itemLabel mustn't be empty string."))
                }
            }
        }
    }

}
