package org.venus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the Venus application.
 *
 * This is the entry point for the Spring Boot application.
 */
@SpringBootApplication
public class VenusApplication {
    /**
     * Entry point for the Venus application.
     *
     * This method initializes and runs the Spring Boot application.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(VenusApplication.class, args);
    }
}
