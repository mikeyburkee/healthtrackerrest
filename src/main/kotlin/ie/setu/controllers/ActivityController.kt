package ie.setu.controllers

import ie.setu.domain.Activity
import ie.setu.domain.User
import ie.setu.domain.repository.ActivityDAO
import ie.setu.domain.repository.UserDAO
import ie.setu.utils.activityInputValidation
import ie.setu.utils.jsonToObject
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
        val activities = activityDAO.getAll()
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
    }

    @OpenApi(
        summary = "Get all activities sorted by parameter input in route ",
        operationId = "getAllActivitiesSorted",
        tags = ["Activity"],
        path = "/api/activities/ascending/{sort-param}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("sort-param", String::class, "The parameter to sort")],
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivitiesSortedAscending(ctx: Context) {
        val activities = activityDAO.getAllSortedAscending(ctx.pathParam("sort-param"))
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
    }

    @OpenApi(
        summary = "Get all activities sorted by parameter descending input in route ",
        operationId = "getAllActivitiesSortedDescending",
        tags = ["Activity"],
        path = "/api/activities/descending/{sort-param}",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("sort-param", String::class, "The parameter to sort")],
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivitiesSortedDescending(ctx: Context) {
        val activities = activityDAO.getAllSortedDescending(ctx.pathParam("sort-param"))
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
    }

    @OpenApi(
        summary = "Get all activities sorted by ascending duration  ",
        operationId = "getAllActivitiesSortedAscendingByDuration",
        tags = ["Activity"],
        path = "/api/activities/ascending/duration",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivitiesSortedAscendingByDuration(ctx: Context) {
        val activities = activityDAO.getAllSortedAscending("duration")
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
    }

    @OpenApi(
        summary = "Get all activities sorted by descending duration  ",
        operationId = "getAllActivitiesSortedDescendingByDuration",
        tags = ["Activity"],
        path = "/api/activities/descending/duration",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivitiesSortedDescendingByDuration(ctx: Context) {
        val activities = activityDAO.getAllSortedDescending("duration")
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
    }

    @OpenApi(
        summary = "Get all activities sorted by ascending calories  ",
        operationId = "getAllActivitiesSortedAscendingByCalories",
        tags = ["Activity"],
        path = "/api/activities/ascending/calories",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivitiesSortedAscendingByCalories(ctx: Context) {
        val activities = activityDAO.getAllSortedAscending("calories")
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
    }

    @OpenApi(
        summary = "Get all activities sorted by descending calories  ",
        operationId = "getAllActivitiesSortedDescendingByCalories",
        tags = ["Activity"],
        path = "/api/activities/descending/calories",
        method = HttpMethod.GET,
        responses = [OpenApiResponse("200", [OpenApiContent(Array<Activity>::class)])]
    )
    fun getAllActivitiesSortedDescendingByCalories(ctx: Context) {
        val activities = activityDAO.getAllSortedDescending("calories")
        if (activities.size != 0) {
            ctx.status(200)
        }
        else{
            ctx.status(404)
        }
        ctx.json(activities)
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
                ctx.json(activities)
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


    /*
    //TODO fix issue with  activities by a singular user
    @OpenApi(
        summary = "Get activities by user ID in duration ascending order",
        operationId = "getActivitiesByUserIdInDurationAscendingOrder",
        tags = ["Activity"],
        path = "/api/users/{user-id}/activities",
        method = HttpMethod.GET,
        pathParams = [OpenApiParam("user-id", Int::class, "The user ID")],
        responses  = [OpenApiResponse("200", [OpenApiContent(Activity::class)])]
    )
    fun getActivitiesByUserIdWithDurationSorted(ctx: Context){
        if (userDao.findById(ctx.pathParam("user-id").toInt()) != null) {

            val activities = activityDAO.findByUserId(ctx.pathParam("user-id").toInt())
            if (activities.isNotEmpty()) {
                ctx.json(activities)
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
     */

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
        val activity : Activity = jsonToObject(ctx.body())
        val userId = userDao.findById(activity.userId)
        if (!activityInputValidation(activity)){
            ctx.status(400)
        }
        else {
            if (userId != null) {
                val activityId = activityDAO.save(activity)
                activity.id = activityId
                ctx.json(activity)
                ctx.status(201)
            } else {
                ctx.status(404)
            }
        }
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
            ctx.json(activity)
            ctx.status(200)
        }
        else{
            ctx.status(404)
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
        if (activityDAO.deleteByActivityId(ctx.pathParam("activity-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
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
        if (activityDAO.deleteByUserId(ctx.pathParam("user-id").toInt()) != 0)
            ctx.status(204)
        else
            ctx.status(404)
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
        val activity : Activity = jsonToObject(ctx.body())
        if (!activityInputValidation(activity)){
            ctx.status(200)
        }
        else {
            if (activityDAO.updateByActivityId(
                    activityId = ctx.pathParam("activity-id").toInt(),
                    activityToUpdate = activity
                ) != 0
            )
                ctx.status(204)
            else
                ctx.status(404)
        }
    }

}