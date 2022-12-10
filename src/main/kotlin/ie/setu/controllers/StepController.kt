package ie.setu.controllers

/**
 * Controller object for step logs
 *
 * @author Michael Burke
 */

import ie.setu.domain.Step
import ie.setu.domain.repository.StepDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.*
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object StepController{

    private val userDao = UserDAO()
    private val stepDAO = StepDAO()

    @OpenApi(
        summary = "Get all steps",
        operationId = "getAllSteps",
        tags = ["Step"],
        path = "/api/steps",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Step>::class)]),
            OpenApiResponse("404", [OpenApiContent(Array<Step>::class)]) ]
    )
    fun getAllSteps(ctx: Context) {
        val steps = stepDAO.getAll()
        if (steps.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(steps)
    }

    @OpenApi(
        summary = "Get steps by user ID",
        operationId = "getStepsByUserId",
        tags = ["Step"],
        path = "/api/users/{user-id}/steps",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Step::class)]),
            OpenApiResponse("404")]
    )
    fun getStepsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val steps = stepDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (steps.isNotEmpty()) {
                ctx.json(steps)
                ctx.status(200)
            }
            else{
                ctx.status(404)
            }
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Add Step",
        operationId = "addStep",
        tags = ["Step"],
        path = "/api/steps",
        method = HttpMethod.POST,
        responses  = [OpenApiResponse("201", [OpenApiContent(Step::class)]),
            OpenApiResponse("404"),OpenApiResponse("400") ]
    )
    fun addStep(ctx: Context) {
        val step : Step = jsonToObject(ctx.body())
        val userId = userDao.findById(step.userId)
        if (!stepInputValidation(step)){
            ctx.status(400)
        }
        else {
            if (userId != null) {
                val stepId = stepDAO.save(step)
                step.id = stepId
                ctx.json(step)
                ctx.status(201)
            }
            else{
                ctx.status(404)
            }
        }
    }

    @OpenApi(
        summary = "Get step by step ID",
        operationId = "getStepsByStepId",
        tags = ["Step"],
        path = "/api/steps/{step-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("step-id", Int::class, "The step ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Step::class)]), OpenApiResponse("404")]
    )
    fun getStepsByStepId(ctx: Context) {
        val step = stepDAO.findByStepId((ctx.pathParam("step-id").toInt()))
        if (step != null){
            ctx.json(step)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete step by step ID",
        operationId = "deleteStepByStepId",
        tags = ["Step"],
        path = "/api/steps/{step-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("step-id", Int::class, "The step ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("404")]
    )
    fun deleteStepByStepId(ctx: Context){
        if (stepDAO.deleteByStepId(ctx.pathParam("step-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete step by user ID",
        operationId = "deleteStepByUserId",
        tags = ["Step"],
        path = "/api/users/{user-id}/steps",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("404")]
    )
    fun deleteStepByUserId(ctx: Context){
        if (stepDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update step by step ID",
        operationId = "updateStep",
        tags = ["Step"],
        path = "/api/steps/{step-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("step-id", Int::class, "The step ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("400"), OpenApiResponse("404")]
    )
    fun updateStep(ctx: Context){
        val step : Step = jsonToObject(ctx.body())
        if (!stepInputValidation(step)){
            ctx.status(400)
        }
        else {
            if (stepDAO.updateByStepId(
                    stepsId = ctx.pathParam("step-id").toInt(),
                    stepsToUpdate = step
                ) != 0
            )
                ctx.status(204)
            else
                ctx.status(404)
        }
    }

}