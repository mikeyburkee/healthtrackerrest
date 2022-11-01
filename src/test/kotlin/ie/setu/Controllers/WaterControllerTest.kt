package ie.setu.Controllers

import ie.setu.config.DbConfig
import ie.setu.domain.Water
import ie.setu.domain.User
import ie.setu.helpers.*
import ie.setu.utils.jsonNodeToObject
import ie.setu.utils.jsonToObject
import kong.unirest.HttpResponse
import kong.unirest.JsonNode
import kong.unirest.Unirest
import org.joda.time.DateTime
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

//retrieving some test data from Fixtures
private val water1 = waters.get(0)
private val water2 = waters.get(1)
private val water3 = waters.get(2)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WaterControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    @Nested
    inner class CreateWaters {

        @Test
        fun `add an water when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated water that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail).body.toString())

            val addWaterResponse = addWater(
                waters[0].volume,
                waters[0].dateEntry, addedUser.id
            )
            assertEquals(201, addWaterResponse.status)

            //After - delete the user (Water will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add an water when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addWaterResponse = addWater(
                waters.get(0).volume,
                waters.get(0).dateEntry, userId
            )
            assertEquals(404, addWaterResponse.status)

        }
    }

    @Nested
    inner class ReadWaters {

        @Test
        fun `get all waters from the database returns 200 or 404 response`() {
            val response = retrieveAllWaters()
            if (response.status == 200){
                val retrievedWaters = jsonNodeToObject<Array<Water>>(response)
                assertNotEquals(0, retrievedWaters.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all waters by user id when user and waters exists returns 200 response`() {
            //Arrange - add a user and 3 associated waters that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            addWater(
                waters[0].volume,
                waters[0].dateEntry, addedUser.id)
            addWater(
                waters[1].volume,
                waters[1].dateEntry, addedUser.id)
            addWater(
                waters[2].volume,
                waters[2].dateEntry, addedUser.id)

            //Assert and Act - retrieve the three added waters by user id
            val response = retrieveWatersByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedWaters = jsonNodeToObject<Array<Water>>(response)
            assertEquals(3, retrievedWaters.size)

            //After - delete the added user and assert a 204 is returned (waters are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all waters by user id when no waters exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())

            //Assert and Act - retrieve the waters by user id
            val response = retrieveWatersByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all waters by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve waters by user id
            val response = retrieveWatersByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get water by water id when no water exists returns 404 response`() {
            //Arrange
            val waterId = -1
            //Assert and Act - attempt to retrieve the water by water id
            val response = retrieveWaterByWaterId(waterId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get water by water id when water exists returns 200 response`() {
            //Arrange - add a user and associated water
            val addedUser : User = jsonToObject(addUser(validName, validEmail).body.toString())
            val addWaterResponse = addWater(
                waters[0].volume, 
                waters[0].dateEntry, addedUser.id)
            assertEquals(201, addWaterResponse.status)
            val addedWater = jsonNodeToObject<Water>(addWaterResponse)

            //Act & Assert - retrieve the water by water id
            val response = retrieveWaterByWaterId(addedWater.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    //helper function to retrieve all waters
    private fun retrieveAllWaters(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/waters").asJson()
    }

    //helper function to retrieve waters by user id
    private fun retrieveWatersByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/waters").asJson()
    }

    //helper function to retrieve water by water id
    private fun retrieveWaterByWaterId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/waters/${id}").asJson()
    }

    //helper function to delete an water by water id
    private fun deleteWaterByWaterId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/waters/$id").asString()
    }

    //helper function to delete an water by water id
    private fun deleteWatersByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/waters").asString()
    }

    //helper function to add a test user to the database
    private fun updateWater(id: Int, volume: Double,
                            dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/waters/$id")
            .body("""
                {
                  "volume":$volume,
                  "dateEntry":"$dateEntry",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to add a water
    private fun addWater(volume: Double,
                         dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/waters")
            .body("""
                {
                   "volume":$volume,
                   "dateEntry":"$dateEntry",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }
    
}