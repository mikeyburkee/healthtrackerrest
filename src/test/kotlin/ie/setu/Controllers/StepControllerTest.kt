package ie.setu.Controllers

/**
 * Controller integration test class for step
 *
 * @author Michael Burke
 */


import ie.setu.config.DbConfig
import ie.setu.domain.Step
import ie.setu.domain.User
import ie.setu.domain.Water
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
import org.junit.jupiter.api.Disabled

//retrieving some test data from Fixtures
private val step1 = steps[0]
private val step2 = steps[1]
private val step3 = steps[2]

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Disabled
class StepControllerTest {

    private val db = DbConfig().getDbConnection()
    private val app = ServerContainer.instance
    private val origin = "http://localhost:" + app.port()

    private val testUserExits = deleteTestUser()

    @Nested
    inner class CreateSteps {
        @Test
        fun `add an step when a user exists for it, returns a 201 response`() {

            //Arrange - add a user and an associated step that we plan to do a delete on
            val addedUser: User = jsonToObject(addUser(validName, validEmail, validWeight, validHeight, validAge, validGender).body.toString())

            val addStepResponse = addStep(
                step1.step_count,
                step1.dateEntry, addedUser.id
            )
            assertEquals(201, addStepResponse.status)

            //After - delete the user (Step will cascade delete in the database)
            deleteUser(addedUser.id)
        }

        @Test
        fun `add an step when no user exists for it, returns a 404 response`() {

            //Arrange - check there is no user for -1 id
            val userId = -1
            assertEquals(404, retrieveUserById(userId).status)

            val addStepResponse = addStep(
                step1.step_count,
                step1.dateEntry, userId
            )
            assertEquals(404, addStepResponse.status)

        }
    }

    @Nested
    inner class ReadSteps {

        @Test
        fun `get all steps from the database returns 200 or 404 response`() {
            val response = retrieveAllSteps()
            if (response.status == 200){
                val retrievedSteps = jsonNodeToObject<Array<Step>>(response)
                assertNotEquals(0, retrievedSteps.size)
            }
            else{
                assertEquals(404, response.status)
            }
        }

        @Test
        fun `get all steps by user id when user and steps exists returns 200 response`() {
            //Arrange - add a user and 3 associated steps that we plan to retrieve
            val addedUser : User = jsonToObject(addUser(validName, validEmail, validWeight, validHeight, validAge, validGender).body.toString())
            addStep(
                step1.step_count,
                step1.dateEntry, addedUser.id)
            addStep(
                step2.step_count,
                step2.dateEntry, addedUser.id)
            addStep(
                step3.step_count,
                step3.dateEntry, addedUser.id)

            //Assert and Act - retrieve the three added steps by user id
            val response = retrieveStepsByUserId(addedUser.id)
            assertEquals(200, response.status)
            val retrievedSteps = jsonNodeToObject<Array<Step>>(response)
            assertEquals(3, retrievedSteps.size)

            //After - delete the added user and assert a 204 is returned (steps are cascade deleted)
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all steps by user id when no steps exist returns 404 response`() {
            //Arrange - add a user
            val addedUser : User = jsonToObject(addUser(validName, validEmail, validWeight, validHeight, validAge, validGender).body.toString())

            //Assert and Act - retrieve the steps by user id
            val response = retrieveStepsByUserId(addedUser.id)
            assertEquals(404, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

        @Test
        fun `get all steps by user id when no user exists returns 404 response`() {
            //Arrange
            val userId = -1

            //Assert and Act - retrieve steps by user id
            val response = retrieveStepsByUserId(userId)
            assertEquals(404, response.status)
        }

        @Test
        fun `get step by step id when no step exists returns 404 response`() {
            //Arrange
            val stepId = -1
            //Assert and Act - attempt to retrieve the step by step id
            val response = retrieveStepByStepId(stepId)
            assertEquals(404, response.status)
        }


        @Test
        fun `get step by step id when step exists returns 200 response`() {
            //Arrange - add a user and associated step
            val addedUser : User = jsonToObject(addUser(validName, validEmail, validWeight, validHeight, validAge, validGender).body.toString())
            val addStepResponse = addStep(
                step1.step_count,
                step1.dateEntry, addedUser.id)
            assertEquals(201, addStepResponse.status)
            val addedStep = jsonNodeToObject<Step>(addStepResponse)

            //Act & Assert - retrieve the step by step id
            val response = retrieveStepByStepId(addedStep.id)
            assertEquals(200, response.status)

            //After - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)
        }

    }

    @Nested
    inner class UpdateSteps {

        @Test
        fun `updating an step by step id when it doesn't exist, returns a 404 response`() {
            val userId = -1
            val stepID = -1

            //Arrange - check there is no user for -1 id
            assertEquals(404, retrieveUserById(userId).status)

            //Act & Assert - attempt to update the details of an step/user that doesn't exist
            assertEquals(
                404, updateStep(
                    stepID, updatedStep_Count,
                    updatedDateTime, userId
                ).status
            )
        }

        @Test
        fun `updating an step by step id when it exists, returns 204 response`() {

            //Arrange - add a user and an associated step that we plan to do an update on
            val addedUser : User = jsonToObject(addUser(validName, validEmail, validWeight, validHeight, validAge, validGender).body.toString())
            val addStepResponse = addStep(
                step1.step_count,
                step1.dateEntry, addedUser.id)
            assertEquals(201, addStepResponse.status)
            val addedStep = jsonNodeToObject<Step>(addStepResponse)

            //Act & Assert - update the added step and assert a 204 is returned
            val updatedStepResponse = updateStep(addedStep.id,
                updatedStep_Count,updatedDateTime, addedUser.id)
            assertEquals(204, updatedStepResponse.status)

            //Assert that the individual fields were all updated as expected
            val retrievedStepResponse = retrieveStepByStepId(addedStep.id)
            val updatedStep = jsonNodeToObject<Step>(retrievedStepResponse)
            assertEquals(updatedStep_Count, updatedStep.step_count)
            assertEquals(updatedDateTime, updatedStep.dateEntry )

            //After - delete the user
            deleteUser(addedUser.id)
        }
    }

    @Nested
    inner class DeleteSteps {

        @Test
        fun `deleting an step by step id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete a step  that doesn't exist
            assertEquals(404, deleteStepByStepId(-1).status)
        }

        @Test
        fun `deleting steps by user id when it doesn't exist, returns a 404 response`() {
            //Act & Assert - attempt to delete steps by a user that that doesn't exist
            assertEquals(404, deleteStepsByUserId(-1).status)
        }

        @Test
        fun `deleting an step by id when it exists, returns a 204 response`() {

            //Arrange - add a user and an associated step that we plan to do a delete on
            val addedUser : User = jsonToObject(addUser(validName, validEmail, validWeight, validHeight, validAge, validGender).body.toString())
            val addStepResponse = addStep(
                step1.step_count,
                step1.dateEntry, addedUser.id)
            assertEquals(201, addStepResponse.status)

            //Act & Assert - delete the added step and assert a 204 is returned
            val addedStep = jsonNodeToObject<Step>(addStepResponse)
            assertEquals(204, deleteStepByStepId(addedStep.id).status)

            //After - delete the user
            deleteUser(addedUser.id)
        }

        @Test
        fun `deleting all steps by userid when it exists, returns a 204 response`() {

            //Arrange - add a user and 3 associated steps that we plan to do a cascade delete
            val addedUser : User = jsonToObject(addUser(validName, validEmail, validWeight, validHeight, validAge, validGender).body.toString())
            val addStepResponse1 = addStep(
                step1.step_count,
                step1.dateEntry, addedUser.id)
            assertEquals(201, addStepResponse1.status)
            val addStepResponse2 = addStep(
                step2.step_count,
                step2.dateEntry, addedUser.id)
            assertEquals(201, addStepResponse2.status)
            val addStepResponse3 = addStep(
                step3.step_count,
                step3.dateEntry, addedUser.id)
            assertEquals(201, addStepResponse3.status)

            //Act & Assert - delete the added user and assert a 204 is returned
            assertEquals(204, deleteUser(addedUser.id).status)

            //Act & Assert - attempt to retrieve the deleted steps
            val addedStep1 = jsonNodeToObject<Step>(addStepResponse1)
            val addedStep2 = jsonNodeToObject<Step>(addStepResponse2)
            val addedStep3 = jsonNodeToObject<Step>(addStepResponse3)
            assertEquals(404, retrieveStepByStepId(addedStep1.id).status)
            assertEquals(404, retrieveStepByStepId(addedStep2.id).status)
            assertEquals(404, retrieveStepByStepId(addedStep3.id).status)
        }
    }

    //helper function to retrieve all steps
    private fun retrieveAllSteps(): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/steps").asJson()
    }

    //helper function to retrieve steps by user id
    private fun retrieveStepsByUserId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/users/${id}/steps").asJson()
    }

    //helper function to retrieve step by step id
    private fun retrieveStepByStepId(id: Int): HttpResponse<JsonNode> {
        return Unirest.get(origin + "/api/steps/${id}").asJson()
    }

    //helper function to delete a step by step id
    private fun deleteStepByStepId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/steps/$id").asString()
    }

    //helper function to delete a step by step id
    private fun deleteStepsByUserId(id: Int): HttpResponse<String> {
        return Unirest.delete(origin + "/api/users/$id/steps").asString()
    }

    //helper function to add a test user to the database
    private fun updateStep(
        id: Int, step_count: Int,
        dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.patch(origin + "/api/steps/$id")
            .body("""
                {
                  "step_count":$step_count,
                  "dateEntry":"$dateEntry",
                  "userId":$userId
                }
            """.trimIndent()).asJson()
    }

    //helper function to add a step
    private fun addStep(
        step_count: Int,
        dateEntry: DateTime, userId: Int): HttpResponse<JsonNode> {
        return Unirest.post(origin + "/api/steps")
            .body("""
                {
                   "step_count":$step_count,
                   "dateEntry":"$dateEntry",
                   "userId":$userId
                }
            """.trimIndent())
            .asJson()
    }

}