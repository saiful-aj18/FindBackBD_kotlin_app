# FindBack BD — New PC Setup

## 1. Install
- Android Studio
- Git
- Android SDK / Platform Tools

## 2. Open
Open `FindBackBD` in Android Studio and allow Gradle sync.

## 3. Firebase
Firebase Console → create/select the FindBack BD project → add Android app with package `com.saiful.findbackbd` → download `google-services.json` → copy it to `FindBackBD\app\google-services.json`.

Enable Email/Password Authentication, Firestore, Storage and Cloud Messaging as needed.

## 4. Google Maps
Create a Maps API key in Google Cloud. Add this line to the local PC's `local.properties`:

```properties
MAPS_API_KEY=YOUR_REAL_MAPS_KEY
```

Do not commit `local.properties`. Restrict the Maps key to the Android app and the APIs actually used.

## 5. Firestore / Storage rules
Copy the contents of `firestore.rules` and `storage.rules` into the corresponding Firebase Console Rules pages and publish after reviewing them.

## 6. Run
Build → Make Project → Run.

## 7. First test
Register → Login → Home → Create Report → Firestore → Search → Details → Logout.

## 8. Git
```bash
git add .
git commit -m "setup: configure FindBack BD"
git push
```

## Important
This pack does not contain your private Firebase configuration or real Maps API key. Those are environment/project-specific.
