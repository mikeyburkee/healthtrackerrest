package ie.setu.domain

import org.joda.time.DateTime

data class Mood     (var id: Int,
                     var description:String,
                     var mood_value: Int,
                     var logged: DateTime,
                     var userId: Int)