package ie.setu.domain.repository

/**
 * Data access object class for sleep logs
 *
 * @author Michael Burke
 */

import ie.setu.domain.Sleep
import ie.setu.domain.db.Sleeps
import ie.setu.utils.mapToSleep
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class SleepDAO {

    //Get all the sleep logs in the database regardless of user id
    fun getAll(): ArrayList<Sleep> {
        val sleepsList: ArrayList<Sleep> = arrayListOf()
        transaction {
            Sleeps.selectAll().map {
                sleepsList.add(mapToSleep(it)) }
        }
        return sleepsList
    }

    //Find a specific sleep by sleep id
    fun findBySleepId(id: Int): Sleep?{
        return transaction {
            Sleeps
                .select() { Sleeps.id eq id}
                .map{ mapToSleep(it) }
                .firstOrNull()
        }
    }

    //Find all sleeps for a specific user id
    fun findByUserId(userId: Int): List<Sleep>{
        return transaction {
            Sleeps
                .select { Sleeps.userId eq userId}
                .map { mapToSleep(it) }
        }
    }

    //Save a sleep to the database
    fun save(sleep: Sleep): Int {
        return transaction {
            Sleeps.insert {
                it[description] = sleep.description
                it[duration] = sleep.duration
                it[rating] = sleep.rating
                it[wakeUpTime] = sleep.wakeUpTime
                it[userId] = sleep.userId
            }
        } get Sleeps.id
    }

    // update a sleep by sleep id
    fun updateBySleepId(sleepId: Int, sleepToUpdate: Sleep) : Int{
        return transaction {
            Sleeps.update ({
                Sleeps.id eq sleepId}) {
                it[description] = sleepToUpdate.description
                it[duration] = sleepToUpdate.duration
                it[rating] = sleepToUpdate.rating
                it[wakeUpTime] = sleepToUpdate.wakeUpTime
                it[userId] = sleepToUpdate.userId
            }
        }
    }

    // delete a sleep by sleep id

    fun deleteBySleepId (sleepId: Int): Int{
        return transaction{
            Sleeps.deleteWhere { Sleeps.id eq sleepId }
        }
    }

    // delete all sleeps by user id
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Sleeps.deleteWhere { Sleeps.userId eq userId }
        }
    }

}