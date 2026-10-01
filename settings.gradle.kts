pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (System.getenv("ANDROID_FRAMEWORK") == "true") {
                when (requested.id.id) {
                    "com.android.application" -> useVersion("9.1.1")
                    "org.jetbrains.kotlin.android" -> useVersion("2.2.10")
                    "org.jetbrains.kotlin.plugin.compose" -> useVersion("2.2.10")
                    "com.google.gms.google-services" -> useVersion("4.5.0")
                    "com.google.dagger.hilt.android" -> useVersion("2.59.2")
                    "com.google.devtools.ksp" -> useVersion("2.3.5")
                }
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "FindBackBD"
include(":app")
