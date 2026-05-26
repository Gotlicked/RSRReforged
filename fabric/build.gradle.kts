plugins {
    id("net.fabricmc.fabric-loom")
    id("java-library")
    id("maven-publish")
}

base {
    archivesName = "${property("mod_id")}-${project.name}-${property("minecraft_version")}-${property("mod_version")}"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(property("java_version").toString())
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    maven {
        url = uri ("https://mvnrepository.com/")
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Fabric"
                url = uri("https://maven.fabricmc.net")
            }
        }
        filter {
            includeGroupAndSubgroups("net.fabricmc")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                url = uri ("https://maven.creeperhost.net")
            }
        }
        filter {
            includeGroupAndSubgroups("com.refinedmods")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Terraformers"
                url = uri ("https://maven.terraformersmc.com")
            }
        }
        filter {
            includeGroupAndSubgroups("com.terraformersmc")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                url = uri ("https://maven.blamejared.com")
            }
        }
        filter {
            includeGroupAndSubgroups("mezz.jei")
        }
    }
    exclusiveContent {
        forRepository {
            maven {
                url = uri ("https://maven.shedaniel.me")
            }
        }
        filter {
            includeGroupAndSubgroups("me.shedaniel")
        }
    }
}

val commonJava: Configuration by configurations.creating
val commonResources: Configuration by configurations.creating

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

dependencies {
    implementation("org.apiguardian:apiguardian-api:1.1.2")
    implementation("com.refinedmods.refinedstorage:refinedstorage-fabric:${property("rs_version")}")
    runtimeOnly("me.shedaniel.cloth:cloth-config-fabric:${property("cloth_config_version")}")
    api("mezz.jei:jei-${property("minecraft_version")}-fabric-api:${property("jei_version")}")
    runtimeOnly("mezz.jei:jei-${property("minecraft_version")}-fabric:${property("jei_version")}")
    runtimeOnly("com.terraformersmc:modmenu:${property("modmenu_version")}")
    add("commonJava",      project(path = ":common", configuration = "commonJava"))
    add("commonResources", project(path = ":common", configuration = "commonResources"))
    compileOnly(project(":common"))
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    implementation("net.fabricmc:fabric-loader:${property("fabric_loader_version")}")
    api("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
    implementation("net.fabricmc:fabric-language-kotlin:${property("fabric_language_kotlin_version")}")
}

loom {
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            appendProjectPathToConfigName = false
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            appendProjectPathToConfigName = false
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
}

tasks.named<JavaCompile>("compileJava") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<ProcessResources>("processResources") {
    dependsOn(commonResources)
    from(commonResources)
    filesMatching(listOf(
        "pack.mcmeta",
        "fabric.mod.json",
        "*.mixins.json",
    )) {
        expand(project.properties)
    }
    from("src/main/templates")
}

tasks.named<Javadoc>("javadoc") {
    dependsOn(commonJava)
    source(commonJava)
}

tasks.named<Jar>("sourcesJar") {
    dependsOn(commonJava, commonResources)
    from(commonJava)
    from(commonResources)
}

tasks.named<Jar>("jar") {
    dependsOn(commonJava, commonResources)
    from(commonJava)
    from(commonResources)
}

tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri(
                System.getenv("local_maven_url")
                    ?: layout.buildDirectory.dir("repo").get().asFile.toURI().toString()
            )
        }
    }
}
