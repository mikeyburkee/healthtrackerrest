package ie.setu.domain.repository

/**
 * Data access object class for water logs
 *
 * @author Michael Burke
 */


import ie.setu.domain.Water
import ie.setu.domain.db.Waters
import ie.setu.utils.mapToWater
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class WaterDAO {

    //Get all the water logs in the database regardless of user id
    fun getAll(): ArrayList<Water> {
        val watersList: ArrayList<Water> = arrayListOf()
        transaction {
            Waters.selectAll().map {
                watersList.add(mapToWater(it)) }
        }
        return watersList
    }

    //Find a specific water by water id
    fun findByWaterId(id: Int): Water?{
        return transaction {
            Waters
                .select() { Waters.id eq id}
                .map{ mapToWater(it) }
                .firstOrNull()
        }
    }

    //Find all waters for a specific user id
    fun findByUserId(userId: Int): List<Water>{
        return transaction {
            Waters
                .select { Waters.userId eq userId}
                .map { mapToWater(it) }
        }
    }

    //Save a water to the database
    fun save(water: Water): Int {
        return transaction {
            Waters.insert {
                it[volume] = water.volume
                it[dateEntry] = water.dateEntry
                it[userId] = water.userId
            }
        } get Waters.id
    }

    // update a water log by water id
    fun updateByWaterId(waterId: Int, waterToUpdate: Water) : Int{
        return transaction {
            Waters.update ({
                Waters.id eq waterId}) {
                it[volume] = waterToUpdate.volume
                it[dateEntry] = waterToUpdate.dateEntry
                it[userId] = waterToUpdate.userId
            }
        }
    }

    // delete a water log by water id
    fun deleteByWaterId (waterId: Int): Int{
        return transaction{
            Waters.deleteWhere { Waters.id eq waterId }
        }
    }

    // delete water logs by a user id
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Waters.deleteWhere { Waters.userId eq userId }
        }
    }

}