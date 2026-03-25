package com.studyroom.smartstudy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling

public class SmartstudyApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartstudyApplication.class, args);
	}

}
