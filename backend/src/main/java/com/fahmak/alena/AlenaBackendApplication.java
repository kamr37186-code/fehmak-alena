package com.fahmak.alena;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AlenaBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlenaBackendApplication.class, args);
	}

}
