package ie.setu.controllers

/**
 * Controller object for sleep logs
 *
 * @author Michael Burke
 */

import ie.setu.domain.Sleep
import ie.setu.domain.User
import ie.setu.domain.Water
import ie.setu.domain.repository.SleepDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.jsonToObject
import ie.setu.utils.sleepInputValidation
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object SleepController {

    private val userDao = UserDAO()
    private val sleepDAO = SleepDAO()

    @OpenApi(
        summary = "Get all sleeps",
        operationId = "getAllSleeps",
        tags = ["Sleep"],
        path = "/api/sleeps",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Sleep>::class)]),
            OpenApiResponse("404", [OpenApiContent(Array<Sleep>::class)]) ]
    )
    fun getAllSleeps(ctx: Context) {
        val sleeps = sleepDAO.getAll()
        if (sleeps.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(sleeps)
    }

    @OpenApi(
        summary = "Get sleeps by user ID",
        operationId = "getSleepsByUserId",
        tags = ["Sleep"],
        path = "/api/users/{user-id}/sleeps",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Sleep::class)]),
            OpenApiResponse("404")]
    )
    fun getSleepsByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val sleeps = sleepDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (sleeps.isNotEmpty()) {
                ctx.json(sleeps)
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
        summary = "Add Sleep",
        operationId = "addSleep",
        tags = ["Sleep"],
        path = "/api/sleeps",
        method = HttpMethod.POST,
        responses  = [OpenApiResponse("201", [OpenApiContent(Sleep::class)]),
            OpenApiResponse("404"),OpenApiResponse("400") ]
    )
    fun addSleep(ctx: Context) {
        val sleep : Sleep = jsonToObject(ctx.body())
        val userId = userDao.findById(sleep.userId)
        if (!sleepInputValidation(sleep)){
            ctx.status(400)
        }
        else{
            if (userId != null) {
                val sleepId = sleepDAO.save(sleep)
                sleep.id = sleepId
                ctx.json(sleep)
                ctx.status(201)
            }
            else{
                ctx.status(404)
            }
        }

    }

    @OpenApi(
        summary = "Get sleep by sleep ID",
        operationId = "getSleepsBySleepId",
        tags = ["Sleep"],
        path = "/api/sleeps/{sleep-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("sleep-id", Int::class, "The sleep ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Sleep::class)]), OpenApiResponse("404")]
    )
    fun getSleepsBySleepId(ctx: Context) {
        val sleep = sleepDAO.findBySleepId((ctx.pathParam("sleep-id").toInt()))
        if (sleep != null){
            ctx.json(sleep)
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
    }

    @OpenApi(
        summary = "Delete sleep by sleep ID",
        operationId = "deleteSleepBySleepId",
        tags = ["Sleep"],
        path = "/api/sleeps/{sleep-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("sleep-id", Int::class, "The sleep ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("404")]
    )
    fun deleteSleepBySleepId(ctx: Context){
        if (sleepDAO.deleteBySleepId(ctx.pathParam("sleep-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Delete sleep by user ID",
        operationId = "deleteSleepByUserId",
        tags = ["Sleep"],
        path = "/api/users/{user-id}/sleeps",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("404")]
    )
    fun deleteSleepByUserId(ctx: Context){
        if (sleepDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
    }

    @OpenApi(
        summary = "Update sleep by sleep ID",
        operationId = "updateSleep",
        tags = ["Sleep"],
        path = "/api/sleeps/{sleep-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("sleep-id", Int::class, "The sleep ID")],
        responses  = [OpenApiResponse("204"), OpenApiResponse("400"), OpenApiResponse("404")]
    )
    fun updateSleep(ctx: Context){
        val sleep : Sleep = jsonToObject(ctx.body())
        if (!sleepInputValidation(sleep)){
            ctx.status(400)
        }
        else {
            if (sleepDAO.updateBySleepId(
                    sleepId = ctx.pathParam("sleep-id").toInt(),
                    sleepToUpdate = sleep
                ) != 0
            )
                ctx.status(204)
            else
                ctx.status(404)
        }
    }

}