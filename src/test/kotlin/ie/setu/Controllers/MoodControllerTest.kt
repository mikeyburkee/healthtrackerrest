package ie.setu.Controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Mood
import ie.setu.domain.User
import ie.setu.helpers.ServerContainer
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//retrieving some test data from Fixtures
private val activity1 = moods.get(0)
private val activity2 = moods.get(1)
private val activity3 = moods.get(2)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MoodControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class CreateActivities {

        @Test
        fun `add an activity when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated activity that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addMoodResponse = addMood(
                moods[0].description, moods[0].rating,
                moods[0].dateEntry, addedUser.id
            )
            assertEquals(201, addMoodResponse.status)

            //After - delete the user (Activity will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add a mood when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addMoodResponse = addMood(
                moods.get(0).description, moods.get(0).rating,
                moods.get(0).dateEntry, userId
            )
            assertEquals(404, addMoodResponse.status)

        }
    }

    //helper function to retrieve all moods
    private fun retrieveAllMoods(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/moods").asJson()
    }

    //helper function to retrieve moods by user id
    private fun retrieveMoodsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/moods").asJson()
    }

    //helper function to retrieve mood by mood id
    private fun retrieveMoodByMoodId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/moods/${id}").asJson()
    }

    //helper function to delete a mood by mood id
    private fun deleteMoodByMoodId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/moods/$id").asString()
    }

    //helper function to delete a mood by mood id
    private fun deleteMoodsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/moods").asString()
    }

    //helper function to add a test user to the database
    private fun updateMood(id: Int, description: String, rating: Int,
                           dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/moods/$id")
            .body("""
                {
                  "description":"$description",
                  "rating":$rating,
                  "dateEntry":"$dateEntry",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to add a mood
    private fun addMood(description: String, rating: Int,
                            dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/moods")
            .body("""
                {
                   "description":"$description",
                   "rating":$rating,
                   "dateEntry":"$dateEntry",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }
}