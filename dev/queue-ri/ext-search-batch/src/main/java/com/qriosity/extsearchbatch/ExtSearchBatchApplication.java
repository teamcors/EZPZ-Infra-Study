package com.qriosity.extsearchbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExtSearchBatchApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExtSearchBatchApplication.class, args);
	}

}
