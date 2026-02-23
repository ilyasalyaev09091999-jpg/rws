plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    java
}

group = "com.route.api"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // ModelMapper
    implementation("org.modelmapper:modelmapper:3.2.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    runtimeOnly("org.postgresql:postgresql")

    implementation("org.flywaydb:flyway-core")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("redis.clients:jedis")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // gRPC
    implementation(project(":route-proto"))
    implementation(project(":refdata-proto"))
    implementation("io.grpc:grpc-netty-shaded:1.75.0")
    implementation("net.devh:grpc-server-spring-boot-starter:2.15.0.RELEASE")
}