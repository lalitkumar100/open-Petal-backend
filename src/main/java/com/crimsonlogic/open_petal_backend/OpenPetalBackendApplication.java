package com.crimsonlogic.open_petal_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpenPetalBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(OpenPetalBackendApplication.class, args);
	}

}
