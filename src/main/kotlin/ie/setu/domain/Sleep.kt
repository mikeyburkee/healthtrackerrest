package ie.setu.domain

import org.joda.time.DateTime

/**
 * Data class for sleep logs
 *
 * @author Michael Burke
 */
data class Sleep    (var id: Int,
                     var description:String,
                     var duration: Double,
                     var rating: Int,
                     var wakeUpTime: DateTime,
                     var userId: Int)