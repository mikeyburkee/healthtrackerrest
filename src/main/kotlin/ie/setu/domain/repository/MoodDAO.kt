package ie.setu.domain.repository

import ie.setu.domain.Mood
import ie.setu.domain.db.Moods
import ie.setu.utils.mapToMood
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class MoodDAO {

    //Get all the moods in the database regardless of user id
    fun getAll(): ArrayList<Mood> {
        val moodsList: ArrayList<Mood> = arrayListOf()
        transaction {
            Moods.selectAll().map {
                moodsList.add(mapToMood(it)) }
        }
        return moodsList
    }

    //Find a specific activity by mood id
    fun findByMoodId(id: Int): Mood?{
        return transaction {
            Moods
                .select() { Moods.id eq id}
                .map{ mapToMood(it) }
                .firstOrNull()
        }
    }

    //Find all moods for a specific user id
    fun findByUserId(userId: Int): List<Mood>{
        return transaction {
            Moods
                .select { Moods.userId eq userId}
                .map { mapToMood(it) }
        }
    }

    //Save a mood to the database
    fun save(mood: Mood): Int {
        return transaction {
            Moods.insert {
                it[description] = mood.description
                it[mood_value] = mood.mood_value
                it[logged] = mood.logged
                it[userId] = mood.userId
            }
        } get Moods.id
    }

    fun updateByMoodId(moodId: Int, moodToUpdate: Mood) : Int{
        return transaction {
            Moods.update ({
                Moods.id eq moodId}) {
                it[description] = moodToUpdate.description
                it[mood_value] = moodToUpdate.mood_value
                it[logged] = moodToUpdate.logged
                it[userId] = moodToUpdate.userId
            }
        }
    }

    fun deleteByMoodId (moodId: Int): Int{
        return transaction{
            Moods.deleteWhere { Moods.id eq moodId }
        }
    }

    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Moods.deleteWhere { Moods.userId eq userId }
        }
    }

}