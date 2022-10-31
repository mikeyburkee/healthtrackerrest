package ie.setu.domain

import org.joda.time.DateTime

data class Mood     (var id: Int,
                     var description:String,
                     var rating: Int,
                     var dateEntry: DateTime,
                     var userId: Int)