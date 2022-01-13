import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.compose") version "0.4.0-build190"
    kotlin("plugin.serialization") version "1.4.32"
}

group = "com.example.hiker"
version = "1.0"

repositories {
    jcenter()
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(group = "uk.co.caprica", name = "vlcj", version = "4.7.1")
    implementation("br.com.devsrsouza.compose.icons.jetbrains:font-awesome:0.2.0")
    implementation("br.com.devsrsouza.compose.icons.jetbrains:eva-icons:0.2.0")
    implementation("br.com.devsrsouza.compose.icons.jetbrains:tabler-icons:0.2.0")
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    //event
    implementation("org.greenrobot:eventbus:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-runtime:1.0-M1-1.4.0-rc")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.4.32")
    implementation("org.jooq:joor-java-8:0.9.14")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "15"
}
val libraryPath = "vlc"

compose.desktop {
    application {
        mainClass = "MainKt"
        fromFiles(project.fileTree("vlc"))
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AO Player"
            packageVersion = "1.0.9"

            val iconsRoot = project.file("src/main/resources/images")
            macOS {
                iconFile.set(iconsRoot.resolve("icon-mac.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("icon-windows.ico"))
                menuGroup = "XiaoMianAo"
                exePackageVersion = "1.2.8"
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "18159995-d967-4CD2-8885-77BFA97CFA9F"
                shortcut = true
            }
            linux {
                iconFile.set(iconsRoot.resolve("icon-linux.png"))
                shortcut = true
            }
        }
    }
}