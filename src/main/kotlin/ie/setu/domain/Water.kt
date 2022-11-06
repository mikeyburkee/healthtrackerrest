package ie.setu.domain

import org.joda.time.DateTime

/**
 * Data class for water logs
 *
 * @author Michael Burke
 */
data class Water (var id: Int,
                  var volume: Double,
                  var dateEntry: DateTime,
                  var userId: Int)