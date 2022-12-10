package ie.setu.domain.repository

/**
 * Data access object class for steps logs
 *
 * @author Michael Burke
 */


import ie.setu.domain.Step
import ie.setu.domain.db.Steps
import ie.setu.utils.mapToStep
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class StepDAO {

    //Get all the steps logs in the database regardless of user id
    fun getAll(): ArrayList<Step> {
        val stepssList: ArrayList<Step> = arrayListOf()
        transaction {
            Steps.selectAll().map {
                stepssList.add(mapToStep(it)) }
        }
        return stepssList
    }

    //Find a specific steps by steps id
    fun findByStepId(id: Int): Step?{
        return transaction {
            Steps
                .select() { Steps.id eq id}
                .map{ mapToStep(it) }
                .firstOrNull()
        }
    }

    //Find all stepss for a specific user id
    fun findByUserId(userId: Int): List<Step>{
        return transaction {
            Steps
                .select { Steps.userId eq userId}
                .map { mapToStep(it) }
        }
    }

    //Save a steps to the database
    fun save(steps: Step): Int {
        return transaction {
            Steps.insert {
                it[step_count] = steps.step_count
                it[dateEntry] = steps.dateEntry
                it[userId] = steps.userId
            }
        } get Steps.id
    }

    // update a steps log by steps id
    fun updateByStepId(stepsId: Int, stepsToUpdate: Step) : Int{
        return transaction {
            Steps.update ({
                Steps.id eq stepsId}) {
                it[step_count] = stepsToUpdate.step_count
                it[dateEntry] = stepsToUpdate.dateEntry
                it[userId] = stepsToUpdate.userId
            }
        }
    }

    // delete a steps log by steps id
    fun deleteByStepId (stepsId: Int): Int{
        return transaction{
            Steps.deleteWhere { Steps.id eq stepsId }
        }
    }

    // delete steps logs by a user id
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Steps.deleteWhere { Steps.userId eq userId }
        }
    }

}