package ie.setu.repository

/**
 * DAO unit test class for steps
 *
 * @author Michael Burke
 */

import ie.setu.domain.Step
import ie.setu.domain.db.Steps
import ie.setu.domain.repository.StepDAO
import ie.setu.helpers.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val step1 = steps.get(0)
private val step2 = steps.get(1)
private val step3 = steps.get(2)

class StepDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateSteps {

        @Test
        fun `multiple step logs added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three step logs
                populateUserTable()
                val stepDAO = populateStepTable()

                //Act & Assert
                assertEquals(3, stepDAO.getAll().size)
                assertEquals(step1, stepDAO.findByStepId(step1.id))
                assertEquals(step2, stepDAO.findByStepId(step2.id))
                assertEquals(step3, stepDAO.findByStepId(step3.id))
            }
        }
    }

    @Nested
    inner class ReadSteps {

        @Test
        fun `getting all activites from a populated table returns all rows`() {
            transaction {
                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()
                //Act & Assert
                assertEquals(3, stepDAO.getAll().size)
            }
        }

        @Test
        fun `get step by user id that has no steps, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()
                //Act & Assert
                assertEquals(0, stepDAO.findByUserId(3).size)
            }
        }

        @Test
        fun `get step by user id that exists, results in a correct activitie(s) returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()
                //Act & Assert
                assertEquals(step1, stepDAO.findByUserId(1).get(0))
                assertEquals(step2, stepDAO.findByUserId(1).get(1))
                assertEquals(step3, stepDAO.findByUserId(2).get(0))
            }
        }

        @Test
        fun `get all steps over empty table returns none`() {
            transaction {

                //Arrange - create and setup stepDAO object
                SchemaUtils.create(Steps)
                val stepDAO = StepDAO()

                //Act & Assert
                assertEquals(0, stepDAO.getAll().size)
            }
        }

        @Test
        fun `get step by step id that has no records, results in no record returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()
                //Act & Assert
                assertEquals(null, stepDAO.findByStepId(4))
            }
        }

        @Test
        fun `get step by step id that exists, results in a correct step returned`() {
            transaction {
                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()
                //Act & Assert
                assertEquals(step1, stepDAO.findByStepId(1))
                assertEquals(step3, stepDAO.findByStepId(3))
            }
        }
    }

    @Nested
    inner class UpdateSteps {

        @Test
        fun `updating existing step in table results in successful update`() {
            transaction {

                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()

                //Act & Assert
                val step3updated = Step(id = 3, step_count = 1000,
                    dateEntry = DateTime.now(), userId = 2)
                stepDAO.updateByStepId(step3updated.id, step3updated)
                assertEquals(step3updated, stepDAO.findByStepId(3))
            }
        }

        @Test
        fun `updating non-existent step in table results in no updates`() {
            transaction {

                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()

                //Act & Assert
                val step4updated = Step(id = 4, step_count = 2000,
                    dateEntry = DateTime.now(), userId = 2)
                stepDAO.updateByStepId(4, step4updated)
                assertEquals(null, stepDAO.findByStepId(4))
                assertEquals(3, stepDAO.getAll().size)
            }
        }
    }

    @Nested
    inner class DeleteSteps {

        @Test
        fun `deleting a non-existent step (by id) in table results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()

                //Act & Assert
                assertEquals(3, stepDAO.getAll().size)
                stepDAO.deleteByStepId(4)
                assertEquals(3, stepDAO.getAll().size)


            }
        }

        @Test
        fun `deleting an existing step (by id) in table results in record being deleted`() {
            transaction {

                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()

                //Act & Assert
                assertEquals(3, stepDAO.getAll().size)
                stepDAO.deleteByStepId(step3.id)
                assertEquals(2, stepDAO.getAll().size)
            }
        }


        @Test
        fun `deleting steps when none exist for user id results in no deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()

                //Act & Assert
                assertEquals(3, stepDAO.getAll().size)
                stepDAO.deleteByUserId(3)
                assertEquals(3, stepDAO.getAll().size)
            }
        }

        @Test
        fun `deleting steps when 1 or more exist for user id results in deletion`() {
            transaction {

                //Arrange - create and populate tables with three users and three steps
                populateUserTable()
                val stepDAO = populateStepTable()

                //Act & Assert
                assertEquals(3, stepDAO.getAll().size)
                stepDAO.deleteByUserId(1)
                assertEquals(1, stepDAO.getAll().size)
            }
        }
    }

}