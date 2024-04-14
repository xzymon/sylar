package com.xzymon.sylar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SylarApplication {

	public static void main(String[] args) {
		SpringApplication.run(SylarApplication.class, args);
	}

}
