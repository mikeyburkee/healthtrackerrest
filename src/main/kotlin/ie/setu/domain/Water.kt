package ie.setu.domain

import org.joda.time.DateTime

data class Water (var id: Int,
                  var volume: Double,
                  var dateEntry: DateTime,
                  var userId: Int)