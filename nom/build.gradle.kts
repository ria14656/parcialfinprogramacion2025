// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// Guarda los builds fuera de OneDrive (en C:\Android\build)
// (El warning de 'buildDir' deprecado puede ignorarse por ahora)
subprojects {
    buildDir = file("C:/Android/build/${rootProject.name}/${project.path.replace(":", "_")}")
}

