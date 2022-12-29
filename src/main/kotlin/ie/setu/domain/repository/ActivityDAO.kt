package ie.setu.domain.repository

/**
 * Data access object class for activity logs
 *
 * @author Michael Burke
 */

import ie.setu.domain.Activity
import ie.setu.domain.db.Activities
import ie.setu.utils.mapToActivity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class ActivityDAO {

    //Get all the activities in the database regardless of user id
    /**
     * Find all the [Activity] in the Activities table
     * @return array list of Activities
     */
    fun getAll(): List<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivity(it)) }
        }
        return activitiesList
    }

    // Return ascending order of [sortParam] list
    /**
     * Find all the [Activity] in the Activities table by ascending
     * @return array list of Activities
     */
    fun getAllSortedAscending(sortParam: String): List<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivity(it)) }
        }

        when (sortParam) {
            "duration" -> return activitiesList.sortedBy { it.duration }
            "calories" -> return activitiesList.sortedBy { it.calories }
            else -> return activitiesList
        }

    }

    // Return descending order of [sortParam] list
    /**
     * Find all the [Activity] in the Activities table by descending
     * @return array list of Activities
     */
    fun getAllSortedDescending(sortParam: String): List<Activity> {
        val activitiesList: ArrayList<Activity> = arrayListOf()
        transaction {
            Activities.selectAll().map {
                activitiesList.add(mapToActivity(it)) }
        }

        when (sortParam) {
            "duration" -> return activitiesList.sortedByDescending { it.duration }
            "calories" -> return activitiesList.sortedByDescending { it.calories }
            else -> return activitiesList
        }

    }


    //Find a specific activity by activity id
    /**
     * Find a [Activity] in the Activities table by ID
     * @return the Activity
     */
    fun findByActivityId(id: Int): Activity?{
        return transaction {
            Activities
                .select() { Activities.id eq id}
                .map{mapToActivity(it)}
                .firstOrNull()
        }
    }

    //Find all activities for a specific user id
    /**
     * Find all [Activity] in the Activities table by user ID
     * @return the list of Activities
     */
    fun findByUserId(userId: Int): List<Activity>{
        return transaction {
            Activities
                .select {Activities.userId eq userId}
                .map {mapToActivity(it)}
        }
    }

    //Save an activity to the database
    /**
     * Adds a [Activity] to the Activities table
     * @return the id of the Activity following the add
     */
    fun save(activity: Activity): Int {
        return transaction {
            Activities.insert {
                it[description] = activity.description
                it[duration] = activity.duration
                it[calories] = activity.calories
                it[started] = activity.started
                it[rating] = activity.rating
                it[userId] = activity.userId
            }
        } get Activities.id
    }

    // update activity by activity id
    /**
     * Update a [Activity] in the Activities table
     * @return the transaction status
     */
    fun updateByActivityId(activityId: Int, activityToUpdate: Activity) : Int{
        return transaction {
            Activities.update ({
                Activities.id eq activityId}) {
                it[description] = activityToUpdate.description
                it[duration] = activityToUpdate.duration
                it[calories] = activityToUpdate.calories
                it[started] = activityToUpdate.started
                it[rating] = activityToUpdate.rating
                it[userId] = activityToUpdate.userId
            }
        }
    }

    // delete activity by activity id
    /**
     * Delete a [Activity] from the Activties table by Activity ID
     * @return the transaction status
     */
    fun deleteByActivityId (activityId: Int): Int{
        return transaction{
            Activities.deleteWhere { Activities.id eq activityId }
        }
    }

    // delete all activities by user id
    /**
     * Delete all [Activity] from the Activities table by user ID
     * @return the transaction status
     */
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Activities.deleteWhere { Activities.userId eq userId }
        }
    }

}