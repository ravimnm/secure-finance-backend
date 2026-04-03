package com.finance.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@SpringBootApplication
public class SecureAccessControlServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecureAccessControlServiceApplication.class, args);
	}
	
}
