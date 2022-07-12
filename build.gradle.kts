import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.jlleitschuh.gradle.ktlint") version "10.0.0"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("kapt") version "1.5.30"
    kotlin("plugin.allopen") version "1.5.30"
}

group = "org.partymaker"
version = "0.0.1"

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.Embeddable")
    annotation("javax.persistence.MappedSuperclass")
}

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(15))
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.jadira.usertype:usertype.core:7.0.0.CR1")
    implementation("org.hibernate:hibernate-spatial:5.6.0.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda")
    implementation("com.vladmihalcea:hibernate-types-52:2.16.2")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.flywaydb:flyway-core")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("joda-time:joda-time:2.10.10")
    implementation("io.arrow-kt:arrow-core:1.1.2")
    implementation("com.zaxxer:HikariCP:4.0.2")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")

    kapt("io.arrow-kt:arrow-meta:0.11.0")

    compileOnly("org.springframework.boot:spring-boot-configuration-processor")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.3.1")
    testImplementation("io.kotest:kotest-assertions-core:5.3.1")
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("com.ninja-squad:springmockk:3.1.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")
}

dependencyManagement {
    imports {
        mavenBom(BOM_COORDINATES) { bomProperty("kotlin.version", "1.5.30") }
    }
}

tasks.bootJar {
    mainClass.set("org.partymaker.matchmaker.MatchmakerApplication")
    archiveFileName.set("matchmaker.jar")
}

tasks.jar {
    archiveClassifier.set("") // Spring Boot Gradle Plugin: make executable archive
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=all")
        jvmTarget = "15"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

configure<KtlintExtension> {
    ignoreFailures.set(false)
    disabledRules.set(setOf("no-wildcard-imports"))
    reporters {
        reporter(PLAIN)
        reporter(CHECKSTYLE)
    }
}
