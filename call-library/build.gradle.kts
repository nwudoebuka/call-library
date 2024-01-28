plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    `maven-publish`
}

buildscript {
   // ext.kotlin_version = "1.7.20"
    repositories {
        google()
        mavenCentral()
        mavenLocal() // << --- ADD This
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.3")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17)) // << --- ADD This
        }
    }
    java {
        sourceCompatibility = JavaVersion.VERSION_17 // << --- ADD This
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
                version = "1.0.4"
                pom {
                    description = "First release"
                }
                repositories {
                    mavenLocal()
                }
                artifact("$buildDir/outputs/aar/call-library-release.aar")
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

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
apply(plugin = "maven-publish")