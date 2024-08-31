plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.graalvm.buildtools.native") version "0.10.2"
}

group = "org.venus"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-pool2:2.12.0")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.3.3")
    implementation("org.springframework.boot:spring-boot-starter-data-redis:3.3.3")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.springframework.boot:spring-boot-starter-cache:3.3.3")
    implementation("io.micrometer:micrometer-registry-jmx:1.13.3")
    implementation("io.micrometer:micrometer-registry-prometheus:1.13.3")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.3")
    implementation("ch.qos.logback:logback-classic:1.5.7")
    implementation("mysql:mysql-connector-java:8.0.32")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.3")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.3.3")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
