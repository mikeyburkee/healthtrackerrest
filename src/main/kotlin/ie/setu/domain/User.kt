package ie.setu.domain

/**
 * Data class for the User
 *
 * @author Michael Burke
 */
data class User (var id: Int,
                 var name: String,
                 var email: String,
                 var weight: Double,
                 var height: Double,
                 var age: Int,
                 var gender: String)