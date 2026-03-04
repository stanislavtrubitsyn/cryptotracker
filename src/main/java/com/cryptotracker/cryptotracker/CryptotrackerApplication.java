package com.cryptotracker.cryptotracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CryptotrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptotrackerApplication.class, args);
	}

	
}
