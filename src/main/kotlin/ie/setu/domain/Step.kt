package ie.setu.domain

import org.joda.time.DateTime

/**
 * Data class for steps logs
 *
 * @author Michael Burke
 */
data class Step    (var id: Int,
                     var step_count: Int,
                     var dateEntry: DateTime,
                     var userId: Int)