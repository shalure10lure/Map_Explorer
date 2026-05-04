import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)

    //app nav
    alias(libs.plugins.kotlinSerialization)
    //room
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    //firebase
    alias(libs.plugins.google.gms.google.services)

}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.ktor.client.okhttp)

            //room
            implementation(libs.androidx.room.sqlite.wrapper)

            //firebase
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.config)
            implementation(libs.firebase.database)
            implementation(libs.kotlinx.coroutines.play.services)

            implementation(libs.firebase.messaging)
            //segundo plano
            implementation(libs.androidx.work.runtime.ktx)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            //koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            //app nav
            implementation(libs.navigation.compose)
            implementation(libs.kotlinx.serialization.json)

            //ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            //room
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.ucb.mapexplorer"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.ucb.mapexplorer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.firebase.database)
    implementation(libs.kotlinx.datetime)
    implementation(libs.firebase.config)
    implementation(libs.firebase.messaging)

    debugImplementation(libs.compose.uiTooling)

    //room
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

//room
room {
    schemaDirectory("$projectDir/schemas")
}