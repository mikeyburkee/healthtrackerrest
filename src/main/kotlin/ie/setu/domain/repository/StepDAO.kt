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
    /**
     * Find all the [Step] in the Steps table
     * @return array list of Steps
     */
    fun getAll(): ArrayList<Step> {
        val stepssList: ArrayList<Step> = arrayListOf()
        transaction {
            Steps.selectAll().map {
                stepssList.add(mapToStep(it)) }
        }
        return stepssList
    }

    //Find a specific steps by steps id
    /**
     * Find a [Step] in the Steps table by step ID
     * @return the Step
     */
    fun findByStepId(id: Int): Step?{
        return transaction {
            Steps
                .select() { Steps.id eq id}
                .map{ mapToStep(it) }
                .firstOrNull()
        }
    }

    //Find all stepss for a specific user id
    /**
     * Find all [Steps] in the Steps table by user ID
     * @return the list of Steps
     */
    fun findByUserId(userId: Int): List<Step>{
        return transaction {
            Steps
                .select { Steps.userId eq userId}
                .map { mapToStep(it) }
        }
    }

    //Save a steps to the database
    /**
     * Adds a [Step] to the Steps table
     * @return the id of the step following the add
     */
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
    /**
     * Update a [Step] in the Steps table
     * @return the transaction status
     */
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
    /**
     * Delete a [Step] from the Steps table by step ID
     * @return the transaction status
     */
    fun deleteByStepId (stepsId: Int): Int{
        return transaction{
            Steps.deleteWhere { Steps.id eq stepsId }
        }
    }

    // delete steps logs by a user id
    /**
     * Delete all [Step] from the Steps table by user ID
     * @return the transaction status
     */
    fun deleteByUserId (userId: Int): Int{
        return transaction{
            Steps.deleteWhere { Steps.userId eq userId }
        }
    }

}