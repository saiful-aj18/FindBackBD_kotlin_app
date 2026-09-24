
# 🔎 FindBack BD

**FindBack BD** is a Kotlin-based Android application designed to help users report, search, match, and recover lost and found belongings in Bangladesh.

The application is built using **Kotlin, Jetpack Compose, Firebase, MVVM, Repository Pattern, Room Database, Google Maps, Camera/Gallery integration, and Firebase Cloud Messaging**.

---

## 📌 Project Overview

Losing personal belongings in buses, universities, shopping malls, hospitals, markets, railway stations, and other public places is a common problem.

FindBack BD provides a centralized platform where users can:

- Report lost items
- Report found items
- Search and filter reports
- Upload item images
- Share item location
- View items on Google Maps
- Find possible matches
- Communicate with other users
- Receive notifications
- Manage their profile
- Recover lost belongings

---

# 🚀 Technologies Used

| Technology | Purpose |
|---|---|
| Kotlin | Main programming language |
| Jetpack Compose | Modern Android UI |
| Material 3 | UI components and design |
| Firebase Authentication | User authentication |
| Firebase Firestore | Cloud database |
| Firebase Storage | Image/file storage |
| Firebase Cloud Messaging | Push notifications |
| Google Maps | Location and map functionality |
| Room Database | Local/offline database |
| Retrofit | REST API communication |
| MVVM | Application architecture |
| StateFlow | Reactive UI state management |
| Hilt | Dependency Injection |
| CameraX | Camera integration |
| Coil | Image loading |
| Git & GitHub | Version control |

---

# 📋 Main Features

## 👤 Authentication

- User Registration
- User Login
- Logout
- Forgot Password
- Password Reset
- Authentication State Management
- Role-Based Access Control

## 📦 Lost & Found Reports

- Create Lost Report
- Create Found Report
- Edit Report
- Delete Report
- View Report Details
- Upload Item Images
- Add Item Description
- Add Category
- Add Date and Time
- Add Location
- Report Status Management

## 🔎 Search & Filter

Users can search and filter reports based on:

- Item name
- Category
- Location
- Date
- Lost/Found status

## 🗺️ Google Maps

- Current location
- Item location
- Map markers
- Location-based item discovery

## 🤖 Smart Matching

FindBack BD can identify possible matches between lost and found reports using information such as:

- Category
- Item name
- Color
- Description
- Location
- Date

Example:

```text
Lost Item:
Black Xiaomi Smartphone
Location: GEC Circle
Date: 15 August

Found Item:
Black Xiaomi Smartphone
Location: GEC Circle
Date: 15 August

Possible Match: 92%
````

## 💬 Chat

* Chat between users
* Send messages
* Real-time message updates
* Chat history

## 🔔 Notifications

Users can receive notifications for:

* Possible Matches
* New Messages
* Report Status Updates
* Other application activities

## 👤 Profile

* View Profile
* Edit Profile
* Profile Image
* User Reports
* User Statistics
* Rating/Review information

## 🛡️ Admin Panel

Administrators can manage:

* Users
* Reports
* Reported Content
* Application activities
* Moderation tasks

---

# 🏗️ Architecture

FindBack BD follows the **MVVM + Repository Pattern** architecture.

```text
                    ┌─────────────────────┐
                    │     UI / Compose    │
                    │       Screens       │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │      ViewModel      │
                    │  StateFlow / Events │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │     Repository      │
                    └──────────┬──────────┘
                               │
                    ┌──────────┴──────────┐
                    ▼                     ▼
          ┌─────────────────┐    ┌─────────────────┐
          │     Firebase    │    │    Local Room   │
          │                 │    │                 │
          │ Auth            │    │ Local Database  │
          │ Firestore       │    │ Cache           │
          │ Storage         │    │                 │
          │ FCM             │    │                 │
          └─────────────────┘    └─────────────────┘
```

---

# 📁 Project Structure

```text
FindBackBD/
│
├── app/
│   ├── google-services.json
│   │
│   └── src/
│       └── main/
│           │
│           ├── java/
│           │   └── com/
│           │       └── saiful/
│           │           └── findbackbd/
│           │
│           │               ├── MainActivity.kt
│           │               │
│           │               ├── data/
│           │               │   ├── model/
│           │               │   │   ├── User.kt
│           │               │   │   ├── LostFoundItem.kt
│           │               │   │   ├── Category.kt
│           │               │   │   ├── Message.kt
│           │               │   │   ├── Notification.kt
│           │               │   │   ├── Comment.kt
│           │               │   │   └── Review.kt
│           │               │   │
│           │               │   ├── repository/
│           │               │   │   ├── AuthRepository.kt
│           │               │   │   ├── ItemRepository.kt
│           │               │   │   ├── ChatRepository.kt
│           │               │   │   ├── UserRepository.kt
│           │               │   │   └── NotificationRepository.kt
│           │               │   │
│           │               │   ├── remote/
│           │               │   │   ├── FirebaseAuthService.kt
│           │               │   │   ├── FirestoreService.kt
│           │               │   │   ├── StorageService.kt
│           │               │   │   └── FcmService.kt
│           │               │   │
│           │               │   └── local/
│           │               │       ├── AppDatabase.kt
│           │               │       ├── ItemDao.kt
│           │               │       └── ItemEntity.kt
│           │               │
│           │               ├── ui/
│           │               │   ├── theme/
│           │               │   │   ├── Color.kt
│           │               │   │   ├── Theme.kt
│           │               │   │   └── Type.kt
│           │               │   │
│           │               │   ├── components/
│           │               │   │   ├── AppButton.kt
│           │               │   │   ├── AppTextField.kt
│           │               │   │   ├── ItemCard.kt
│           │               │   │   ├── CategoryCard.kt
│           │               │   │   ├── LoadingView.kt
│           │               │   │   ├── EmptyView.kt
│           │               │   │   └── BottomNavigationBar.kt
│           │               │   │
│           │               │   ├── navigation/
│           │               │   │   ├── AppNavigation.kt
│           │               │   │   └── Screen.kt
│           │               │   │
│           │               │   └── screens/
│           │               │       ├── splash/
│           │               │       │   └── SplashScreen.kt
│           │               │       │
│           │               │       ├── onboarding/
│           │               │       │   └── OnboardingScreen.kt
│           │               │       │
│           │               │       ├── auth/
│           │               │       │   ├── LoginScreen.kt
│           │               │       │   ├── RegisterScreen.kt
│           │               │       │   ├── ForgotPasswordScreen.kt
│           │               │       │   └── AuthViewModel.kt
│           │               │       │
│           │               │       ├── home/
│           │               │       │   ├── HomeScreen.kt
│           │               │       │   └── HomeViewModel.kt
│           │               │       │
│           │               │       ├── search/
│           │               │       │   ├── SearchScreen.kt
│           │               │       │   └── SearchViewModel.kt
│           │               │       │
│           │               │       ├── report/
│           │               │       │   ├── CreateReportScreen.kt
│           │               │       │   ├── EditReportScreen.kt
│           │               │       │   └── ReportViewModel.kt
│           │               │       │
│           │               │       ├── details/
│           │               │       │   ├── ItemDetailsScreen.kt
│           │               │       │   └── ItemDetailsViewModel.kt
│           │               │       │
│           │               │       ├── map/
│           │               │       │   ├── MapScreen.kt
│           │               │       │   └── MapViewModel.kt
│           │               │       │
│           │               │       ├── chat/
│           │               │       │   ├── ChatListScreen.kt
│           │               │       │   ├── ChatScreen.kt
│           │               │       │   └── ChatViewModel.kt
│           │               │       │
│           │               │       ├── notification/
│           │               │       │   ├── NotificationScreen.kt
│           │               │       │   └── NotificationViewModel.kt
│           │               │       │
│           │               │       ├── profile/
│           │               │       │   ├── ProfileScreen.kt
│           │               │       │   ├── EditProfileScreen.kt
│           │               │       │   └── ProfileViewModel.kt
│           │               │       │
│           │               │       ├── settings/
│           │               │       │   └── SettingsScreen.kt
│           │               │       │
│           │               │       └── admin/
│           │               │           ├── AdminDashboardScreen.kt
│           │               │           ├── ManageReportsScreen.kt
│           │               │           ├── ManageUsersScreen.kt
│           │               │           ├── ReportedContentScreen.kt
│           │               │           └── AdminViewModel.kt
│           │               │
│           │               ├── viewmodel/
│           │               │   └── AppViewModel.kt
│           │               │
│           │               ├── utils/
│           │               │   ├── Constants.kt
│           │               │   ├── ValidationUtils.kt
│           │               │   ├── DateUtils.kt
│           │               │   └── PermissionUtils.kt
│           │               │
│           │               └── di/
│           │                   └── AppModule.kt
│           │
│           ├── res/
│           │   ├── drawable/
│           │   ├── mipmap/
│           │   └── values/
│           │       ├── strings.xml
│           │       └── themes.xml
│           │
│           └── AndroidManifest.xml
│
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── firestore.rules
├── storage.rules
├── .gitignore
└── README.md
```

---

# 🔥 Firebase Configuration

Before running the application, configure Firebase.

## 1. Create Firebase Project

Create a Firebase project named:

```text
FindBackBD
```

---

## 2. Register Android Application

Register the Android application using the following package name:

```text
com.saiful.findbackbd
```

---

## 3. Download `google-services.json`

Download the Firebase configuration file and place it here:

```text
FindBackBD/
└── app/
    └── google-services.json
```

---

## 4. Enable Firebase Authentication

Go to:

```text
Firebase Console
→ Authentication
→ Sign-in method
→ Email/Password
```

Enable:

```text
Email/Password
```

---

## 5. Create Firestore Database

Go to:

```text
Firebase Console
→ Firestore Database
→ Create Database
```

For initial setup, create the database in **Locked Mode**.

The project includes:

```text
firestore.rules
```

which can be used to configure Firestore security rules.

---

## 6. Enable Firebase Storage

Go to:

```text
Firebase Console
→ Storage
→ Get Started
```

The application uses Firebase Storage for:

* Item images
* Profile images
* Other uploaded media

The project includes:

```text
storage.rules
```

for Storage security configuration.

---

## 7. Firebase Cloud Messaging

Firebase Cloud Messaging is used for push notifications such as:

* Possible Match
* New Message
* Report Status Update
* Application Notifications

---

# 🗺️ Google Maps Configuration

FindBack BD uses Google Maps for location-based features.

## Step 1 — Enable Maps SDK

Go to:

```text
Google Cloud Console
→ APIs & Services
→ Library
```

Enable:

```text
Maps SDK for Android
```

---

## Step 2 — Create API Key

Go to:

```text
Google Cloud Console
→ APIs & Services
→ Credentials
→ Create Credentials
→ API Key
```

Create a Google Maps API key.

---

## Step 3 — Create `local.properties`

Create the following file in the project root:

```text
FindBackBD/
├── app/
├── local.properties
├── build.gradle.kts
└── settings.gradle.kts
```

Add:

```properties
sdk.dir=C:\\Users\\YOUR_USERNAME\\AppData\\Local\\Android\\Sdk
MAPS_API_KEY=YOUR_REAL_MAPS_API_KEY
```

Replace:

```text
YOUR_USERNAME
```

and:

```text
YOUR_REAL_MAPS_API_KEY
```

with your own values.

### Important

Do not commit `local.properties` to GitHub.

---

# 🔐 Security

Never commit the following files or credentials to a public GitHub repository:

```text
local.properties
google-services.json
*.jks
*.keystore
service-account.json
private API keys
passwords
```

The `.gitignore` file should prevent sensitive local configuration files from being committed.

---

# ▶️ Running the Project

## Step 1

Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/FindBackBD.git
```

## Step 2

Open the project in Android Studio.

Open:

```text
FindBackBD/
```

Do not open only the `app` folder.

## Step 3

Add Firebase configuration:

```text
FindBackBD/
└── app/
    └── google-services.json
```

## Step 4

Create:

```text
FindBackBD/
└── local.properties
```

and add your Android SDK path and Maps API key.

## Step 5

Sync Gradle:

```text
Android Studio
→ File
→ Sync Project with Gradle Files
```

## Step 6

Connect an Android device or start an Android Emulator.

## Step 7

Run:

```text
Run ▶
```

---

# 🧪 Development Components

The application is organized into several major modules.

```text
Authentication
      ↓
Home
      ↓
Search / Filter
      ↓
Lost & Found Reports
      ↓
Item Details
      ↓
Google Maps
      ↓
Smart Matching
      ↓
Chat
      ↓
Notifications
      ↓
Profile
      ↓
Admin Panel
```

---

# 📊 Data Structure

The main Firestore collections include:

```text
users
items
chats
notifications
comments
reviews
```

### Users

Stores user information and role.

```text
users/{userId}
```

### Items

Stores lost and found reports.

```text
items/{itemId}
```

### Chats

Stores conversations and messages.

```text
chats/{chatId}
```

### Notifications

Stores application notifications.

```text
notifications/{notificationId}
```

### Comments

Stores comments related to reports.

```text
comments/{commentId}
```

### Reviews

Stores user ratings and reviews.

```text
reviews/{reviewId}
```

---

# 🛡️ User Roles

FindBack BD supports role-based access control.

## User

Regular users can:

* Create reports
* Edit their reports
* Delete their reports
* Search reports
* View item details
* Contact other users
* Chat
* Manage their profile

## Admin

Administrators can:

* Manage users
* Manage reports
* Review reported content
* Moderate application data
* Monitor application activities

---

# 🔄 Application Flow

```text
                    Start
                      │
                      ▼
                   Splash
                      │
                      ▼
                 Onboarding
                      │
                      ▼
              Login / Register
                      │
                      ▼
                    Home
                      │
          ┌───────────┼────────────┐
          ▼           ▼            ▼
       Search      Create        Profile
          │         Report          │
          ▼           │             ▼
      Item List       ▼          Settings
          │       Firestore
          ▼
     Item Details
          │
     ┌────┴─────┐
     ▼          ▼
    Map        Chat
     │          │
     └────┬─────┘
          ▼
    Possible Match
          │
          ▼
     Item Recovery
```

---

# 🎯 Project Goal

The main goal of FindBack BD is to provide a centralized and user-friendly platform for recovering lost belongings.

The application combines:

* Mobile technology
* Cloud database
* Location services
* Image upload
* Search and filtering
* Smart matching
* Real-time communication
* Push notifications
* Secure authentication

to create a practical Lost & Found solution for Bangladesh.

---

# 🔮 Future Scope

Future versions of FindBack BD can include:

* AI-based image matching
* Bangla and English language support
* Verified university/public organization accounts
* QR-based item identification
* Secure handover verification
* Improved location-based matching
* Advanced notification system
* More powerful moderation tools

---

# 👨‍💻 Development

**Project:** FindBack BD
**Platform:** Android
**Language:** Kotlin
**UI:** Jetpack Compose
**Architecture:** MVVM + Repository Pattern
**Backend:** Firebase
**Database:** Firestore + Room
**Maps:** Google Maps
**Version Control:** Git & GitHub

---

# 📄 License

This project is developed for educational and academic purposes.

```

```
