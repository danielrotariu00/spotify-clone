package com.spotify.idm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class IdmApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdmApplication.class, args);
	}
}
