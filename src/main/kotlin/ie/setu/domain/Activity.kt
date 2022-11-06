package ie.setu.domain

import org.joda.time.DateTime

/**
 * Data class for an Activity
 *
 * @author Michael Burke
 */

data class Activity (var id: Int,
                     var description:String,
                     var duration: Double,
                     var calories: Int,
                     var started: DateTime,
                     var rating: Int,
                     var userId: Int)