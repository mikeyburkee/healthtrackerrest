package ie.setu.helpers

import ie.setu.domain.*
import ie.setu.domain.db.*
import ie.setu.domain.repository.*
import ie.setu.repository.user1
import ie.setu.repository.user2
import ie.setu.repository.user3
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"

val updatedName = "Updated Name"
val updatedEmail = "Updated Email"

val updatedDescription = "Updated Description"
val updatedDuration = 3.66
val updatedCalories = 945
val updatedStarted = DateTime.parse("2020-06-11T05:59:27.258Z")

val updatedRating = 1
val updatedDateEntry = updatedStarted

val updatedWakeUpTime = updatedStarted

val updatedVolume = 2.75

val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4)
)

val activities = arrayListOf<Activity>(
    Activity(id = 1, description = "Running", duration = 22.0, calories = 230, started = DateTime.now(), userId = 1),
    Activity(id = 2, description = "Hopping", duration = 10.5, calories = 80, started = DateTime.now(), userId = 1),
    Activity(id = 3, description = "Walking", duration = 12.0, calories = 120, started = DateTime.now(), userId = 2)
)

val moods = arrayListOf<Mood>(
    Mood(id = 1, description = "Happy", rating = 7, dateEntry = DateTime.now(), userId = 1),
    Mood(id = 2, description = "Ok", rating = 6, dateEntry = DateTime.now(), userId = 1),
    Mood(id = 3, description = "Over the moon", rating = 9, dateEntry = DateTime.now(), userId = 2)
)

val sleeps = arrayListOf<Sleep>(
    Sleep(id = 1, description = "Unbroken", duration = 6.0, rating = 7, wakeUpTime = DateTime.now(), userId = 1),
    Sleep(id = 2, description = "Bad", duration = 4.5, rating = 4, wakeUpTime = DateTime.now(), userId = 1),
    Sleep(id = 3, description = "Fantastic", duration = 8.0, rating = 9, wakeUpTime = DateTime.now(), userId = 2)
)

val waters = arrayListOf<Water>(
    Water(id = 1, volume = 1.0, dateEntry = DateTime.now(), userId = 1),
    Water(id = 2, volume = 2.5, dateEntry = DateTime.now(), userId = 1),
    Water(id = 3, volume = 5.0, dateEntry =  DateTime.now(), userId = 2)
)

fun populateUserTable(): UserDAO {
    SchemaUtils.create(Users)
    val userDAO = UserDAO()
    userDAO.save(users.get(0))
    userDAO.save(users.get(1))
    userDAO.save(users.get(2))
    return userDAO
}
fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities.get(0))
    activityDAO.save(activities.get(1))
    activityDAO.save(activities.get(2))
    return activityDAO
}

fun populateMoodTable(): MoodDAO {
    SchemaUtils.create(Moods)
    val moodDAO = MoodDAO()
    moodDAO.save(moods.get(0))
    moodDAO.save(moods.get(1))
    moodDAO.save(moods.get(2))
    return moodDAO
}

fun populateSleepTable(): SleepDAO {
    SchemaUtils.create(Sleeps)
    val sleepDAO = SleepDAO()
    sleepDAO.save(sleeps.get(0))
    sleepDAO.save(sleeps.get(1))
    sleepDAO.save(sleeps.get(2))
    return sleepDAO
}

fun populateWaterTable(): WaterDAO {
    SchemaUtils.create(Waters)
    val waterDAO = WaterDAO()
    waterDAO.save(waters.get(0))
    waterDAO.save(waters.get(1))
    waterDAO.save(waters.get(2))
    return waterDAO
}