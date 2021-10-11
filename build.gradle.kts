plugins {
    java
    idea
    `maven-publish`
}

allprojects {
    group = "de.exlll"
    version = "3.4.0"
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "maven-publish")

    java.sourceCompatibility = JavaVersion.VERSION_11
    java.targetCompatibility = JavaVersion.VERSION_11

    repositories {
        mavenCentral()
        maven { url = uri("https://maven.pkg.github.com/Exlll/ConfigLib") }
        maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    }

    dependencies {
        compileOnly("de.exlll:configlib-core:2.2.0")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
        testImplementation("org.mockito:mockito-core:3.6.28")
        testImplementation("org.hamcrest:hamcrest:2.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    }

    publishing {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/Exlll/DatabaseLib")
                credentials {
                    username = System.getenv("GITHUB_ACTOR")
                    password = System.getenv("GITHUB_TOKEN")
                }
            }
            publications {
                register<MavenPublication>("gpr") {
                    from(components["java"])
                }
            }
        }
    }
}

project(":databaselib-core") {
    dependencies {
        implementation("com.zaxxer:HikariCP:3.4.5")
    }
}
project(":databaselib-bukkit") {
    repositories {
        maven(url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/"))
    }
    dependencies {
        compileOnly("org.spigotmc:spigot-api:1.16.4-R0.1-SNAPSHOT")
    }
}
project(":databaselib-bungee") {
    repositories {
        maven(url = uri("https://oss.sonatype.org/content/repositories/snapshots/"))
    }
    dependencies {
        compileOnly("net.md-5:bungeecord-api:1.16-R0.3")
    }
}

configure(listOf(project(":databaselib-bukkit"), project(":databaselib-bungee"))) {
    dependencies {
        implementation(project(":databaselib-core"))
    }
    tasks.jar {
        from(project(":databaselib-core").sourceSets["main"].output)
    }
}