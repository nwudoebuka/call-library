plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android") version "2.48" apply false
    `maven-publish`
}

buildscript {
   // ext.kotlin_version = "1.7.20"
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
        mavenLocal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.48.1")
    }
}
android {
    namespace = "com.appcapital.call_library"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            consumerProguardFiles("proguard-rules.pro")
            consumerProguardFiles("consumer-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17)) // << --- ADD This
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

}


afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.nwudoebuka"
                artifactId = "call-library"
                version = "2.0.7"
                pom {
                    description = "First release"
                }
                repositories {
                    mavenLocal()
                }
               // artifact("$buildDir/outputs/aar/call-library-release.aar")
            }
        }
    }
}


//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = "com.github.nwudoebuka"
//            artifactId = "call-library"
//            version = "1.0.3"
//            pom {
//                description.set("First version release")
//            }
//        }
//    }
//
//    repositories {
//        mavenLocal()
//    }
//}
kapt {
    correctErrorTypes = true
    useBuildCache = true
}
dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
    //APPLOVIN
    implementation("com.applovin:applovin-sdk:12.4.2")
    implementation("com.google.android.gms:play-services-ads-identifier:18.0.1")
    //AD MOB
    implementation("com.google.android.gms:play-services-ads:23.0.0")
    //NETWORK
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.2")
    //DI
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    kapt("androidx.hilt:hilt-compiler:1.0.0")
    kapt("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.4.2")
    implementation("com.squareup:javapoet:1.8.0")
    //Location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    // NETWORK IMAGE
    implementation("com.github.bumptech.glide:glide:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")
}
apply(plugin = "maven-publish")
apply(plugin = "com.google.dagger.hilt.android")