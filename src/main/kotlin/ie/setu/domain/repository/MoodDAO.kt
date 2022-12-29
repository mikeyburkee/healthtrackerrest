package ie.setu.domain.repository

/**
 * Data access object class for mood logs
 *
 * @author Michael Burke
 */

import ie.setu.domain.Mood
import ie.setu.domain.db.Moods
import ie.setu.utils.mapToMood
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class MoodDAO {

    //Get all the moods in the database regardless of user id
    /**
     * Find all the [Mood] in the Moods table
     * @return array list of Moods
     */
    fun getAll(): ArrayList<Mood> {
        val moodsList: ArrayList<Mood> = arrayListOf()
        transaction {
            Moods.selectAll().map {
                moodsList.add(mapToMood(it)) }
        }
        return moodsList
    }

    //Find a specific activity by mood id
    /**
     * Find a [Mood] in the Moods table by ID
     * @return the Mood
     */
    fun findByMoodId(id: Int): Mood?{
        return transaction {
            Moods
                .select() { Moods.id eq id}
                .map{ mapToMood(it) }
                .firstOrNull()
        }
    }

    //Find all moods for a specific user id
    /**
     * Find all [Mood] in the Moods table by user ID
     * @return the list of Mood
     */
    fun findByUserId(userId: Int): List<Mood>{
        return transaction {
            Moods
                .select { Moods.userId eq userId}
                .map { mapToMood(it) }
        }
    }

    //Save a mood to the database
    /**
     * Adds a [Mood] to the Moods table
     * @return the id of the Mood following the add
     */
    fun save(mood: Mood): Int {
        return transaction {
            Moods.insert {
                it[description] = mood.description
                it[rating] = mood.rating
                it[dateEntry] = mood.dateEntry
                it[userId] = mood.userId
            }
        } get Moods.id
    }

    // update a mood by mood id
    /**
     * Update a [Mood] in the Moods table
     * @return the transaction status
     */
    fun updateByMoodId(moodId: Int, moodToUpdate: Mood) : Int{
        return transaction {
            Moods.update ({
                Moods.id eq moodId}) {
                it[description] = moodToUpdate.description
                it[rating] = moodToUpdate.rating
                it[dateEntry] = moodToUpdate.dateEntry
                it[userId] = moodToUpdate.userId
            }
        }
    }

    // delete a mood by mood id
    /**
     * Delete a [Mood] from the Moods table by Mood ID
     * @return the transaction status
     */
    fun deleteByMoodId (moodId: Int): Int{
        return transaction{
            Moods.deleteWhere { Moods.id eq moodId }
        }
    }

    // delete all moods by user id
    /**
     * Delete all [Mood] from the Moods table by user ID
     * @return the transaction status
     */
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Moods.deleteWhere { Moods.userId eq userId }
        }
    }

}