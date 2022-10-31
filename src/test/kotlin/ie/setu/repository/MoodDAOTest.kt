package ie.setu.repository

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import ie.setu.domain.db.Moods
import ie.setu.domain.Mood
import ie.setu.domain.repository.MoodDAO
import ie.setu.helpers.*
import org.junit.jupiter.api.Disabled
import kotlin.test.assertEquals

//retrieving some test data from Fixtures
private val mood1 = moods.get(0)
private val mood2 = moods.get(1)
private val mood3 = moods.get(2)

class MoodDAOTest {

    companion object {
        //Make a connection to a local, in memory H2 database.
        @BeforeAll
        @JvmStatic
        internal fun setupInMemoryDatabaseConnection() {
            Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver", user = "root", password = "")
        }
    }

    @Nested
    inner class CreateMoods {

        @Test
        fun `multiple moods added to table can be retrieved successfully`() {
            transaction {
                //Arrange - create and populate tables with three users and three activities
                val userDAO = populateUserTable()
                val moodDAO = populateMoodTable()

                //Act & Assert
                assertEquals(3, moodDAO.getAll().size)
                assertEquals(mood1, moodDAO.findByMoodId(mood1.id))
                assertEquals(mood2, moodDAO.findByMoodId(mood2.id))
                assertEquals(mood3, moodDAO.findByMoodId(mood3.id))
            }
        }
    }

}