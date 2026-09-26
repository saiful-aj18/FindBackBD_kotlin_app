package com.saiful.findbackbd.data.model

enum class Role {
    USER,
    ADMIN
}

data class LostFoundItem(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val isLost: Boolean = true,
    val place: String = "",
    val time: String = "",
    val distance: String = "1.2 km",
    val description: String = "",
    val contact: String = "",
    val contactPhone: String = "+880 1712 345678",
    val contactEmail: String = "",
    val date: String = "Today",
    val latitude: Double = 23.7808,
    val longitude: Double = 90.4071,
    val ownerId: String = "",
    val imageUrl: String = "",
    val isResolved: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "+880 1712 345678",
    val role: Role = Role.USER,
    val isBlocked: Boolean = false,
    val avatarUrl: String = ""
)

data class ChatThread(
    val id: String = "",
    val participantName: String = "",
    val participantPhone: String = "+880 1712 345678",
    val itemId: String = "",
    val itemName: String = "",
    val lastMessage: String = "",
    val lastTime: String = "",
    val unreadCount: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

data class Message(
    val id: String = "",
    val chatId: String = "",
    val senderName: String = "",
    val text: String = "",
    val time: String = "",
    val mine: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class AppNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val time: String = "",
    val type: String = "info",
    val itemId: String = "",
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class FlaggedReport(
    val id: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val reason: String = "",
    val reporterName: String = "",
    val time: String = "Just now",
    val timestamp: Long = System.currentTimeMillis()
)

data class BdLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val distanceLabel: String
)

object SampleData {
    val categories = listOf(
        "Mobile",
        "Bag",
        "Wallet",
        "Keys",
        "Documents",
        "Electronics",
        "Clothes",
        "Others"
    )

    val bdLocations = listOf(
        BdLocation("Dhanmondi 27, Dhaka", 23.7516, 90.3704, "1.8 km"),
        BdLocation("University of Dhaka (TSC)", 23.7324, 90.3957, "2.4 km"),
        BdLocation("Gulshan 2 Circle, Dhaka", 23.7947, 90.4143, "4.2 km"),
        BdLocation("Mirpur 10 Roundabout, Dhaka", 23.8069, 90.3687, "5.5 km"),
        BdLocation("Uttara Sector 7, Dhaka", 23.8728, 90.3984, "9.1 km"),
        BdLocation("Banani 11, Dhaka", 23.7937, 90.4066, "3.8 km"),
        BdLocation("Motijheel Commercial Area, Dhaka", 23.7330, 90.4172, "3.2 km"),
        BdLocation("Mohammadpur Town Hall, Dhaka", 23.7658, 90.3584, "2.9 km"),
        BdLocation("Bashundhara R/A, Dhaka", 23.8191, 90.4526, "6.4 km"),
        BdLocation("Agrabad, Chattogram", 22.3264, 91.8123, "12 km"),
        BdLocation("Zindabazar, Sylhet", 24.8949, 91.8687, "15 km")
    )

    private val now = System.currentTimeMillis()

    val items = listOf(
        LostFoundItem(
            id = "item_1",
            name = "iPhone 13 (Blue Case)",
            category = "Mobile",
            isLost = false,
            place = "Dhanmondi 27, Dhaka",
            time = "2h ago",
            distance = "1.8 km",
            description = "Found an iPhone 13 with a dark blue silicone case near Dhanmondi 27 bus stand. Lock screen has a cat wallpaper.",
            contact = "Rifat Hasan",
            contactPhone = "+880 1712 345678",
            contactEmail = "rifat@example.com",
            date = "Today",
            latitude = 23.7516,
            longitude = 90.3704,
            ownerId = "u1",
            updatedAt = now - 7_200_000L
        ),
        LostFoundItem(
            id = "item_2",
            name = "Black Asus Laptop Backpack",
            category = "Bag",
            isLost = true,
            place = "University of Dhaka (TSC)",
            time = "3h ago",
            distance = "2.4 km",
            description = "Black backpack containing notebooks, charger, and a student ID card. Lost near the central library entrance.",
            contact = "Nusrat Jahan",
            contactPhone = "+880 1819 223344",
            contactEmail = "nusrat@example.com",
            date = "Today",
            latitude = 23.7324,
            longitude = 90.3957,
            ownerId = "u3",
            updatedAt = now - 10_800_000L
        ),
        LostFoundItem(
            id = "item_3",
            name = "Navy Blue Leather Wallet",
            category = "Wallet",
            isLost = true,
            place = "Gulshan 2 Circle, Dhaka",
            time = "5h ago",
            distance = "4.2 km",
            description = "Blue leather wallet containing NID card, BRAC Bank debit card, and some cash. Lost near Gulshan 2.",
            contact = "Tanvir Ahmed",
            contactPhone = "+880 1911 556677",
            contactEmail = "tanvir@example.com",
            date = "Yesterday",
            latitude = 23.7947,
            longitude = 90.4143,
            ownerId = "u4",
            updatedAt = now - 18_000_000L
        ),
        LostFoundItem(
            id = "item_4",
            name = "Bunch of 3 Keys (Red Keychain)",
            category = "Keys",
            isLost = false,
            place = "Mirpur 10 Roundabout, Dhaka",
            time = "6h ago",
            distance = "5.5 km",
            description = "Found a set of 3 house keys attached to a red leather keychain near Mirpur 10 metro station gate.",
            contact = "Arafat Rahman",
            contactPhone = "+880 1615 889900",
            contactEmail = "arafat@example.com",
            date = "Yesterday",
            latitude = 23.8069,
            longitude = 90.3687,
            ownerId = "u2",
            updatedAt = now - 21_600_000L
        ),
        LostFoundItem(
            id = "item_5",
            name = "iPhone 13 Blue",
            category = "Mobile",
            isLost = true,
            place = "Dhanmondi 27, Dhaka",
            time = "1h ago",
            distance = "1.8 km",
            description = "Lost my blue iPhone 13 with a silicone case while getting off the bus at Dhanmondi 27.",
            contact = "Arafat Rahman",
            contactPhone = "+880 1615 889900",
            contactEmail = "arafat@example.com",
            date = "Today",
            latitude = 23.7522,
            longitude = 90.3712,
            ownerId = "u2",
            updatedAt = now - 3_600_000L
        )
    )

    val users = listOf(
        User(id = "u1", name = "Rifat Hasan", email = "rifat@example.com", phone = "+880 1712 345678", role = Role.USER),
        User(id = "u2", name = "Arafat Rahman", email = "arafat@example.com", phone = "+880 1615 889900", role = Role.USER),
        User(id = "u3", name = "Nusrat Jahan", email = "nusrat@example.com", phone = "+880 1819 223344", role = Role.USER),
        User(id = "u4", name = "Tanvir Ahmed", email = "tanvir@example.com", phone = "+880 1911 556677", role = Role.USER),
        User(id = "admin_1", name = "FindBack Admin", email = "admin@findback.bd", phone = "+880 1700 000000", role = Role.ADMIN)
    )

    val initialThreads = listOf(
        ChatThread(
            id = "chat_rifat_hasan",
            participantName = "Rifat Hasan",
            participantPhone = "+880 1712 345678",
            itemId = "item_1",
            itemName = "iPhone 13 (Blue Case)",
            lastMessage = "I found it near Dhanmondi 27. Can we meet there?",
            lastTime = "10:27 AM",
            unreadCount = 1,
            updatedAt = now - 1_800_000L
        ),
        ChatThread(
            id = "chat_nusrat_jahan",
            participantName = "Nusrat Jahan",
            participantPhone = "+880 1819 223344",
            itemId = "item_2",
            itemName = "Black Asus Laptop Backpack",
            lastMessage = "Please let me know if anyone turns it in at TSC.",
            lastTime = "Yesterday",
            unreadCount = 0,
            updatedAt = now - 86_400_000L
        ),
        ChatThread(
            id = "chat_tanvir_ahmed",
            participantName = "Tanvir Ahmed",
            participantPhone = "+880 1911 556677",
            itemId = "item_3",
            itemName = "Navy Blue Leather Wallet",
            lastMessage = "Thanks for checking around Gulshan 2!",
            lastTime = "Yesterday",
            unreadCount = 0,
            updatedAt = now - 120_000_000L
        )
    )

    val messages = listOf(
        Message("m1", "chat_rifat_hasan", "Rifat Hasan", "Hi! Is this your blue iPhone 13?", "10:24 AM", false, now - 2_000_000L),
        Message("m2", "chat_rifat_hasan", "Me", "Yes! It has a blue silicone case. Where did you find it?", "10:26 AM", true, now - 1_900_000L),
        Message("m3", "chat_rifat_hasan", "Rifat Hasan", "I found it near Dhanmondi 27. Can we meet there?", "10:27 AM", false, now - 1_800_000L),
        Message("m4", "chat_nusrat_jahan", "Nusrat Jahan", "Hi, I lost my Black Asus Laptop Backpack near DU Library.", "Yesterday", false, now - 87_000_000L),
        Message("m5", "chat_nusrat_jahan", "Nusrat Jahan", "Please let me know if anyone turns it in at TSC.", "Yesterday", false, now - 86_400_000L),
        Message("m6", "chat_tanvir_ahmed", "Tanvir Ahmed", "Thanks for checking around Gulshan 2!", "Yesterday", false, now - 120_000_000L)
    )

    val notifications = listOf(
        AppNotification(
            id = "notif_1",
            title = "Possible Match Found (85% Match)",
            body = "\"iPhone 13 (Blue Case)\" found in Dhanmondi 27 matches a lost iPhone report.",
            time = "1h ago",
            type = "match",
            itemId = "item_1",
            timestamp = now - 3_600_000L
        ),
        AppNotification(
            id = "notif_2",
            title = "New Message from Rifat Hasan",
            body = "I found it near Dhanmondi 27. Can we meet there?",
            time = "2h ago",
            type = "message",
            itemId = "item_1",
            timestamp = now - 7_200_000L
        ),
        AppNotification(
            id = "notif_3",
            title = "Community Alert • Dhaka University",
            body = "Nusrat Jahan reported a lost Black Asus Laptop Backpack near TSC.",
            time = "3h ago",
            type = "status",
            itemId = "item_2",
            timestamp = now - 10_800_000L
        ),
        AppNotification(
            id = "notif_4",
            title = "Welcome to FindBack BD",
            body = "Report lost or found items, get automatic match alerts, and chat directly.",
            time = "1d ago",
            type = "welcome",
            timestamp = now - 86_400_000L
        )
    )

    val initialFlaggedReports = listOf(
        FlaggedReport(
            id = "flag_1",
            itemId = "item_3",
            itemName = "Navy Blue Leather Wallet",
            reason = "Duplicate post reported by community member",
            reporterName = "Arafat Rahman",
            time = "3h ago",
            timestamp = now - 10_800_000L
        ),
        FlaggedReport(
            id = "flag_2",
            itemId = "item_1",
            itemName = "iPhone 13 (Blue Case)",
            reason = "Caller asked for verification of IMEI before handover",
            reporterName = "Tanvir Ahmed",
            time = "5h ago",
            timestamp = now - 18_000_000L
        )
    )
}
