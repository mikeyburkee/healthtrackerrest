package ie.setu.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.mapJSONDate
import io.javalin.http.Context
import io.javalin.plugin.openapi.annotations.*

object ActivityController {

    private val userDao = UserDAO()
    private val activityDAO = ActivityDAO()

    @OpenApi(
        summary = "Get all activities",
        operationId = "getAllActivities",
        tags = ["Activity"],
        path = "/api/activities",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivities(ctx: Context) {
        //mapper handles the deserialization of Joda date into a String.
        val mapper = mapJSONDate()
        ctx.json(mapper.writeValueAsString( activityDAO.getAll() ))

    }

    @OpenApi(
        summary = "Get activities by user ID",
        operationId = "getActivitiesByUserId",
        tags = ["Activity"],
        path = "/api/users/{user-id}/activities",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Activity::class)])]
    )
    fun getActivitiesByUserId(ctx: Context) {
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {
            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                //mapper handles the deserialization of Joda date into a String.
                val mapper = mapJSONDate()
                ctx.json(mapper.writeValueAsString(activities))
            }
        }
    }

    @OpenApi(
        summary = "Add Activity",
        operationId = "addActivity",
        tags = ["Activity"],
        path = "/api/activities",
        method = HttpMethod.POST,
        //pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200")]
    )
    fun addActivity(ctx: Context) {
        //mapper handles the serialisation of Joda date into a String.
        val mapper = mapJSONDate()

        val activity = mapper.readValue<Activity>(ctx.body())
        activityDAO.save(activity)
        ctx.json(activity)
    }

    @OpenApi(
        summary = "Get activity by activity ID",
        operationId = "getActivitiesByActivityId",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(User::class)])]
    )
    fun getActivitiesByActivityId(ctx: Context) {
        val activity = activityDAO.findByActivityId((ctx.pathParam("activity-id").toInt()))
        if (activity != null){
            val mapper = mapJSONDate()
            ctx.json(mapper.writeValueAsString(activity))
        }
    }

    @OpenApi(
        summary = "Delete activity by activity ID",
        operationId = "deleteActivityByActivityId",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteActivityByActivityId(ctx: Context){
        activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt())
    }

    @OpenApi(
        summary = "Delete activity by user ID",
        operationId = "deleteActivityByUserId",
        tags = ["Activity"],
        path = "/api/users/{user-id}/activities",
        method = HttpMethod.DELETE,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun deleteActivityByUserId(ctx: Context){
        activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt())
    }

    @OpenApi(
        summary = "Update activity by activity ID",
        operationId = "updateActivity",
        tags = ["Activity"],
        path = "/api/activities/{activity-id}",
        method = HttpMethod.PATCH,
        pathParams = [OpenApiParam("activity-id", Int::class, "The activity ID")],
        responses  = [OpenApiResponse("204")]
    )
    fun updateActivity(ctx: Context){
        val mapper = mapJSONDate()
        val activity = mapper.readValue<Activity>(ctx.body())
            activityDAO.updateByActivityId(
            activityId = ctx.pathParam("activity-id").toInt(),
            activityDTO=activity)
    }
}