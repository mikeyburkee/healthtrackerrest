package ie.setu.Controllers

import ie.setu.config.DbConfig
import ie.setu.controllers.SleepController.addSleep
import ie.setu.domain.Sleep
import ie.setu.domain.User
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//retrieving some test data from Fixtures
private val sleep1 = sleeps.get(0)
private val sleep2 = sleeps.get(1)
private val sleep3 = sleeps.get(2)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SleepControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class CreateSleeps {

        @Test
        fun `add an sleep when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated sleep that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addSleepResponse = addSleep(
                sleeps[0].description, sleeps[0].duration,
                sleeps[0].rating, sleeps[0].wakeUpTime, addedUser.id
            )
            assertEquals(201, addSleepResponse.status)

            //After - delete the user (Sleep will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add an sleep when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addSleepResponse = addSleep(
                sleeps.get(0).description, sleeps.get(0).duration,
                sleeps.get(0).rating, sleeps.get(0).wakeUpTime, userId
            )
            assertEquals(404, addSleepResponse.status)

        }
    }

    //helper function to retrieve all sleeps
    private fun retrieveAllSleeps(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/sleeps").asJson()
    }

    //helper function to retrieve sleeps by user id
    private fun retrieveSleepsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/sleeps").asJson()
    }

    //helper function to retrieve sleep by sleep id
    private fun retrieveSleepBySleepId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/sleeps/${id}").asJson()
    }

    //helper function to delete an sleep by sleep id
    private fun deleteSleepBySleepId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/sleeps/$id").asString()
    }

    //helper function to delete an sleep by sleep id
    private fun deleteSleepsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/sleeps").asString()
    }

    //helper function to add a test user to the database
    private fun updateSleep(id: Int, description: String, duration: Double, rating: Int,
                            wakeUpTime: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/sleeps/$id")
            .body("""
                {
                  "description":"$description",
                  "duration":$duration,
                  "rating":$rating,
                  "wakeUpTime":"$wakeUpTime",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to add an sleep
    private fun addSleep(description: String, duration: Double, rating: Int,
                            wakeUpTime: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/sleeps")
            .body("""
                {
                   "description":"$description",
                   "duration":$duration,
                   "rating":$rating,
                   "wakeUpTime":"$wakeUpTime",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }
}