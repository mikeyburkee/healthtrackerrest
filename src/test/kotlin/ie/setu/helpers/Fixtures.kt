package ie.setu.helpers

import ie.setu.domain.*
import ie.setu.domain.db.*
import ie.setu.domain.repository.*
import org.jetbrains.exposed.sql.SchemaUtils
import org.joda.time.DateTime

val nonExistingEmail = "112233445566778testUser@xxxxx.xx"
val validName = "Test User 1"
val validEmail = "testuser1@test.com"

val updatedName = "Updated Name"
val updatedEmail = "updated@email.com"
val updatedWeight = 99.9
val updatedHeight = 1.99
val updatedAge = 99
val updatedGender = "other"

val updatedDescription = "Updated Description"
val updatedDuration = 3.66
val updatedCalories = 945
val updatedDateTime = DateTime.parse("2020-06-11T05:59:27.258Z")

val updatedRating = 1

val updatedVolume = 2.75

val users = arrayListOf<User>(
    User(name = "Alice Wonderland", email = "alice@wonderland.com", id = 1, weight = 66.0 , height = 1.6, age = 20, gender = "female"),
    User(name = "Bob Cat", email = "bob@cat.ie", id = 2, weight = 99.2 , height = 1.8, age = 65, gender = "male"),
    User(name = "Mary Contrary", email = "mary@contrary.com", id = 3, weight = 80.3 , height = 1.65, age = 40, gender = "female"),
    User(name = "Carol Singer", email = "carol@singer.com", id = 4, weight = 77.0 , height = 1.5, age = 25, gender = "female")
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
    userDAO.save(users[0])
    userDAO.save(users[1])
    userDAO.save(users[2])
    return userDAO
}

fun populateActivityTable(): ActivityDAO {
    SchemaUtils.create(Activities)
    val activityDAO = ActivityDAO()
    activityDAO.save(activities[0])
    activityDAO.save(activities[1])
    activityDAO.save(activities[2])
    return activityDAO
}

fun populateMoodTable(): MoodDAO {
    SchemaUtils.create(Moods)
    val moodDAO = MoodDAO()
    moodDAO.save(moods[0])
    moodDAO.save(moods[1])
    moodDAO.save(moods[2])
    return moodDAO
}

fun populateSleepTable(): SleepDAO {
    SchemaUtils.create(Sleeps)
    val sleepDAO = SleepDAO()
    sleepDAO.save(sleeps[0])
    sleepDAO.save(sleeps[1])
    sleepDAO.save(sleeps[2])
    return sleepDAO
}

fun populateWaterTable(): WaterDAO {
    SchemaUtils.create(Waters)
    val waterDAO = WaterDAO()
    waterDAO.save(waters[0])
    waterDAO.save(waters[1])
    waterDAO.save(waters[2])
    return waterDAO
}