package com.example.writein;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WriteInApplication {

	public static void main(String[] args) {
    SpringApplication.run(WriteInApplication.class, args);
	}

}
