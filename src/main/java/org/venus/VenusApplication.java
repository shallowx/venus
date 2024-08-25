package org.venus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class VenusApplication {

	public static void main(String[] args) {
		SpringApplication.run(VenusApplication.class, args);
	}

}
