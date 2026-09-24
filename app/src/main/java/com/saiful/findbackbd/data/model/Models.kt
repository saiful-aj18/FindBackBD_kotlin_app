package com.saiful.findbackbd.data.model

enum class Role { USER, ADMIN }

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: Role = Role.USER
)

data class LostFoundItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val isLost: Boolean = true,
    val place: String = "",
    val time: String = "",
    val distance: String = "",
    val description: String = "",
    val contact: String = "",
    // --- newly added fields (were missing, caused the unresolved-reference errors) ---
    val date: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownerId: String = "",
    val updatedAt: Long = 0L
)

data class Message(
    val text: String = "",
    val time: String = "",
    val mine: Boolean = false
)

data class AppNotification(
    val title: String = "",
    val body: String = "",
    val time: String = "",
    val type: String = ""
)

/** UI demo data. Replace with Repository (Firestore + Room) later. */
object SampleData {
    val categories = listOf("Mobile", "Bag", "Wallet", "Keys", "Documents", "Electronics", "Clothes", "Others")
    val items = listOf(
        LostFoundItem(id = "1", name = "iPhone 13", category = "Mobile", isLost = false, place = "Dhanmondi", time = "4h ago", distance = "3 km", description = "iPhone 13 with a blue case. Found near Dhanmondi 27 bus stop.", contact = "Rifat Hasan"),
        LostFoundItem(id = "2", name = "Black Backpack", category = "Bag", isLost = true, place = "University", time = "2h ago", distance = "2 km", description = "Black backpack with a laptop inside. Lost near the library.", contact = "Nusrat Jahan"),
        LostFoundItem(id = "3", name = "Blue Wallet", category = "Wallet", isLost = true, place = "Gulshan", time = "5h ago", distance = "5 km", description = "Blue leather wallet with NID and cards.", contact = "Tanvir Ahmed"),
        LostFoundItem(id = "4", name = "Keys", category = "Keys", isLost = false, place = "Mirpur", time = "6h ago", distance = "6 km", description = "Bunch of 3 keys with a red keychain.", contact = "Rifat Hasan")
    )
    val users = listOf(
        User(id = "u1", name = "Rifat Hasan", email = "rifat@example.com"),
        User(id = "u2", name = "Arafat Rahman", email = "arafat@example.com"),
        User(id = "u3", name = "Nusrat Jahan", email = "nusrat@example.com"),
        User(id = "u4", name = "Tanvir Ahmed", email = "tanvir@example.com")
    )
    val messages = listOf(
        Message("Hi! Is this your phone?", "10:24 AM", false), Message("Yes! This is my phone. Where did you find it?", "10:26 AM", true),
        Message("I found it near Dhanmondi 27. Can we meet there?", "10:27 AM", false), Message("Sure. Thank you so much!", "10:28 AM", true)
    )
    val notifications = listOf(
        AppNotification("Possible Match Found", "Your lost item matches with a found item.", "2h ago", "match"),
        AppNotification("New Message", "Arafat Rahman sent you a message.", "3h ago", "message"),
        AppNotification("Report Status Updated", "Your report status changed to Matched.", "5h ago", "status"),
        AppNotification("New Match", "A new possible match is available for you.", "6h ago", "match"),
        AppNotification("Welcome to FindBack BD", "Thank you for joining our community!", "1d ago", "welcome")
    )
}